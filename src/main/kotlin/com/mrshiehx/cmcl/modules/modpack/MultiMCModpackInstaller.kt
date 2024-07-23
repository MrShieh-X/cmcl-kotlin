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
import com.mrshiehx.cmcl.functions.mod.ModpackFunction
import com.mrshiehx.cmcl.modules.extra.fabric.FabricMerger
import com.mrshiehx.cmcl.modules.extra.forge.ForgeMerger
import com.mrshiehx.cmcl.modules.extra.liteloader.LiteloaderMerger
import com.mrshiehx.cmcl.modules.extra.quilt.QuiltMerger
import com.mrshiehx.cmcl.modules.version.VersionInstaller
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.json.XJSONObject
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object MultiMCModpackInstaller {
    @Throws(ModpackFunction.NotValidModPackFormat::class)
    fun tryToInstallMultiMCModpack(
        zipFile: ZipFile,
        file: File,
        versionDir: File,
        keepFile: Boolean,
        installAssets: Boolean,
        installNatives: Boolean,
        installLibraries: Boolean,
        threadCount: Int
    ): Int {
        val instanceFileName = "instance.cfg"
        var instanceCFGEntry: ZipEntry?
        var modpackName: String? = null
        if (zipFile.getEntry(instanceFileName).also { instanceCFGEntry = it } != null) modpackName = "" else {
            val entries: Enumeration<*> = zipFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (entry is ZipEntry) {
                    val entryName = entry.name
                    val idx = entryName.indexOf('/')
                    if (idx >= 0 && entryName.length == idx + instanceFileName.length + 1 && entryName.startsWith(
                            instanceFileName,
                            idx + 1
                        )
                    ) {
                        modpackName = entryName.substring(0, idx + 1)
                        instanceCFGEntry = entry
                        break
                    }
                }
            }
        }
        if (modpackName == null) throw ModpackFunction.NotValidModPackFormat("not a MultiMC modpack")
        val mmcPack =
            zipFile.getEntry(modpackName + "mmc-pack.json")
                ?: throw ModpackFunction.NotValidModPackFormat("not a MultiMC modpack")
        val mmcPackJSON: JSONObject
        try {
            mmcPackJSON = JSONObject(Utils.inputStream2String(zipFile.getInputStream(mmcPack)))
        } catch (e: Exception) {
            /*System.out.println(getString("EXCEPTION_INSTALL_MODPACK", e));
            Utils.deleteDirectory(versionDir);
            return -1;*/
            throw ModpackFunction.NotValidModPackFormat(e.message)
        }
        val instanceCFG = Properties()
        try {
            instanceCFG.load(InputStreamReader(zipFile.getInputStream(instanceCFGEntry!!), StandardCharsets.UTF_8))
        } catch (e: Exception) {
            /*System.out.println(getString("EXCEPTION_INSTALL_MODPACK", e));
            Utils.deleteDirectory(versionDir);
            return -1;*/
        }
        val components: JSONArray? = mmcPackJSON.optJSONArray("components")
        var gameVersion: String? = null
        var forgeVersion: String? = null
        var liteloaderVersion: String? = null
        var fabricVersion: String? = null
        var quiltVersion: String? = null

        var neoforgeVersion: String? = null
        if (components != null) {
            for (jsonObject in components) {
                if (jsonObject is JSONObject) {
                    val version: String = jsonObject.optString("version")
                    if (!Utils.isEmpty(version)) {
                        when (jsonObject.optString("uid")) {
                            "net.minecraft" -> gameVersion = version
                            "net.minecraftforge" -> forgeVersion = version
                            "com.mumfrey.liteloader" -> liteloaderVersion = version
                            "net.fabricmc.fabric-loader" -> fabricVersion = version
                            "org.quiltmc.quilt-loader" -> quiltVersion = version
                            "net.neoforged" -> {
                                neoforgeVersion = version
                            }
                        }
                    }
                }
            }
        }
        if (Utils.isEmpty(gameVersion)) {
            /*System.out.println(getString("MESSAGE_INSTALL_MODPACK_NOT_FOUND_GAME_VERSION"));
            return -1;*/
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

        //Quilt
        if (!Utils.isEmpty(forgeVersion) && !Utils.isEmpty(quiltVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "Forge", "Quilt"))
            return -1
        }
        if (!Utils.isEmpty(liteloaderVersion) && !Utils.isEmpty(quiltVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "LiteLoader", "Quilt"))
            return -1
        }
        if (!Utils.isEmpty(fabricVersion) && !Utils.isEmpty(quiltVersion)) {
            println(CMCL.getString("MESSAGE_INSTALL_MODPACK_COEXIST", "Fabric", "Quilt"))
            return -1
        }
        val finalModpackName: String = modpackName
        zipFile.stream().forEach { zipEntry: ZipEntry ->
            val start = if (zipEntry.name.startsWith("$finalModpackName.minecraft/")) {
                "$finalModpackName.minecraft/".length
            } else if (zipEntry.name.startsWith(finalModpackName + "minecraft/")) {
                (finalModpackName + "minecraft/").length
            } else {
                return@forEach
            }
            val to = File(versionDir, zipEntry.name.substring(start))
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
        var installForgeOrFabricOrQuilt: VersionInstaller.InstallForgeOrFabricOrQuilt? = null
        var mergerForFabric: VersionInstaller.Merger? = null
        var mergerForForge: VersionInstaller.Merger? = null
        var mergerForQuilt: VersionInstaller.Merger? = null
        var mergerForLiteLoader: VersionInstaller.Merger? = null
        if (!Utils.isEmpty(forgeVersion)) {
            installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.FORGE
            val finalForgeVersion = forgeVersion
            mergerForForge =
                VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                    val forges = try {
                        ForgeMerger.getInstallableForges(minecraftVersion)
                    } catch (e: Exception) {
                        println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e.message))
                        FileUtils.deleteDirectory(versionDir)
                        return@Merger Pair(false, null)
                    }
                    val forge: JSONObject? = forges[finalForgeVersion]
                    if (forge == null) {
                        println(
                            CMCL.getString(
                                "EXCEPTION_INSTALL_MODPACK",
                                CMCL.getString("INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION", finalForgeVersion)
                                    .replace("\${NAME}", "Forge")
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
            val finalModLoaderVersion = fabricVersion
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
            val finalModLoaderVersion = quiltVersion
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
            val finalModLoaderVersion = liteloaderVersion
            mergerForLiteLoader =
                VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File?, askContinue: Boolean ->
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
        val onFinished = {
            try {
                FileUtils.writeFile(
                    File(versionDir, "modpack.json"),
                    XJSONObject().put("type", "MultiMC").put("mmcPack", mmcPackJSON).put("instanceConfig", instanceCFG)
                        .toString(2),
                    false
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (!keepFile) {
                Utils.close(zipFile)
                file.delete()
            }
            println(CMCL.getString("INSTALL_MODPACK_COMPLETE"))
        }
        try {
            val versionsFile: File = Utils.downloadVersionsFile()
            val versions: JSONArray = JSONObject(FileUtils.readFileContent(versionsFile)).optJSONArray("versions")
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
                null,
                onFinished, null, null
            )
        } catch (e: Exception) {
            println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e))
            FileUtils.deleteDirectory(versionDir)
            return -1
        }
        return 0
    }
}
