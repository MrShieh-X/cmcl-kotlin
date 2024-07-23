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
package com.mrshiehx.cmcl.functions.root

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.exceptions.NotSelectedException
import com.mrshiehx.cmcl.functions.Functions
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.AccountUtils
import com.mrshiehx.cmcl.utils.console.CommandUtils
import org.json.JSONObject
import java.util.*
import kotlin.system.exitProcess

object ImmersiveMode {
    fun getIn() {
        CMCL.isImmersiveMode = true
        while (true) {
            if (Constants.ECHO_OPEN_FOR_IMMERSIVE) {
                printPrompt()
            }
            val scanner = Scanner(System.`in`)
            val s: String? = try {
                scanner.nextLine()
            } catch (e: NoSuchElementException) {
                return
            }
            if (!isEmpty(s)) {
                when (val command = format(s)) {
                    "echo off" -> Constants.ECHO_OPEN_FOR_IMMERSIVE = false
                    "echo on" -> Constants.ECHO_OPEN_FOR_IMMERSIVE = true
                    "exit" -> exitProcess(0)


                    else -> {
                        val list = CommandUtils.splitCommand(command)
                        val args = Arguments(list, true)
                        if (args.size == 0) continue
                        val argument = args.optArgument(0) ?: continue
                        val key = argument.key
                        if ("help" == key) {
                            RootFunction.printHelp()
                            break
                        }
                        val function = Functions[key]
                        if (function == null) {
                            //交给RootOption
                            RootFunction.execute(args, true)
                            break
                        } else {
                            val usage = args.optArgument(1)
                            if (usage != null && (usage.equals("help") || usage.equals("h"))) {
                                val name = function.usageName
                                if (!isEmpty(name)) {
                                    println(CMCL.getHelpDocumentation(name))
                                } else {
                                    RootFunction.printHelp()
                                }
                            } else {
                                try {
                                    function.execute(args)
                                } catch (e: Throwable) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun format(command: String): String {
        return if (!isEmpty(command)) CommandUtils.clearRedundantSpaces(command) else ""
    }

    private fun printPrompt() {
        //MrShiehX@1.18.2$
        var account: JSONObject? = null
        val config: JSONObject = Utils.getConfig()
        try {
            account = AccountUtils.getSelectedAccount(config, false)
        } catch (ignore: NotSelectedException) {
        }
        val selectedVersion = config.optString("selectedVersion")
        val stringBuilder = StringBuilder()
        if (account != null && !isEmpty(account.optString("playerName"))) {
            stringBuilder.append(account.optString("playerName"))
            if (!isEmpty(selectedVersion)) {
                stringBuilder.append('@')
            }
        }
        if (!isEmpty(selectedVersion)) {
            stringBuilder.append(selectedVersion)
        }
        stringBuilder.append("$ ")
        print(stringBuilder) //legal
    }
}
