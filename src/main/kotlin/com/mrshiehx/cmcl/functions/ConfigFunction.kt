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
import com.mrshiehx.cmcl.bean.arguments.*
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getConfig
import com.mrshiehx.cmcl.utils.console.PrintingUtils
import org.json.JSONObject

object ConfigFunction : Function {
    override val usageName = "config"
    override fun execute(arguments: Arguments) {
        if (!Function.checkArgs(
                arguments,
                2,
                1,
                ArgumentRequirement.ofSingle("a"),
                ArgumentRequirement.ofSingle("all"),
                ArgumentRequirement.ofSingle("getRaw"),
                ArgumentRequirement.ofSingle("c"),
                ArgumentRequirement.ofSingle("clear"),
                ArgumentRequirement.ofSingle("v"),
                ArgumentRequirement.ofSingle("view"),
                ArgumentRequirement.ofValue("getRaw"),
                ArgumentRequirement.ofValue("d"),
                ArgumentRequirement.ofValue("delete")
            )
        ) return
        val config: JSONObject = getConfig()
        val firstArg = arguments.optArgument(1)
        if (firstArg is TextArgument) {
            val key = firstArg.key
            val secondArg = arguments.optArgument(2)
            if (secondArg == null) {
                val obj = config.opt(key)
                if (obj != null) {
                    println((key + '=' + obj + " (" + Utils.getTypeText(obj.javaClass.getSimpleName())) + ")")
                } else {
                    println("null")
                }
                return
            }
            val value = secondArg.originArray[0]
            config.put(key, value)
            Utils.saveConfig(config)
            if (CMCL.isImmersiveMode) {
                CMCL.initConfig()
            }
        } else if (firstArg is SingleArgument) {
            val key = firstArg.key
            when (key) {
                "a", "all" -> PrintingUtils.printTable(
                    arrayOf(
                        CMCL.getString("TABLE_CONFIG_ALL_NAME"),
                        CMCL.getString("TABLE_CONFIG_ALL_TYPE"),
                        CMCL.getString("TABLE_CONFIG_ALL_VALUE")
                    ),
                    intArrayOf(25, 11, 30),
                    false,
                    *config.toMap().entries.map { (key1, value1) ->
                        val value = when (key1) {
                            "accounts" -> CMCL.getString("TABLE_CONFIG_ALL_VIEW_SEPARATELY", key1)
                            else -> value1.toString()
                        }
                        arrayOf<String>(key1, Utils.getTypeText(value1.javaClass.getSimpleName()), value)
                    }.toTypedArray<Array<String>>()
                )

                "c", "clear" -> {
                    Utils.saveConfig(JSONObject())
                    if (CMCL.isImmersiveMode) {
                        CMCL.initConfig()
                    }
                }

                "v", "view" -> {
                    PrintingUtils.printTable(
                        arrayOf<String>(
                            CMCL.getString("TABLE_SETTABLE_CONFIG_NAME"),
                            CMCL.getString("TABLE_SETTABLE_CONFIG_TYPE"),
                            CMCL.getString("TABLE_SETTABLE_CONFIG_MEANING")
                        ),
                        intArrayOf(25, 11, 30),
                        false,
                        *CMCL.getString("MESSAGE_CONFIGURATIONS_TABLE_CONTENT").splitByRegex("\n").map { item: String ->
                            item.splitByRegex("\\|")
                        }.toTypedArray<Array<String>>()
                    )
                    println(CMCL.getString("MESSAGE_CONFIGURATIONS_TIP"))
                }

                "getRaw" -> println(config.toString(2))
                else -> println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", firstArg.originString))
            }
        } else if (firstArg is ValueArgument) {
            val key = firstArg.key
            val arg = arguments.optArgument(1)
            val value = (arg as ValueArgument).value
            when (key) {
                "getRaw" -> {
                    val indentFactor = try {
                        value.toInt()
                    } catch (e: NumberFormatException) {
                        println(CMCL.getString("CONSOLE_UNSUPPORTED_VALUE", value))
                        return
                    }
                    println(config.toString(indentFactor))
                }

                "d", "delete" -> {
                    config.remove(value)
                    Utils.saveConfig(config)
                    if (CMCL.isImmersiveMode) {
                        CMCL.initConfig()
                    }
                }

                else -> println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", firstArg.originString))
            }
        }
    }

}
