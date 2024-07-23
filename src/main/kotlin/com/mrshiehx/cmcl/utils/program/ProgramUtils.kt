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
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.program.ProgramUtils.compareTwoLanguages
import java.util.*

fun main2(args: Array<String>) {
    println("in En not in Can: ")
    compareTwoLanguages(LanguageEnum.ENGLISH.textMap, LanguageEnum.CANTONESE.textMap)
    println()
    println("in Can not in En: ")
    compareTwoLanguages(LanguageEnum.CANTONESE.textMap, LanguageEnum.ENGLISH.textMap)
    println()
    println("in Can not in Zh: ")
    compareTwoLanguages(LanguageEnum.CANTONESE.textMap, LanguageEnum.SIMPLIFIED_CHINESE.textMap)
    println()
    /*System.out.println("in Zh not in Can: ");
    compareTwoLanguages(Languages.getZh(),Languages.getCantonese());//永远为空
    System.out.println();*/
}

object ProgramUtils {
    /**
     * 返回存在于x而不存在于y中的字符串名称
     */
    fun compareTwoLanguages(x: Map<String, String>, y: Map<String, String>) {
        x.entries.stream().filter { (key) -> isEmpty(y[key]) }.forEach { println(it.key) }
    }

    fun printStringsThatChineseNotHave() {
        val fin: MutableList<String> = LinkedList()
        for ((s) in LanguageEnum.ENGLISH.textMap) {
            if (isEmpty(LanguageEnum.SIMPLIFIED_CHINESE.textMap[s])) {
                fin.add(s)
            }
        }
        fin.forEach { println(it) }
    }

    fun printMap(map: Map<*, *>) {
        for ((key, value) in map) {
            println(key.toString() + ": " + value)
        }
    }

    fun compareLibrary(xStr: String, yStr: String) {
        val x = xStr.splitByRegex(";")
        val y = yStr.splitByRegex(";")
        val xHaveYNo = LinkedList<String>()
        val yHaveXNo: MutableList<String> = LinkedList()
        for (old in x) {
            if (!y.contains(old)) xHaveYNo.add(old)
        }
        for (nee in y) {
            if (!x.contains(nee)) yHaveXNo.add(nee)
        }
        println("xHaveYNo:")
        for (k in xHaveYNo) {
            println(k)
        }
        println()
        println()
        println()
        println()
        println()
        println("yHaveXNo:")
        for (k in yHaveXNo) {
            println(k)
        }
    }
}
