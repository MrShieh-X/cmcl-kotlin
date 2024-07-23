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
 */
package com.mrshiehx.cmcl.modules.extra.fabric

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.modules.extra.ExtraMerger
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.console.PrintingUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Fabric 与原版的合并器
 */
abstract class AbstractFabricMerger : ExtraMerger {
    protected abstract val modLoaderName: String

    protected abstract val metaUrl: String

    protected abstract val mavenUrl: String

    protected abstract val storageName: String

    protected open val isQuilt: Boolean = false

    /**
     * 将 Fabric 的JSON合并到原版JSON
     *
     * @return key: 如果无法安装Fabric，是否继续安装；value：如果成功合并，则为需要安装的依赖库集合，否则为空
     */
    override fun merge(
        minecraftVersion: String,
        headJSONObject: JSONObject,
        jarFile: File,
        askContinue: Boolean,
        extraVersion: String?
    ): Pair<Boolean, List<JSONObject>?> {
        val fabricVersion: String?
        if (Utils.isEmpty(extraVersion)) {
            val jsonArray = try {
                listFabricLoaderVersions(minecraftVersion)
            } catch (e: Exception) {
                if (Constants.isDebug()) e.printStackTrace()
                //e.printStackTrace();
                println(CMCL.getString("INSTALL_MODLOADER_FAILED_TO_GET_INSTALLABLE_VERSION", modLoaderName))
                return Pair(
                    askContinue && InteractionUtils.yesOrNo(
                        CMCL.getString(
                            "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                            modLoaderName
                        )
                    ), null
                )
            }
            if (jsonArray.length() == 0) {
                println(CMCL.getString("INSTALL_MODLOADER_NO_INSTALLABLE_VERSION", modLoaderName))
                return Pair(
                    askContinue && InteractionUtils.yesOrNo(
                        CMCL.getString(
                            "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                            modLoaderName
                        )
                    ), null
                )
            }
            val fabrics: MutableMap<String, JSONObject> = LinkedHashMap()
            for (`object` in jsonArray) {
                if (`object` is JSONObject) {
                    val jsonObject = `object`
                    fabrics[jsonObject.optJSONObject("loader", JSONObject()).optString("version")] = jsonObject
                }
            }
            val fabricVersions: List<String> = ArrayList(fabrics.keys)
            PrintingUtils.printListItems(fabricVersions, true, 4, 2, true)
            fabricVersion = ExtraMerger.selectExtraVersion(
                CMCL.getString(
                    "INSTALL_MODLOADER_SELECT",
                    modLoaderName,
                    fabricVersions[0]
                ), fabrics, fabricVersions[0], modLoaderName
            )
            if (fabricVersion == null) return Pair(false, null)
        } else {
            fabricVersion = extraVersion
        }
        return try {
            installInternal(minecraftVersion, fabricVersion, headJSONObject)
        } catch (e: Exception) {
            if (Constants.isDebug()) e.printStackTrace()
            println(e.message)
            Pair(
                askContinue && InteractionUtils.yesOrNo(
                    CMCL.getString(
                        "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                        modLoaderName
                    )
                ), null
            )
        }
    }

    @Throws(ExceptionWithDescription::class)
    fun installInternal(
        minecraftVersion: String,
        fabricVersion: String,
        headJSONObject: JSONObject
    ): Pair<Boolean, List<JSONObject>> {
        val jsonUrl = metaUrl + String.format("versions/loader/%s/%s", minecraftVersion, fabricVersion)
        val targetJSONString = try {
            NetworkUtils[jsonUrl]
        } catch (e: IOException) {
            if (Constants.isDebug()) e.printStackTrace()
            //e.printStackTrace();
            throw ExceptionWithDescription(CMCL.getString("INSTALL_MODLOADER_FAILED_TO_GET_TARGET_JSON", modLoaderName))
        }
        if (targetJSONString.contains("no loader version found")) {
            throw ExceptionWithDescription(
                CMCL.getString("INSTALL_MODLOADER_SELECT_NOT_FOUND_GAME_OR_TARGET_EXTRA")
                    .replace("\${NAME}", modLoaderName)
            )
        }
        val fabricJSONOrigin = JSONUtils.parseJSONObject(targetJSONString)
            ?: throw ExceptionWithDescription(
                CMCL.getString(
                    "INSTALL_MODLOADER_FAILED_TO_PARSE_TARGET_JSON",
                    modLoaderName
                )
            )
        if (fabricJSONOrigin.optString("message")
                .contains("not found", ignoreCase = true) || fabricJSONOrigin.optString("message")
                .contains("does not exist", ignoreCase = true) || fabricJSONOrigin.optString("code")
                .contains("not_found", ignoreCase = true)
        ) {
            throw ExceptionWithDescription(
                CMCL.getString("INSTALL_MODLOADER_SELECT_NOT_FOUND_GAME_OR_TARGET_EXTRA")
                    .replace("\${NAME}", modLoaderName)
            )
        }
        val fabricJSON = JSONObject()
        val loader = fabricJSONOrigin.optJSONObject("loader")
        val intermediary = fabricJSONOrigin.optJSONObject("intermediary")
        val launcherMeta = fabricJSONOrigin.optJSONObject("launcherMeta")
        val hashed = fabricJSONOrigin.optJSONObject("hashed")
        if (launcherMeta != null) {
            val mainClassObject = launcherMeta.opt("mainClass")
            if (mainClassObject is String) {
                fabricJSON.put("mainClass", mainClassObject)
            } else if (mainClassObject is JSONObject) {
                fabricJSON.put("mainClass", mainClassObject.optString("client"))
            }
            val launchWrapper = launcherMeta.optJSONObject("launchwrapper")
            if (launchWrapper != null) {
                val tweakers = launchWrapper.optJSONObject("tweakers")
                if (tweakers != null) {
                    val client: JSONArray? = tweakers.optJSONArray("client")
                    if (client != null) {
                        for (o in client) {
                            if (o is String) {
                                fabricJSON.put(
                                    "arguments",
                                    JSONObject().put("game", JSONArray().put("--tweakClass").put(o))
                                )
                                break
                            }
                        }
                    }
                }
            }
            val libraries = launcherMeta.optJSONObject("libraries")
            if (libraries != null) {
                val common: JSONArray = libraries.optJSONArray("common")
                val server: JSONArray = libraries.optJSONArray("server")
                fabricJSON.put("libraries", common.putAll(server))
            }
        }
        val libraries: JSONArray = fabricJSON.optJSONArray("libraries")
            ?: JSONArray().also { fabricJSON.put("libraries", it) }
        if (intermediary != null) {
            val maven = intermediary.optString("maven")
            if (!Utils.isEmpty(maven)) {
                libraries.put(
                    JSONObject().put("name", maven).put(
                        "url",
                        if (maven.startsWith("net.fabricmc:intermediary:")) "https://maven.fabricmc.net/" else mavenUrl
                    )
                )
            }
        }
        if (loader != null) {
            val maven = loader.optString("maven")
            if (!Utils.isEmpty(maven)) {
                libraries.put(JSONObject().put("name", maven).put("url", mavenUrl))
            }
        }
        if (hashed != null) {
            var maven = hashed.optString("maven")
            if (!Utils.isEmpty(maven)) {
                var mavenUrl = mavenUrl
                if (isQuilt && maven.startsWith("org.quiltmc:hashed")) {
                    maven = maven.replace("org.quiltmc:hashed", "net.fabricmc:intermediary")
                    mavenUrl = "https://maven.fabricmc.net/"
                }
                libraries.put(JSONObject().put("name", maven).put("url", mavenUrl))
            }
        }
        return Pair(true, realMerge(headJSONObject, fabricJSON, fabricVersion, jsonUrl))
    }

    private fun realMerge(
        headJSONObject: JSONObject,
        fabricJSON: JSONObject,
        fabricVersion: String,
        jsonUrl: String
    ): List<JSONObject> {
        val mainClass = fabricJSON.optString("mainClass")
        mainClass?.let { headJSONObject.put("mainClass", it) }

        val fabric = JSONObject()
        fabric.put("version", fabricVersion)
        fabric.put("originJsonUrl", jsonUrl)
        headJSONObject.put(storageName, fabric)
        val fabricLibraries: JSONArray? = fabricJSON.optJSONArray("libraries")
        val list: MutableList<JSONObject> = LinkedList()
        if (fabricLibraries != null) {
            for (o in fabricLibraries) {
                if (o is JSONObject) {
                    list.add(o)
                }
            }
            headJSONObject.optJSONArray("libraries").putAll(fabricLibraries)
        }
        val arguments = fabricJSON.optJSONObject("arguments")
        if (arguments != null) {
            val argumentsMC = headJSONObject.optJSONObject("arguments")
            if (argumentsMC != null) {
                val game: JSONArray? = arguments.optJSONArray("game")
                if (game != null && game.length() > 0) {
                    argumentsMC.optJSONArray("game").putAll(game)
                }
                val jvm: JSONArray? = arguments.optJSONArray("jvm")
                if (jvm != null && jvm.length() > 0) {
                    argumentsMC.optJSONArray("jvm").putAll(jvm)
                }
            } else {
                headJSONObject.put("arguments", arguments)
            }
        }
        return list
    }

    //列出该 Minecraft 版本支持的所有 Fabric 版本
    @Throws(IOException::class)
    private fun listFabricLoaderVersions(minecraftVersion: String): JSONArray {
        return JSONArray(NetworkUtils[metaUrl + "versions/loader/" + minecraftVersion])
    }
}
