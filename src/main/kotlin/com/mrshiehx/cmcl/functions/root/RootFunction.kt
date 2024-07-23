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
import com.mrshiehx.cmcl.bean.arguments.SingleArgument
import com.mrshiehx.cmcl.bean.arguments.TextArgument
import com.mrshiehx.cmcl.bean.arguments.ValueArgument
import com.mrshiehx.cmcl.functions.Function
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getConfig
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils

object RootFunction : Function {
    override fun execute(arguments: Arguments) {
        execute(arguments, false)
    }

    override val usageName = null
    fun execute(arguments: Arguments, isImmersive: Boolean) {
        val firstArgument = arguments.optArgument(0)!!
        val key = firstArgument.key
        val originString = firstArgument.originString
        val originArray = firstArgument.originArray
        if (!isImmersive) {
            when (firstArgument) {
                is SingleArgument -> {
                    when (key) {
                        "h", "help" -> printHelp()
                        "a", "about" -> AboutPrinter.execute()
                        //"i", "immersive" -> ImmersiveMode.getIn()
                        "c", "check-for-updates" -> UpdatesChecker.execute()
                        "l", "list" -> VersionsLister.execute(null)
                        "p", "print" -> {
                            val selectedVersion: String = getConfig().optString("selectedVersion")
                            if (Utils.isEmpty(selectedVersion)) {
                                println(CMCL.getString("MESSAGE_TO_SELECT_VERSION"))
                                return
                            }
                            LaunchCommands.print(selectedVersion)
                        }

                        else -> tryToStartVersion(originArray[0])
                    }
                }

                is ValueArgument -> {
                    val value = firstArgument.value
                    when (key) {
                        "l", "list" -> VersionsLister.execute(value)
                        "p", "print" -> LaunchCommands.print(value)
                        "s", "select" -> VersionSelector.execute(value)
                        "h", "help" -> println(getString("CONSOLE_HELP_WRONG_WRITE", originString))
                        else -> tryToStartVersion(originArray[0])
                    }
                }

                else -> {
                    tryToStartVersion(originArray[0])
                }
            }
        } else {
            if (firstArgument !is TextArgument) {
                println(getString("CONSOLE_IMMERSIVE_WRONG", firstArgument.originString))
                return
            }
            val secondArgument = arguments.optArgument(1)
            when (key) {
                "help" -> printHelp()
                "about" -> AboutPrinter.execute()
                "immersive" -> ImmersiveMode.getIn()
                "check-for-updates" -> UpdatesChecker.execute()
                "list" -> VersionsLister.execute(secondArgument?.originArray?.get(0))
                "print" -> secondArgument?.originArray?.get(0)?.let { LaunchCommands.print(it) }
                "select" -> {
                    if (secondArgument == null) {
                        println(getString("CONSOLE_IMMERSIVE_MISSING_PARAMETER"))
                        return
                    }
                    VersionSelector.execute(secondArgument.originArray[0])
                }

                "start" -> VersionStarter.execute(secondArgument?.originArray?.get(0))
                else -> println(getString("CONSOLE_IMMERSIVE_NOT_FOUND", key))
            }
        }
    }

    private fun tryToStartVersion(versionName: String) {
        if (VersionUtils.versionExists(versionName)) {
            VersionStarter.execute(versionName)
        } else {
            println(getString("CONSOLE_NOT_FOUND_VERSION_OR_OPTION", versionName))
        }
        //如果版本不存在，提示打错命令或者目标版本不存在
    }

    fun printHelp() {
        println(CMCL.getHelpDocumentation("ROOT"))
    }
}
