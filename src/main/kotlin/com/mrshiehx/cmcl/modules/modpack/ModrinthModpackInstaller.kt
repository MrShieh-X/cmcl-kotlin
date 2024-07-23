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
import com.mrshiehx.cmcl.utils.internet.ThreadsDownloader
import com.mrshiehx.cmcl.utils.json.XJSONObject
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object ModrinthModpackInstaller {
    @Throws(ModpackFunction.NotValidModPackFormat::class, ExceptionWithDescription::class, IOException::class)
    fun installModrinthModpack(
        manifest: JSONObject,
        zipFile: ZipFile,
        modPackFile: File,
        versionDir: File,
        keepFile: Boolean,
        installAssets: Boolean,
        installNatives: Boolean,
        installLibraries: Boolean,
        threadCount: Int
    ): Int {
        zipFile.stream().forEach { zipEntry: ZipEntry ->
            val overrides: String = if (zipEntry.name.startsWith("overrides")) {
                "overrides"
            } else if (zipEntry.name.startsWith("client-overrides")) {
                "client-overrides"
            } else return@forEach
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
        val dependencies: JSONObject = manifest.optJSONObject("dependencies")
            ?: throw ModpackFunction.NotValidModPackFormat(
                MissingElementException(
                    "dependencies",
                    "JSONObject"
                ).message
            )
        var gameVersion: String? = null
        var forgeVersion: String? = null
        var fabricVersion: String? = null
        var quiltVersion: String? = null
        var neoforgeVersion: String? = null
        for ((key, value) in dependencies.toMap().entries) {
            if (value is String) {
                when (key) {
                    "minecraft" -> gameVersion = value
                    "forge" -> forgeVersion = value
                    "fabric-loader" -> fabricVersion = value
                    "quilt-loader" -> quiltVersion = value
                    "neoforge" -> neoforgeVersion = value
                    else -> println(CMCL.getString("INSTALL_MODPACK_MODRINTH_UNKNOWN_MODLOADER", key))
                }
            }
        }
        if (Utils.isBlank(gameVersion))
            throw ModpackFunction.NotValidModPackFormat(CMCL.getString("MESSAGE_INSTALL_MODPACK_NOT_FOUND_GAME_VERSION"))

        if (!Utils.isEmpty(neoforgeVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_NOT_SUPPORTED_NEOFORGE"))
            return -1
        }

        if (!Utils.isBlank(forgeVersion) && !Utils.isBlank(fabricVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "Forge", "Fabric"))
            return -1
        }

        //quilt
        if (!Utils.isBlank(quiltVersion) && !Utils.isBlank(fabricVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "Quilt", "Fabric"))
            return -1
        }
        if (!Utils.isBlank(forgeVersion) && !Utils.isBlank(quiltVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "Forge", "Quilt"))
            return -1
        }
        var installForgeOrFabricOrQuilt: VersionInstaller.InstallForgeOrFabricOrQuilt? = null
        val modLoaderVersion: String?
        var mergerForFabric: VersionInstaller.Merger? = null
        var mergerForForge: VersionInstaller.Merger? = null
        var mergerForQuilt: VersionInstaller.Merger? = null
        if (!Utils.isBlank(forgeVersion)) {
            installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.FORGE
            modLoaderVersion = forgeVersion
            mergerForForge =
                VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                    val forges: Map<String, JSONObject> = try {
                        ForgeMerger.getInstallableForges(minecraftVersion)
                    } catch (e: Exception) {
                        println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                        FileUtils.deleteDirectory(versionDir)
                        return@Merger Pair(false, null)
                    }
                    val forge: JSONObject? = forges[modLoaderVersion]
                    if (forge == null) {
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
                            minecraftVersion,
                            minecraftJarFile
                        )
                    } catch (e: Exception) {
                        println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                        FileUtils.deleteDirectory(versionDir)
                        return@Merger Pair(false, null)
                    }
                }
        } else if (!Utils.isEmpty(fabricVersion)) {
            installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.FABRIC
            modLoaderVersion = fabricVersion
            mergerForFabric =
                VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                    try {
                        return@Merger FabricMerger.installInternal(
                            minecraftVersion,
                            modLoaderVersion,
                            headJSONObject
                        )
                    } catch (e: Exception) {
                        println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                        FileUtils.deleteDirectory(versionDir)
                        return@Merger Pair(false, null)
                    }
                }
        } else if (!Utils.isEmpty(quiltVersion)) {
            installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.QUILT
            modLoaderVersion = quiltVersion
            mergerForQuilt =
                VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                    try {
                        return@Merger QuiltMerger.installInternal(
                            minecraftVersion,
                            modLoaderVersion,
                            headJSONObject
                        )
                    } catch (e: Exception) {
                        println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                        FileUtils.deleteDirectory(versionDir)
                        return@Merger Pair(false, null)
                    }
                }
        }
        val onFinished = {
            val files: JSONArray? = manifest.optJSONArray("files")
            if (files != null && files.length() > 0) {
                val filess: MutableList<Pair<String, File>> = LinkedList()
                for (i in 0 until files.length()) {
                    val o: Any = files.get(i)
                    val file: JSONObject = o as? JSONObject ?: continue
                    val path: String = file.optString("path")
                    val downloads: JSONArray? = file.optJSONArray("downloads")
                    var url: String? = null
                    if (downloads != null) {
                        for (download in downloads) {
                            if (download is String) {
                                url = download
                            }
                        }
                    }
                    if (Utils.isBlank(url) || Utils.isBlank(path)) {
                        continue
                    }
                    filess.add(Pair(url, File(versionDir, path)))


                }
                if (filess.size > 0) {
                    val threadsDownloader = ThreadsDownloader(filess, true)
                    threadsDownloader.start()
                }
            }
            try {
                FileUtils.writeFile(File(versionDir, "modpack.json"), manifest.toString(2), false)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (!keepFile) {
                Utils.close(zipFile)
                modPackFile.delete()
            }
            println(CMCL.getString("INSTALL_MODPACK_COMPLETE"))
        }
        val versionsFile: File = Utils.downloadVersionsFile()
        val versions: JSONArray? = JSONObject(FileUtils.readFileContent(versionsFile)).optJSONArray("versions")
        VersionInstaller.start(
            gameVersion,
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
        return 0
    }

    @Throws(ModpackFunction.NotValidModPackFormat::class)
    fun tryToInstallModrinthModpack(
        zipFile: ZipFile,
        file: File,
        versionDir: File,
        keepFile: Boolean,
        installAssets: Boolean,
        installNatives: Boolean,
        installLibraries: Boolean,
        threadCount: Int
    ): Int {
        val entry = zipFile.getEntry("modrinth.index.json")
            ?: throw ModpackFunction.NotValidModPackFormat("not a Modrinth modpack")
        //FileUtils.inputStream2File(zipFile.getInputStream(entry),new File(versionDir,"modpack.json"));
        try {
            val i = zipFile.getInputStream(entry)
            val manifest: JSONObject = XJSONObject(Utils.inputStream2String(i))
            return installModrinthModpack(
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
            e.printStackTrace()
            println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e))
            FileUtils.deleteDirectory(versionDir)
        }
        return 0
    }
}
