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

package com.mrshiehx.cmcl.bean

import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.bean.arguments.ValueArgument
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.ArgumentsUtils
import com.mrshiehx.cmcl.utils.console.CommandUtils
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

class VersionConfig(
    val gameDir: String?,
    val javaPath: String?,
    val maxMemory: Int?,
    val windowSizeWidth: Int?,
    val windowSizeHeight: Int?,
    val isFullscreen: Boolean?,
    val jvmArgs: List<String>,
    val gameArgs: Map<String, String?>,
    val assetsDir: String?,
    val resourcesDir: String?,
    val exitWithMinecraft: Boolean?,
    val printStartupInfo: Boolean?,
    val checkAccountBeforeStart: Boolean?,
    val isolate: Boolean?,
    val qpLogFile: String?,
    val qpSaveName: String?,
    val qpServerAddress: String?,
    val qpRealmsID: String?
) {
    companion object {
        val EMPTY = VersionConfig(
            null,
            null,
            null,
            null,
            null,
            null,
            emptyList(),
            emptyMap(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )

        fun valueOf(origin: JSONObject): VersionConfig =
            VersionConfig(
                origin.optString("gameDir"),
                origin.optString("javaPath"),
                if (origin.opt("maxMemory") is Int) origin.optInt("maxMemory") else null,
                if (origin.opt("windowSizeWidth") is Int) origin.optInt("windowSizeWidth") else null,
                if (origin.opt("windowSizeHeight") is Int) origin.optInt("windowSizeHeight") else null,
                if (origin.opt("isFullscreen") is Boolean) origin.optBoolean("isFullscreen") else null,
                ArgumentsUtils.parseJVMArgs(origin.optJSONArray("jvmArgs")),
                ArgumentsUtils.parseGameArgs(origin.optJSONObject("gameArgs")),
                origin.optString("assetsDir"),
                origin.optString("resourcesDir"),
                if (origin.opt("exitWithMinecraft") is Boolean) origin.optBoolean("exitWithMinecraft") else null,
                if (origin.opt("printStartupInfo") is Boolean) origin.optBoolean("printStartupInfo") else null,
                if (origin.opt("checkAccountBeforeStart") is Boolean) origin.optBoolean("checkAccountBeforeStart") else null,
                if (origin.opt("isolate") is Boolean) origin.optBoolean("isolate") else null,
                origin.optString("qpLogFile"),
                origin.optString("qpSaveName"),
                origin.optString("qpServerAddress"),
                origin.optString("qpRealmsID")
            )

        fun valueOfPCL2(setupIni: String?): VersionConfig {
            if (setupIni.isNullOrBlank()) return EMPTY
            val lines = setupIni.splitByRegex("\n")
            val gameArgs: MutableMap<String, String?> = LinkedHashMap()
            var jvmArgs: List<String> = emptyList()
            var javaPath: String? = null
            var isolate: Boolean? = null
            var qpServerAddress: String? = null
            for (line in lines) {
                val indexOf = line.indexOf(":")
                if (indexOf <= 0) continue //小于0的情况：若不存在:则返回-1
                val name = line.substring(0, indexOf)
                var value: String
                val matcher = Pattern.compile("$name:\\s*(?<value>[\\s\\S]*)").matcher(line)
                value = if (matcher.find()) {
                    matcher.group("value")
                } else ""
                if (!isEmpty(value)) {
                    when (name) {
                        "VersionAdvanceGame" -> {
                            val arguments = Arguments(value, false)
                            for (argument in arguments.getArguments()) {
                                val key = argument.key
                                if (key == "version" || key == "versionType") continue
                                if (argument is ValueArgument) {
                                    gameArgs[key] = argument.value
                                } else {
                                    gameArgs[key] = null
                                }
                            }
                        }

                        "VersionArgumentJavaSelect" -> try {
                            javaPath = JSONObject(value).optString("Path")
                        } catch (ignored: JSONException) {
                        }

                        "VersionAdvanceJvm" -> jvmArgs =
                            ArgumentsUtils.parseJVMArgs(
                                CommandUtils.splitCommand(
                                    CommandUtils.clearRedundantSpaces(
                                        value
                                    )
                                )
                            )

                        "VersionArgumentIndie" -> isolate = when (value) {
                            "1" -> true
                            "2" -> false
                            else -> null
                        }

                        "VersionServerEnter" -> qpServerAddress = value
                    }
                }
            }
            return VersionConfig(
                null,
                javaPath,
                null,
                null,
                null,
                null,
                jvmArgs,
                gameArgs,
                null,
                null,
                null,
                null,
                null,
                isolate,
                null,
                null,
                qpServerAddress,
                null
            )
        }


        fun valueOfHMCL(origin: JSONObject): VersionConfig {
            //workingDirectory
            var isolate = false
            var workingDirectory: String? = null
            run {
                val gameDirType = origin.optInt("gameDirType")
                if (gameDirType == 1) {
                    isolate = true
                } else if (gameDirType == 2) {
                    workingDirectory = origin.optString("gameDir")
                }
            }
            val gameArgs: MutableMap<String, String?> = java.util.LinkedHashMap()
            val gameArgsString = origin.optString("minecraftArgs")
            if (!isEmpty(gameArgsString)) {
                val arguments = Arguments(gameArgsString, false)
                for (argument in arguments.getArguments()) {
                    val key = argument.key
                    if (key == "version" || key == "versionType") continue
                    if (argument is ValueArgument) {
                        gameArgs[key] = argument.value
                    } else {
                        gameArgs[key] = null
                    }
                }
            }
            return VersionConfig(
                workingDirectory,
                origin.optString("defaultJavaPath"),
                if (origin.opt("maxMemory") is Int) origin.optInt("maxMemory") else null,
                if (origin.opt("width") is Int) origin.optInt("width") else null,
                if (origin.opt("height") is Int) origin.optInt("height") else null,
                if (origin.opt("fullscreen") is Boolean) origin.optBoolean("fullscreen") else null,
                ArgumentsUtils.parseJVMArgs(
                    CommandUtils.splitCommand(
                        CommandUtils.clearRedundantSpaces(
                            origin.optString(
                                "javaArgs"
                            )
                        )
                    )
                ),
                gameArgs,
                null,
                null,
                null,
                null,
                null,
                isolate,
                null,
                null,
                null,  /*I don't know*/
                null
            )
        }

    }

    /**
     * 合并本对象与 `versionConfig` 并返回新的版本配置对象。
     *
     * 若 `versionConfig` 某项为空才会把本对象的对应项作为新版本配置对象的对应项。
     */
    fun mergeTo(versionConfig: VersionConfig): VersionConfig {
        val newGameArgs: MutableMap<String, String?> = LinkedHashMap()
        newGameArgs.putAll(versionConfig.gameArgs)
        newGameArgs.putAll(gameArgs)
        val newJvmArgs: MutableList<String> = LinkedList()
        newJvmArgs.addAll(versionConfig.jvmArgs)
        newJvmArgs.addAll(jvmArgs)
        return VersionConfig(
            if (!isEmpty(versionConfig.gameDir)) versionConfig.gameDir else gameDir,
            if (!isEmpty(versionConfig.javaPath)) versionConfig.javaPath else javaPath,
            versionConfig.maxMemory ?: maxMemory,
            versionConfig.windowSizeWidth ?: windowSizeWidth,
            versionConfig.windowSizeHeight ?: windowSizeHeight,
            versionConfig.isFullscreen ?: isFullscreen,
            newJvmArgs,
            newGameArgs,
            if (!isEmpty(versionConfig.assetsDir)) versionConfig.assetsDir else assetsDir,
            if (!isEmpty(versionConfig.resourcesDir)) versionConfig.resourcesDir else resourcesDir,
            versionConfig.exitWithMinecraft ?: exitWithMinecraft,
            versionConfig.printStartupInfo ?: printStartupInfo,
            versionConfig.checkAccountBeforeStart ?: checkAccountBeforeStart,
            versionConfig.isolate ?: isolate,
            if (!isEmpty(versionConfig.qpLogFile)) versionConfig.qpLogFile else qpLogFile,
            if (!isEmpty(versionConfig.qpSaveName)) versionConfig.qpSaveName else qpSaveName,
            if (!isEmpty(versionConfig.qpServerAddress)) versionConfig.qpServerAddress else qpServerAddress,
            if (!isEmpty(versionConfig.qpRealmsID)) versionConfig.qpRealmsID else qpRealmsID
        )
    }
}