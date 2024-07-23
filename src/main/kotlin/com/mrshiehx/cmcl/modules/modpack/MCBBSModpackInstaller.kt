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
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.exceptions.MissingElementException
import com.mrshiehx.cmcl.functions.mod.ModpackFunction
import com.mrshiehx.cmcl.modules.extra.fabric.FabricMerger
import com.mrshiehx.cmcl.modules.extra.forge.ForgeMerger
import com.mrshiehx.cmcl.modules.extra.liteloader.LiteloaderMerger
import com.mrshiehx.cmcl.modules.extra.optifine.OptiFineMerger
import com.mrshiehx.cmcl.modules.extra.quilt.QuiltMerger
import com.mrshiehx.cmcl.modules.version.VersionInstaller
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.console.TextProgress
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.internet.ThreadsDownloader
import com.mrshiehx.cmcl.utils.json.JSONUtils
import com.mrshiehx.cmcl.utils.json.XJSONObject
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object MCBBSModpackInstaller {
    @Throws(ModpackFunction.NotValidModPackFormat::class)
    fun tryToInstallMCBBSModpack(
        zipFile: ZipFile,
        modpackFile: File,
        versionDir: File,
        keepFile: Boolean,
        installAssets: Boolean,
        installNatives: Boolean,
        installLibraries: Boolean,
        threadCount: Int
    ): Int {
        val entry = zipFile.getEntry("mcbbs.packmeta") ?: (zipFile.getEntry("manifest.json")
            ?: throw ModpackFunction.NotValidModPackFormat("not a MCBBS modpack"))
        //FileUtils.inputStream2File(zipFile.getInputStream(entry),new File(versionDir,"modpack.json"));
        val mcbbsPackMeta = try {
            val i = zipFile.getInputStream(entry)
            XJSONObject(Utils.inputStream2String(i))
        } catch (e: Exception) {
            if (Constants.isDebug()) e.printStackTrace()
            throw ModpackFunction.NotValidModPackFormat(e.message)
        }
        val addonsJsonArray: JSONArray = mcbbsPackMeta.optJSONArray("addons")
            ?: throw ModpackFunction.NotValidModPackFormat(MissingElementException("addons", "JSONArray").message)
        val addons: List<JSONObject> = JSONUtils.jsonArrayToJSONObjectList(addonsJsonArray)
        var gameVersion: String? = null
        var forgeVersion: String? = null
        var liteloaderVersion: String? = null
        var fabricVersion: String? = null
        var quiltVersion: String? = null //not sure
        var optifineVersion: String? = null
        var neoforgeVersion: String? = null
        for (addon in addons) {
            val id: String = addon.optString("id")
            val version: String = addon.optString("version")
            when (id) {
                "game" -> gameVersion = version
                "forge" -> forgeVersion = version
                "liteloader" -> liteloaderVersion = version
                "fabric" -> fabricVersion = version
                "quilt" -> quiltVersion = version
                "optifine" -> optifineVersion = version
                "neoforge" -> neoforgeVersion = version
            }
        }
        if (Utils.isEmpty(gameVersion)) {
            throw ModpackFunction.NotValidModPackFormat(CMCL.getString("MESSAGE_INSTALL_MODPACK_NOT_FOUND_GAME_VERSION"))
        }
        if (!Utils.isEmpty(neoforgeVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_NOT_SUPPORTED_NEOFORGE"))
            return -1
        }
        if (!Utils.isEmpty(forgeVersion) && !Utils.isEmpty(fabricVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "Forge", "Fabric"))
            return -1
        }
        if (!Utils.isEmpty(liteloaderVersion) && !Utils.isEmpty(fabricVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "LiteLoader", "Fabric"))
            return -1
        }
        if (!Utils.isEmpty(optifineVersion) && !Utils.isEmpty(fabricVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "OptiFine", "Fabric"))
            return -1
        }
        if (!Utils.isEmpty(forgeVersion) && !Utils.isEmpty(quiltVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "Forge", "Quilt"))
            return -1
        }
        if (!Utils.isEmpty(liteloaderVersion) && !Utils.isEmpty(quiltVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "LiteLoader", "Quilt"))
            return -1
        }
        if (!Utils.isEmpty(optifineVersion) && !Utils.isEmpty(quiltVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "OptiFine", "Quilt"))
            return -1
        }
        if (!Utils.isEmpty(fabricVersion) && !Utils.isEmpty(quiltVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "Fabric", "Quilt"))
            return -1
        }
        var installForgeOrFabricOrQuilt: VersionInstaller.InstallForgeOrFabricOrQuilt? = null
        var modLoaderVersion: String?
        var mergerForFabric: VersionInstaller.Merger? = null
        var mergerForForge: VersionInstaller.Merger? = null
        var mergerForQuilt: VersionInstaller.Merger? = null
        var mergerForLiteLoader: VersionInstaller.Merger? = null
        var mergerForOptiFine: VersionInstaller.Merger? = null
        if (!Utils.isEmpty(forgeVersion)) {
            installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.FORGE
            modLoaderVersion = forgeVersion
            val finalModLoaderVersion1 = modLoaderVersion
            mergerForForge =
                VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                    val forges = try {
                        ForgeMerger.getInstallableForges(minecraftVersion)
                    } catch (e: Exception) {
                        println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                        FileUtils.deleteDirectory(versionDir)
                        return@Merger Pair(false, null)
                    }
                    val forge: JSONObject = forges[finalModLoaderVersion1] ?: run {
                        println(
                            CMCL.getString(
                                "EXCEPTION_INSTALL_MODPACK",
                                CMCL.getString(
                                    "INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION",
                                    finalModLoaderVersion1
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
            val finalModLoaderVersion = modLoaderVersion
            mergerForFabric =
                VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                    try {
                        return@Merger FabricMerger.installInternal(
                            minecraftVersion,
                            finalModLoaderVersion,
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
            val finalModLoaderVersion = modLoaderVersion
            mergerForQuilt =
                VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                    try {
                        return@Merger QuiltMerger.installInternal(
                            minecraftVersion,
                            finalModLoaderVersion,
                            headJSONObject
                        )
                    } catch (e: Exception) {
                        println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                        FileUtils.deleteDirectory(versionDir)
                        return@Merger Pair(false, null)
                    }
                }
        }
        if (!Utils.isEmpty(liteloaderVersion)) {
            modLoaderVersion = liteloaderVersion
            val finalModLoaderVersion = modLoaderVersion
            mergerForLiteLoader =
                VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                    try {
                        return@Merger LiteloaderMerger.installInternal(
                            minecraftVersion,
                            finalModLoaderVersion,
                            headJSONObject
                        )
                    } catch (e: Exception) {
                        println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                        FileUtils.deleteDirectory(versionDir)
                        return@Merger Pair(false, null)
                    }
                }
        }
        if (!Utils.isEmpty(optifineVersion)) {
            modLoaderVersion = optifineVersion
            val finalModLoaderVersion = modLoaderVersion
            mergerForOptiFine =
                VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                    try {
                        return@Merger OptiFineMerger.installInternal(
                            minecraftVersion,
                            finalModLoaderVersion,
                            headJSONObject,
                            minecraftJarFile
                        )
                    } catch (e: Exception) {
                        println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                        FileUtils.deleteDirectory(versionDir)
                        return@Merger Pair(false, null)
                    }
                }
        }
        zipFile.stream().forEach { zipEntry: ZipEntry ->
            val overrides = "overrides"
            if (!zipEntry.name.startsWith(overrides)) return@forEach
            val to = File(versionDir, zipEntry.name.substring(9))
            if (zipEntry.isDirectory) {
                to.mkdirs()
            } else {
                try {
                    FileUtils.inputStream2File(zipFile.getInputStream(zipEntry), to)
                } catch (e: IOException) {
                    if (Constants.isDebug()) e.printStackTrace()
                    println(CMCL.getString("MESSAGE_FAILED_TO_DECOMPRESS_FILE", zipEntry.name, e))
                }
            }
        }
        val librariesToBeMerged: JSONArray? = mcbbsPackMeta.optJSONArray("libraries")
        val onFinished = {
            /*from CurseForgeModpackInstaller*/
            val filesArray: JSONArray? = mcbbsPackMeta.optJSONArray("files")
            val files: MutableList<Pair<String, File>> = LinkedList()
            if (filesArray != null && filesArray.length() > 0) {
                print(CMCL.getString("INSTALL_MODPACK_EACH_MOD_GET_URL"))
                val textProgress = TextProgress()
                textProgress.maximum = filesArray.length()
                for (i in 0 until filesArray.length()) {
                    val file = filesArray.get(i) as? JSONObject ?: continue
                    val fileID: Int = file.optInt("fileID", -1)
                    val projectID: Int = file.optInt("projectID", -1)
                    if (fileID == -1 || projectID == -1) {
                        textProgress.value = i + 1
                        continue
                    }
                    try {
                        var fileName: String
                        var fileDownloadUrl: String
                        try {
                            val url = "https://api.curseforge.com/v1/mods/$projectID/files/$fileID"
                            val jsonObject: JSONObject =
                                JSONObject(NetworkUtils.curseForgeGet(url)).optJSONObject("data")
                            fileName = jsonObject.optString("fileName")
                            fileDownloadUrl = jsonObject.optString("downloadUrl")
                            if (Utils.isBlank(fileDownloadUrl))
                                fileDownloadUrl =
                                    "https://edge.forgecdn.net/files/${fileID / 1000}/${fileID % 1000}/${fileName}"
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
                FileUtils.writeFile(File(versionDir, "modpack.json"), mcbbsPackMeta.toString(2), false)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val onDownloaded = {
                if (!keepFile) {
                    modpackFile.delete()
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
        val versionJSONMerger = VersionInstaller.VersionJSONMerger { headJSONObject: JSONObject ->
            val launchInfo: JSONObject = mcbbsPackMeta.optJSONObject("launchInfo", JSONObject())
            val launchArgument: JSONArray? = launchInfo.optJSONArray("launchArgument")
            val javaArgument: JSONArray? = launchInfo.optJSONArray("javaArgument")
            val arguments: JSONObject? = headJSONObject.optJSONObject("arguments")
            if (arguments != null) {
                if (launchArgument != null && launchArgument.length() > 0) {
                    arguments.put(
                        "game",
                        (arguments.optJSONArray("game") ?: JSONArray())
                            .putAll(launchArgument)
                    )
                }
                if (javaArgument != null && javaArgument.length() > 0) {
                    arguments.put(
                        "jvm",
                        (arguments.optJSONArray("jvm") ?: JSONArray()).putAll(javaArgument)
                    )
                }
            } else {
                if (launchArgument != null && launchArgument.length() > 0) {
                    val sb = StringBuilder()
                    for (o in launchArgument) {
                        sb.append(o).append(' ')
                    }
                    val minecraftArguments: String = headJSONObject.optString("minecraftArguments")
                    if (minecraftArguments.isEmpty()) {
                        headJSONObject.put("minecraftArguments", sb.substring(0, sb.length - 1))
                    } else {
                        headJSONObject.put(
                            "minecraftArguments",
                            minecraftArguments + " " + sb.substring(0, sb.length - 1)
                        )
                    }
                }
            }
        }
        try {
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
                mergerForLiteLoader,
                mergerForOptiFine,
                onFinished,
                versionJSONMerger,
                librariesToBeMerged
            )
        } catch (e: Exception) {
            println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e))
            FileUtils.deleteDirectory(versionDir)
            return -1
        }
        return 0
    }
}
