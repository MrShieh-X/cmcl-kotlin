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

import java.util.*

object CommandUtils {
    fun splitCommand(src: String): List<String> {
        val list: MutableList<String> = LinkedList()
        var quoting = false
        val chars = src.toCharArray()
        for (i in src.indices) {
            val str = chars[i]
            var modifiedQuoting = false
            if (str == '\"' && (i == 0 || chars[i - 1] != '\\')) {
                quoting = !quoting
                modifiedQuoting = true
            }
            if (!quoting) {
                if (!modifiedQuoting) {
                    if (str != ' ') {
                        if (i == 0) list.add(str.toString()) else list[list.size - 1] = list[list.size - 1] + str
                    } else {
                        list.add("")
                    }
                }
            } else {
                if (!modifiedQuoting) {
                    if (list.size > 0) {
                        list[list.size - 1] = list[list.size - 1] + str
                    } else {
                        list.add("" + str)
                    }
                }
            }
        }
        if (list.size == 0) {
            list.add(src)
        }
        return list
    }

    fun clearRedundantSpaces(string: String): String {
        val sourceChars = string.toCharArray()
        val space = Any()
        val objects = arrayOfNulls<Any>(string.length)
        var quoting = false
        for (i in sourceChars.indices) {
            val cha = sourceChars[i]
            if (cha == '\"' && (i == 0 || sourceChars[i - 1] != '\\')) {
                quoting = !quoting
            }
            objects[i] = if (!quoting && cha == ' ') space else cha
        }
        val list: MutableList<Char> = LinkedList()
        run {
            var i = 0
            while (i < objects.size) {
                val `object` = objects[i]
                if (`object` === space) {
                    list.add(' ')
                    for (j in i until objects.size) {
                        if (objects[j] !== space) {
                            i = j - 1
                            break
                        }
                    }
                } else {
                    list.add(`object` as Char)
                }
                i++
            }
        }
        val chars = CharArray(list.size)
        for (i in list.indices) {
            chars[i] = list[i]
        }
        return if (quoting) (String(chars) + "\"").trim { it <= ' ' } else String(chars).trim { it <= ' ' } //Judging whether it is still quoted, that is, judging whether the double quotes are complete
    }

    fun argsToCommand(args: List<String>): String {
        val stringBuilder = StringBuilder()
        for (i in args.indices) {
            var str = args[i]
            if (str.contains(" ")) {
                str = "\"" + str + "\""
                if (str.contains("\\")) {
                    str = str.replace("\\", "\\\\")
                }
            }
            stringBuilder.append(str)
            if (i + 1 != args.size) {
                stringBuilder.append(" ")
            }
        }
        return stringBuilder.toString()
    }

    fun powershellString(str: String): String = "'" + str.replace("'", "''") + "'"

}
