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
package com.mrshiehx.cmcl.functions

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.bean.arguments.SingleArgument
import com.mrshiehx.cmcl.bean.arguments.TextArgument
import com.mrshiehx.cmcl.bean.arguments.ValueArgument
import com.mrshiehx.cmcl.utils.FileUtils.createFile
import com.mrshiehx.cmcl.utils.FileUtils.readFileContent
import com.mrshiehx.cmcl.utils.FileUtils.writeFile
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getConfig
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils
import org.json.JSONObject
import java.io.File
import java.io.IOException

object GameArgsFunction : Function {
    override val usageName = "gameArgs"
    override fun execute(arguments: Arguments) {
        //因为参数内容可能会被误判，所以不检测
        /*if(!Function.checkArgs(arguments,2,1,
                ArgumentRequirement.ofSingle("p"),
                ArgumentRequirement.ofSingle("print"),
                ArgumentRequirement.ofValue("p"),
                ArgumentRequirement.ofValue("print"),
                ArgumentRequirement.ofValue("a"),
                ArgumentRequirement.ofValue("add"),
                ArgumentRequirement.ofValue("d"),
                ArgumentRequirement.ofValue("delete"),
                ArgumentRequirement.ofValue("v"),
                ArgumentRequirement.ofValue("version")))return;*/
        val version = arguments.opt("v", arguments.opt("version", null))
        var versionConfigJO: JSONObject? = null
        var versionConfigFile: File? = null
        if (!isEmpty(version)) {
            if (!VersionUtils.versionExists(version)) {
                println(CMCL.getString("EXCEPTION_VERSION_NOT_FOUND", version))
                return
            }
            versionConfigFile = File(CMCL.versionsDir, "$version/cmclversion.json")
            versionConfigJO = if (!versionConfigFile.exists()) {
                try {
                    createFile(versionConfigFile, false)
                    JSONObject()
                } catch (e: IOException) {
                    println(CMCL.getString("EXCEPTION_CREATE_FILE_WITH_PATH", e))
                    return
                }
            } else {
                try {
                    JSONObject(readFileContent(versionConfigFile))
                } catch (e: Exception) {
                    JSONObject()
                }
            }
        }
        val jsonObject = versionConfigJO ?: getConfig()
        val gameArgs = jsonObject.optJSONObject("gameArgs") ?: JSONObject().also { jsonObject.put("gameArgs", it) }
        val firstArg = arguments.optArgument(1)
        if (firstArg is SingleArgument) {
            val firstKey = firstArg.key
            if (firstKey == "p" || firstKey == "print") {
                println(gameArgs.toString(2))
            } else {
                println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", firstArg.originString))
                return
            }
        } else if (firstArg is ValueArgument) {
            val firstKey = firstArg.key
            val value = firstArg.value
            when (firstKey) {
                "p", "print" -> {
                    val indentFactor = try {
                        Utils.parseWithPrompting(value)
                    } catch (e: Exception) {
                        return
                    }
                    println(gameArgs.toString(indentFactor))
                }

                "a", "add" -> {
                    var valueValue = ""
                    val valueArg = arguments.optArgument(2)
                    if (valueArg is TextArgument) {
                        valueValue = valueArg.originString
                    }
                    gameArgs.put(value, valueValue)
                    if (versionConfigFile != null) {
                        try {
                            writeFile(versionConfigFile, jsonObject.toString(), false)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Utils.saveConfig(jsonObject)
                    }
                }

                "d", "delete" -> {
                    gameArgs.remove(value)
                    if (versionConfigFile != null) {
                        try {
                            writeFile(versionConfigFile, jsonObject.toString(), false)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Utils.saveConfig(jsonObject)
                    }
                }

                else -> println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", firstArg.originString))
            }
        } else {
            println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", firstArg!!.originString))
        }
    }

}
