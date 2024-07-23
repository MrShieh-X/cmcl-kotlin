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
import com.mrshiehx.cmcl.utils.FileUtils.readFileContent
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.version.VersionModuleUtils
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils
import org.json.JSONObject
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object PrintVersionInfo {
    fun execute(jsonFile: File, jarFile: File, versionDir: File, versionName: String) {
        try {
            val head = JSONObject(readFileContent(jsonFile))
            val information: MutableMap<String, String> = LinkedHashMap()
            val gameVersion = VersionUtils.getGameVersion(head, jarFile)
            if (!isEmpty(gameVersion.id) && Utils.isEmpty(gameVersion.name)) {
                information[CMCL.getString("VERSION_INFORMATION_GAME_VERSION")] = gameVersion.id
            } else if (isEmpty(gameVersion.id) && !isEmpty(gameVersion.name)) {
                information[CMCL.getString("VERSION_INFORMATION_GAME_VERSION")] = gameVersion.name
            } else if (!isEmpty(gameVersion.id) && !isEmpty(gameVersion.name)) {
                if (gameVersion.name == gameVersion.id) {
                    information[CMCL.getString("VERSION_INFORMATION_GAME_VERSION")] = gameVersion.name
                } else {
                    information[CMCL.getString("VERSION_INFORMATION_GAME_VERSION")] =
                        gameVersion.name + " (" + gameVersion.id + ")"
                }
            } else {
                information[CMCL.getString("VERSION_INFORMATION_GAME_VERSION")] =
                    CMCL.getString("VERSION_INFORMATION_GAME_VERSION_FAILED_GET")
            }
            information[CMCL.getString("VERSION_INFORMATION_VERSION_PATH")] = versionDir.absolutePath
            val type = head.optString("type")
            if (!Utils.isEmpty(type)) {
                when (type) {
                    "release" -> information[CMCL.getString("VERSION_INFORMATION_VERSION_TYPE")] =
                        CMCL.getString("VERSION_INFORMATION_VERSION_TYPE_RELEASE")

                    "snapshot" -> information[CMCL.getString("VERSION_INFORMATION_VERSION_TYPE")] =
                        CMCL.getString("VERSION_INFORMATION_VERSION_TYPE_SNAPSHOT")

                    "old_beta" -> information[CMCL.getString("VERSION_INFORMATION_VERSION_TYPE")] =
                        CMCL.getString("VERSION_INFORMATION_VERSION_TYPE_OLD_BETA")

                    "old_alpha" -> information[CMCL.getString("VERSION_INFORMATION_VERSION_TYPE")] =
                        CMCL.getString("VERSION_INFORMATION_VERSION_TYPE_OLD_ALPHA")
                }
            }
            val assets = head.optString("assets")
            if (!Utils.isEmpty(assets)) information[CMCL.getString("VERSION_INFORMATION_ASSETS_VERSION")] = assets
            var releaseTime = head.optString("releaseTime")
            if (!Utils.isEmpty(releaseTime)) {
                val iso8601: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                var parse: Date? = null
                try {
                    parse = iso8601.parse(releaseTime)
                } catch (e: Exception) {
                    try {
                        releaseTime = releaseTime.substring(0, 22) + releaseTime.substring(23)
                        parse = iso8601.parse(releaseTime)
                    } catch (e1: Exception) {
                        information[CMCL.getString("VERSION_INFORMATION_RELEASE_TIME")] =
                            CMCL.getString("EXCEPTION_UNABLE_PARSE")
                    }
                }
                if (parse != null) {
                    val format = SimpleDateFormat(CMCL.getString("TIME_FORMAT"), CMCL.locale)
                    information[CMCL.getString("VERSION_INFORMATION_RELEASE_TIME")] =
                        format.format(parse) + " (" + TimeZone.getDefault().displayName + ")"
                }
            }
            val javaVersion = head.optJSONObject("javaVersion")
            if (javaVersion != null) {
                val component = javaVersion.optString("component")
                if (!Utils.isEmpty(component)) {
                    information[CMCL.getString("VERSION_INFORMATION_JAVA_COMPONENT")] = component
                }
                val majorVersion = javaVersion.optString("majorVersion")
                if (!Utils.isEmpty(majorVersion)) {
                    information[CMCL.getString("VERSION_INFORMATION_JAVA_VERSION")] = majorVersion
                }
            }
            /*JSONObject fabric = head.optJSONObject("fabric");
                    if (fabric != null) {
                        String version = fabric.optString("version");
                        if (!isEmpty(version)) {
                            information.put(getString("VERSION_INFORMATION_FABRIC_VERSION"), version);
                        }
                    }*/
            val fabricVersion = VersionModuleUtils.getFabricVersion(head)
            if (!Utils.isEmpty(fabricVersion)) information[CMCL.getString("VERSION_INFORMATION_FABRIC_VERSION")] =
                fabricVersion
            val forgeVersion = VersionModuleUtils.getForgeVersion(head)
            if (!Utils.isEmpty(forgeVersion)) information[CMCL.getString("VERSION_INFORMATION_FORGE_VERSION")] =
                forgeVersion
            val liteloaderVersion = VersionModuleUtils.getLiteloaderVersion(head)
            if (!Utils.isEmpty(liteloaderVersion)) information[CMCL.getString("VERSION_INFORMATION_LITELOADER_VERSION")] =
                liteloaderVersion
            val optiFineVersion = VersionModuleUtils.getOptifineVersion(head)
            if (!Utils.isEmpty(optiFineVersion)) information[CMCL.getString("VERSION_INFORMATION_OPTIFINE_VERSION")] =
                optiFineVersion
            val quiltVersion = VersionModuleUtils.getQuiltVersion(head)
            if (!Utils.isEmpty(quiltVersion)) information[CMCL.getString("VERSION_INFORMATION_QUILT_VERSION")] =
                quiltVersion
            val neoForge = VersionModuleUtils.getNeoForgeVersion(head)
            if (!Utils.isEmpty(neoForge)) information[CMCL.getString("VERSION_INFORMATION_NEOFORGE_VERSION")] =
                neoForge
            println("$versionName:") //legal
            for ((key, value) in information) {
                print(key) //legal
                println(value) //legal
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println(CMCL.getString("UNABLE_GET_VERSION_INFORMATION"))
        }
    }
}
