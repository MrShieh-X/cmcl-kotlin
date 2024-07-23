/*
 * Console Minecraft Launcher (Kotlin)
 * Copyright (C) 2021-2024  MrShiehX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package com.mrshiehx.cmcl.modules.modpack

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.exceptions.MissingElementException
import com.mrshiehx.cmcl.functions.mod.ModpackFunction
import com.mrshiehx.cmcl.modules.extra.fabric.FabricMerger
import com.mrshiehx.cmcl.modules.extra.forge.ForgeMerger
import com.mrshiehx.cmcl.modules.extra.quilt.QuiltMerger
import com.mrshiehx.cmcl.modules.version.VersionInstaller
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.console.TextProgress
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.internet.ThreadsDownloader
import com.mrshiehx.cmcl.utils.json.XJSONObject
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object CurseForgeModpackInstaller {
    @Throws(ExceptionWithDescription::class, IOException::class, ModpackFunction.NotValidModPackFormat::class)
    fun installCurseForgeModpack(
        manifest: JSONObject,
        zipFile: ZipFile,
        modPackFile: File,
        versionDir: File,
        keepFile: Boolean,
        installAssets: Boolean,
        installNatives: Boolean,
        installLibraries: Boolean,
        threadCount: Int
    ) {
        val overrides = NetworkUtils.addSlashIfMissing(manifest.optString("overrides"))
        zipFile.stream().forEach { zipEntry: ZipEntry ->
            if (!zipEntry.name.startsWith(overrides)) return@forEach
            val to = File(versionDir, zipEntry.name.substring(overrides.length))
            if (zipEntry.isDirectory) {
                to.mkdirs()
            } else {
                try {
                    FileUtils.inputStream2File(zipFile.getInputStream(zipEntry), to)
                } catch (e: IOException) {
                    println(CMCL.getString("MESSAGE_FAILED_TO_DECOMPRESS_FILE", zipEntry.name, e))
                }
            }
        }
        val minecraft: JSONObject = manifest.optJSONObject("minecraft")
            ?: throw ModpackFunction.NotValidModPackFormat(MissingElementException("minecraft", "JSONObject").message)
        val minecraftVersion: String = minecraft.optString("version")
        if (Utils.isEmpty(minecraftVersion)) {
            throw ModpackFunction.NotValidModPackFormat(MissingElementException("version", "String").message)
        }
        var modLoader: String? = null
        val modLoaders: JSONArray? = minecraft.optJSONArray("modLoaders")
        if (modLoaders != null && modLoaders.length() > 0) {
            for (m in modLoaders) {
                if (m is JSONObject) {
                    modLoader = m.optString("id")
                }
            }
        }
        var installForgeOrFabricOrQuilt: VersionInstaller.InstallForgeOrFabricOrQuilt? = null
        val modLoaderVersion: String
        var mergerForFabric: VersionInstaller.Merger? = null
        var mergerForQuilt: VersionInstaller.Merger? = null
        var mergerForForge: VersionInstaller.Merger? = null
        if (!Utils.isEmpty(modLoader)) {
            if (modLoader.startsWith("forge-") && modLoader.length > 6) {
                installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.FORGE
                modLoaderVersion = modLoader.substring(6)
                mergerForForge =
                    VersionInstaller.Merger { minecraftVersion12: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                        val forges = try {
                            ForgeMerger.getInstallableForges(minecraftVersion12)
                        } catch (e: Exception) {
                            println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                            FileUtils.deleteDirectory(versionDir)
                            return@Merger Pair(false, null)
                        }
                        val forge: JSONObject = forges[modLoaderVersion] ?: run {
                            println(
                                CMCL.getString(
                                    "EXCEPTION_INSTALL_MODPACK", CMCL.getString(
                                        "INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION",
                                        modLoaderVersion
                                    ).replace("\${NAME}", "Forge")
                                )
                            )
                            FileUtils.deleteDirectory(versionDir)
                            return@Merger Pair(false, null)
                        }
                        try {
                            //System.out.println(getString("MESSAGE_START_INSTALLING_FORGE"));
                            return@Merger ForgeMerger.installInternal(
                                forge,
                                headJSONObject,
                                minecraftVersion12,
                                minecraftJarFile
                            )
                        } catch (e: Exception) {
                            println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                            FileUtils.deleteDirectory(versionDir)
                            return@Merger Pair(false, null)
                        }
                    }
            } else if (modLoader.startsWith("fabric-") && modLoader.length > 7) {
                installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.FABRIC
                modLoaderVersion = modLoader.substring(7)
                mergerForFabric =
                    VersionInstaller.Merger { minecraftVersion1: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                        try {
                            return@Merger FabricMerger.installInternal(
                                minecraftVersion1,
                                modLoaderVersion,
                                headJSONObject
                            )
                        } catch (e: Exception) {
                            println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                            FileUtils.deleteDirectory(versionDir)
                            return@Merger Pair(false, null)
                        }
                    }
            } else if (modLoader.startsWith("quilt-") && modLoader.length > 6) {
                installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.QUILT
                modLoaderVersion = modLoader.substring(6)
                mergerForQuilt =
                    VersionInstaller.Merger { minecraftVersion1: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                        try {
                            return@Merger QuiltMerger.installInternal(
                                minecraftVersion1,
                                modLoaderVersion,
                                headJSONObject
                            )
                        } catch (e: Exception) {
                            println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                            FileUtils.deleteDirectory(versionDir)
                            return@Merger Pair(false, null)
                        }
                    }
            } else if (modLoader.startsWith("neoforge-") && modLoader.length > 9) {
                println(CMCL.getString("MESSAGE_INSTALL_MODPACK_NOT_SUPPORTED_NEOFORGE"))
                return
            }
        }
        val onFinished = {
            val filesArray: JSONArray? = manifest.optJSONArray("files")
            val files: MutableList<Pair<String, File>> = LinkedList()
            if (filesArray != null && filesArray.length() > 0) {
                print(CMCL.getString("INSTALL_MODPACK_EACH_MOD_GET_URL"))
                val textProgress = TextProgress()
                textProgress.maximum = filesArray.length()
                for (i in 0 until filesArray.length()) {
                    val file = filesArray.get(i) as? JSONObject ?: continue
                    val fileID: Int = file.optInt("fileID")
                    val projectID: Int = file.optInt("projectID")
                    if (fileID == 0 || projectID == 0) continue
                    try {
                        var fileName: String
                        var fileDownloadUrl: String
                        try {
                            val url = "https://api.curseforge.com/v1/mods/$projectID/files/$fileID"
                            val jsonObject: JSONObject =
                                JSONObject(NetworkUtils.curseForgeGet(url)).optJSONObject("data")
                            fileName = jsonObject.optString("fileName")
                            fileDownloadUrl = jsonObject.optString("downloadUrl")
                            if (Utils.isEmpty(fileDownloadUrl)) {
                                fileDownloadUrl =
                                    "https://edge.forgecdn.net/files/${fileID / 1000}/${fileID % 1000}/${fileName}"

                            }
                        } catch (e2: Exception) {
                            println(CMCL.getString("INSTALL_MODPACK_FAILED_DOWNLOAD_MOD", projectID, e2))
                            continue
                        }
                        file.put("fileName", fileName)
                        file.put("url", fileDownloadUrl)
                        files.add(Pair(fileDownloadUrl, File(versionDir, "mods/$fileName")))
                        textProgress.value = i + 1
                    } catch (e: Exception) {
                        println(CMCL.getString("INSTALL_MODPACK_FAILED_DOWNLOAD_MOD", projectID, e))
                    }
                }
                textProgress.value = filesArray.length()
            }
            try {
                FileUtils.writeFile(File(versionDir, "modpack.json"), manifest.toString(2), false)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val onDownloaded = {
                if (!keepFile) {
                    modPackFile.delete()
                }
                println(CMCL.getString("INSTALL_MODPACK_COMPLETE"))
            }
            Utils.close(zipFile)
            if (files.size > 0) {
                val threadsDownloader = ThreadsDownloader(files, onDownloaded, true)
                threadsDownloader.start()
            } else {
                onDownloaded()
            }
        }
        val versionsFile: File = Utils.downloadVersionsFile()
        val versions: JSONArray? = JSONObject(FileUtils.readFileContent(versionsFile)).optJSONArray("versions")
        VersionInstaller.start(
            minecraftVersion,
            versionDir.getName(),
            versions,
            installAssets,
            installNatives,
            installLibraries,
            installForgeOrFabricOrQuilt,
            threadCount,
            mergerForFabric,
            mergerForForge,
            mergerForQuilt,
            null,
            null,
            onFinished, null, null
        )
    }

    @Throws(ModpackFunction.NotValidModPackFormat::class)
    fun tryToInstallCurseForgeModpack(
        zipFile: ZipFile,
        file: File,
        versionDir: File,
        keepFile: Boolean,
        installAssets: Boolean,
        installNatives: Boolean,
        installLibraries: Boolean,
        threadCount: Int
    ): Int {
        val entry =
            zipFile.getEntry("manifest.json") ?: throw ModpackFunction.NotValidModPackFormat("not a CurseForge modpack")
        //FileUtils.inputStream2File(zipFile.getInputStream(entry),new File(versionDir,"modpack.json"));
        try {
            val i = zipFile.getInputStream(entry)
            val manifest: JSONObject = XJSONObject(Utils.inputStream2String(i))
            installCurseForgeModpack(
                manifest,
                zipFile,
                file,
                versionDir,
                keepFile,
                installAssets,
                installNatives,
                installLibraries,
                threadCount
            )
        } catch (e: ModpackFunction.NotValidModPackFormat) {
            FileUtils.deleteDirectory(versionDir)
            throw e
        } catch (e: Exception) {
            println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e))
            FileUtils.deleteDirectory(versionDir)
        }
        return 0
    }
}
