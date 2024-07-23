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
package com.mrshiehx.cmcl.utils.console

import com.mrshiehx.cmcl.utils.Utils
import java.util.*

object InteractionUtils {
    fun yesOrNo(tip: String): Boolean {
        val text = "$tip(Y/N) "
        return internalYesOrNo(text)
    }

    private fun internalYesOrNo(text: String): Boolean {
        print(text) //legal
        val scanner = Scanner(System.`in`)
        val input = try {
            scanner.nextLine()
        } catch (ignore: NoSuchElementException) {
            return false
        }
        if (input.equals("y", ignoreCase = true) || input.equals("yes", ignoreCase = true)) {
            return true
        } else if (input.equals("n", ignoreCase = true) || input.equals("no", ignoreCase = true)) {
            return false
        }
        return internalYesOrNo(text)
    }

    @JvmOverloads
    fun inputInt(
        tip: String, smallest: Int, biggest: Int, exitWithSpecialValue: Boolean = false, specialValue: Int = 0
    ): Int? {
        return internalInputInt(tip, "", smallest, biggest, exitWithSpecialValue, specialValue)
    }

    private fun internalInputInt(
        origin: String,
        text: String,
        smallest: Int,
        biggest: Int,
        exitWithSpecialValue: Boolean,
        specialValue: Int
    ): Int? {
        print(text + origin) //legal
        val scanner = Scanner(System.`in`)
        val input: String = try {
            scanner.nextLine()
        } catch (ignore: NoSuchElementException) {
            return null
        }
        val value: Int = try {
            input.toInt()
        } catch (e: Exception) {
            return internalInputInt(
                origin,
                Utils.getString("CONSOLE_INPUT_INT_WRONG"),
                smallest,
                biggest,
                exitWithSpecialValue,
                specialValue
            )
        }
        if (exitWithSpecialValue && value == specialValue) return specialValue
        return if (value < smallest || value > biggest) {
            internalInputInt(
                origin,
                Utils.getString("CONSOLE_INPUT_INT_WRONG"),
                smallest,
                biggest,
                exitWithSpecialValue,
                specialValue
            )
        } else value
    }

    fun inputStringInFilter(tip: String, wrongTip: String, filter: (String) -> Boolean): String? {
        return internalInputStringInFilter(tip, tip, wrongTip, filter)
    }

    private fun internalInputStringInFilter(
        text: String,
        origin: String,
        wrongTip: String,
        filter: (String) -> Boolean
    ): String? {
        print(text) //legal
        val scanner = Scanner(System.`in`)
        val input: String = try {
            scanner.nextLine()
        } catch (ignore: NoSuchElementException) {
            return null
        }
        if (filter(input)) return input
        return if (Utils.isEmpty(input)) {
            internalInputStringInFilter(text, origin, wrongTip, filter)
        } else internalInputStringInFilter(String.format(wrongTip, input) + origin, origin, wrongTip, filter)
    }

    fun inputString(tip: String): String? {
        return internalInputString(tip, tip)
    }

    private fun internalInputString(text: String, origin: String): String? {
        print(text) //legal
        val scanner = Scanner(System.`in`)
        val input: String = try {
            scanner.nextLine()
        } catch (ignore: NoSuchElementException) {
            return null
        }
        return if (Utils.isEmpty(input)) {
            internalInputString(text, origin)
        } else {
            input
        }
    }
}
