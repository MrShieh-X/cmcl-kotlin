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

import com.mrshiehx.cmcl.bean.arguments.ArgumentRequirement
import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.utils.Utils.getString

interface Function {
    fun execute(arguments: Arguments)

    val usageName: String?

    companion object {
        fun checkArgs(
            arguments: Arguments,
            leastCount: Int,
            offsetForOrigin: Int,
            vararg argumentRequirements: ArgumentRequirement
        ): Boolean {
            if (arguments.size >= leastCount) {
                val unruly = arguments.exclude(argumentRequirements.toList(), offsetForOrigin)
                if (unruly.size == 1) {
                    println(getString("CONSOLE_ARG_CHECKING_ONE", unruly[0].originString))
                    return false
                } else if (unruly.size > 1) {
                    val sb = StringBuilder()
                    unruly.forEach { argument -> sb.append(argument.originString).append('\n') }
                    print(getString("CONSOLE_ARG_CHECKING_PLURAL", sb))
                    return false
                }
            }
            return true
        }
    }
}
