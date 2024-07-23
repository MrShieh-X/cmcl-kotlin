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
package com.mrshiehx.cmcl.modules.version

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.utils.FileUtils.bytes2File
import com.mrshiehx.cmcl.utils.FileUtils.getBytes
import com.mrshiehx.cmcl.utils.FileUtils.readFileContent
import com.mrshiehx.cmcl.utils.FileUtils.writeFile
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.downloadVersionsFile
import com.mrshiehx.cmcl.utils.cmcl.version.VersionModuleUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

object VersionCompleter {
    fun execute(jsonFile: File, jarFile: File, versionName: String) {
        val headJSONObjectIC: JSONObject
        val headJSONObjectICContent: String
        try {
            headJSONObjectICContent = readFileContent(jsonFile)
            headJSONObjectIC = JSONObject(headJSONObjectICContent)
        } catch (e: IOException) {
            println(CMCL.getString("EXCEPTION_READ_FILE_WITH_PATH", jsonFile.absolutePath))
            return
        }
        val inheritsFrom = headJSONObjectIC.optString("inheritsFrom")
        if (Utils.isEmpty(inheritsFrom)) {
            println(CMCL.getString("MESSAGE_COMPLETE_VERSION_IS_COMPLETE"))
            return
        }
        var jarFileContent: ByteArray? = null
        if (jarFile.exists()) {
            try {
                jarFileContent = getBytes(jarFile)
            } catch (ignore: Exception) {
            }
        }
        try {
            VersionInstaller.start(
                inheritsFrom,
                versionName,
                JSONObject(readFileContent(downloadVersionsFile())).optJSONArray("versions"),
                installAssets = true,
                installNatives = true,
                installLibraries = true,
                null,
                Constants.DEFAULT_DOWNLOAD_THREAD_COUNT,
                null,
                null,
                null,
                null,
                null,
                { println(CMCL.getString("MESSAGE_COMPLETED_VERSION")) },
                { headJSONObject: JSONObject ->
                    val mainClass = headJSONObjectIC.optString("mainClass")
                    if (!Utils.isBlank(mainClass)) {
                        headJSONObject.put("mainClass", mainClass)
                    }
                    var minecraftArguments = headJSONObjectIC.optString("minecraftArguments")
                    val arguments3 = headJSONObjectIC.optJSONObject("arguments")
                    if (!Utils.isBlank(minecraftArguments)) {
                        val hmca = headJSONObject.optString("minecraftArguments")
                        if (hmca.isEmpty())
                            headJSONObject.put("minecraftArguments", minecraftArguments)
                        else {
                            val arguments1 = Arguments(hmca, false)
                            val arguments2 = Arguments(
                                minecraftArguments, false
                            )
                            arguments1.merge(arguments2)
                            headJSONObject.put(
                                "minecraftArguments",
                                arguments1.toString("--").also { minecraftArguments = it })
                        }
                    }
                    if (arguments3 != null) {
                        val argumentsMC = headJSONObject.optJSONObject("arguments")
                        if (argumentsMC != null) {
                            var gameMC = argumentsMC.optJSONArray("game")
                            var jvmMC = argumentsMC.optJSONArray("jvm")
                            val game = arguments3.optJSONArray("game")
                            if (game != null && game.length() > 0) {
                                if (gameMC == null) argumentsMC.put("game", JSONArray().also { gameMC = it })
                                gameMC!!.putAll(game)
                            }
                            val jvm = arguments3.optJSONArray("jvm")
                            if (jvm != null && jvm.length() > 0) {
                                if (jvmMC == null) argumentsMC.put("jvm", JSONArray().also { jvmMC = it })
                                jvmMC!!.putAll(jvm)
                            }
                        } else {
                            headJSONObject.put("arguments", arguments3)
                        }
                    }
                    val libraries = headJSONObject.optJSONArray("libraries") ?: JSONArray().also {
                        headJSONObject.put(
                            "libraries",
                            it
                        )
                    }
                    libraries.putAll(headJSONObjectIC.optJSONArray("libraries"))
                    val fabricVersion = VersionModuleUtils.getFabricVersion(headJSONObject)
                    if (!Utils.isEmpty(fabricVersion)) {
                        headJSONObject.put("fabric", JSONObject().put("version", fabricVersion))
                    }
                    val forgeVersion = VersionModuleUtils.getForgeVersion(headJSONObject)
                    if (!Utils.isEmpty(forgeVersion)) {
                        headJSONObject.put("forge", JSONObject().put("version", forgeVersion))
                    }
                    val liteloaderVersion = VersionModuleUtils.getLiteloaderVersion(headJSONObject)
                    if (!Utils.isEmpty(liteloaderVersion)) {
                        headJSONObject.put("liteloader", JSONObject().put("version", liteloaderVersion))
                    }
                    val optifineVersion = VersionModuleUtils.getOptifineVersion(headJSONObject)
                    if (!Utils.isEmpty(optifineVersion)) {
                        headJSONObject.put("optifine", JSONObject().put("version", optifineVersion))
                    }
                    val quiltVersion = VersionModuleUtils.getQuiltVersion(headJSONObject)
                    if (!Utils.isEmpty(quiltVersion)) {
                        headJSONObject.put("quilt", JSONObject().put("version", quiltVersion))
                    }
                    val neoForgeVersion = VersionModuleUtils.getNeoForgeVersion(headJSONObject)
                    if (!Utils.isEmpty(neoForgeVersion)) {
                        headJSONObject.put("neoforge", JSONObject().put("version", neoForgeVersion))
                    }
                },
                null
            )
        } catch (e: Exception) {
            println(e.message) //legal
            try {
                writeFile(jsonFile, headJSONObjectICContent, false)
                if (jarFileContent == null) {
                    jarFile.delete()
                } else {
                    bytes2File(jarFile, jarFileContent, false)
                }
            } catch (ignore: Exception) {
            }
        }
    }
}
