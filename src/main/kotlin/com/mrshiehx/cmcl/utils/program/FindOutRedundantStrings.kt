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
package com.mrshiehx.cmcl.utils.program

import com.mrshiehx.cmcl.constants.languages.LanguageEnum
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*

@Throws(IOException::class)
fun main3(args: Array<String>) {
    println("en:")
    FindOutRedundantStrings.main(LanguageEnum.ENGLISH.textMap)
    println()
    println()
    println()
    println("can:")//because Cantonese includes all the text in Simplified Chinese
    FindOutRedundantStrings.main(LanguageEnum.CANTONESE.textMap)
}

object FindOutRedundantStrings {
    @Throws(IOException::class)
    fun main(map: Map<String, String>) {
        val dir = File("build/classes/kotlin/main")
        val files: MutableList<File> = LinkedList()
        traverseGetFiles(files, dir)
        val contents: MutableList<String> = LinkedList()
        for (file in files) {
            if (!file.getName().startsWith("Cantonese") && !file.getName()
                    .startsWith("SimplifiedChinese") && !file.getName().startsWith("English")
            // && file.getName() != FindOutRedundantStrings::class.simpleName + ".class"
            ) contents.add(readFileContent(file, true))
        }
        val final: MutableSet<String> = HashSet()
        for (name in map.keys) {
            var contains = false
            a@ for (content in contents) {
                if (content.contains(name)) {
                    contains = true
                    break@a
                }
            }
            if (!contains) final.add(name)
        }
        for (s in final) {
            println(s)
        }
    }

    @Throws(IOException::class)
    fun readFileContent(file: File, switchLine: Boolean): String {
        val sbf = StringBuilder()
        val reader = BufferedReader(FileReader(file))
        var tempStr: String?
        while (reader.readLine().also { tempStr = it } != null) {
            sbf.append(tempStr)
            if (switchLine) sbf.append('\n')
        }
        reader.close()
        return sbf.toString()
    }

    fun traverseGetFiles(list: MutableList<File>, file: File) {
        if (!file.exists()) return
        if (file.isFile()) {
            list.add(file)
        } else {
            val files = file.listFiles() ?: return
            for (file1 in files) {
                if (file1.isFile()) {
                    if (file1.getName() != "strings.xml" && !file1.getName().endsWith(".png"))
                        list.add(file1)
                } else {
                    traverseGetFiles(list, file1)
                }
            }
        }
    }
}
