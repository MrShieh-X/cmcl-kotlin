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
import com.mrshiehx.cmcl.bean.arguments.ValueArgument
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getConfig
import org.json.JSONObject

object SimplifyCommandFunction : Function {
    override val usageName = "simplify"

    override fun execute(arguments: Arguments) {
        //因为参数内容可能会被误判，并且好像没有检测的必要，所以不检测
        val config: JSONObject = getConfig()
        val simplifyCommands =
            config.optJSONObject("simplifyCommands") ?: JSONObject().also { config.put("simplifyCommands", it) }
        val firstArg = arguments.optArgument(1)
        if (firstArg is SingleArgument) {
            val firstKey = firstArg.key
            if (firstKey == "p" || firstKey == "print") {
                println(simplifyCommands.toString(2))
            } else {
                println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", firstArg.originString))
                return
            }
        } else if (firstArg is ValueArgument) {
            val firstKey = firstArg.key
            val simplifiedCommand = firstArg.value
            when (firstKey) {
                "s", "set" -> {
                    val originCommandArg = arguments.optArgument(2)
                    if (originCommandArg == null) {
                        println(CMCL.getString("CONSOLE_IMMERSIVE_MISSING_PARAMETER"))
                        return
                    }
                    val originCommand = originCommandArg.originString
                    simplifyCommands.put(simplifiedCommand, originCommand)
                    Utils.saveConfig(config)
                }

                "d", "delete" -> {
                    simplifyCommands.remove(simplifiedCommand)
                    Utils.saveConfig(config)
                }

                else -> println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", firstArg.originString))
            }
        } else {
            println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", firstArg!!.originString))
        }
    }

}
