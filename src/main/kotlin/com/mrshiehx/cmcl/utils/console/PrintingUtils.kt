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

import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils
import org.jline.terminal.TerminalBuilder
import java.io.IOException

object PrintingUtils {
    fun oldNormalizeListItemsToText(list: List<String>, reverse: Boolean): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append('[')
        var i = if (!reverse) 0 else list.size - 1
        while (if (!reverse) i < list.size else i >= 0) {
            var item = list[i]
            if (item.contains(" ")) item = "\"" + item + "\""
            stringBuilder.append(item) //legal
            if (i > 0) {
                stringBuilder.append(", ")
            }
            if (!reverse) i++ else i--
        }
        stringBuilder.append(']')
        return stringBuilder.toString()
    }

    fun normalizeListItemsToText(
        items: List<String>,
        reverse: Boolean,
        columnsNumIfFullLineCannot: Int,
        separatingSpaceLength: Int,
        originalTryFullLine: Boolean
    ): String {
        var tryFullLine = originalTryFullLine
        var windowWidth = 0
        if (tryFullLine) {
            try {
                TerminalBuilder.terminal().use { terminal ->
                    windowWidth = terminal.width
                    if (windowWidth <= 0) tryFullLine = false
                }
            } catch (ignore: IOException) {
                tryFullLine = false
            }
        }
        if (!tryFullLine) {
            return normalizeListItemsToText(items, reverse, columnsNumIfFullLineCannot, separatingSpaceLength)
        }
        var i = 1
        var previous: String? = null
        var now: String? = null
        var max = 0
        while (max <= windowWidth) {
            previous = now
            now = normalizeListItemsToText(items, reverse, i++, separatingSpaceLength)
            val split = now.splitByRegex("\n")
            if (split.size == 1) {
                return now
            }
            max = 0
            for (a in split) {
                var length: Int
                if (Utils.stringOccupiedSpacesLength(a).also { length = it } > max) {
                    max = length
                }
            }
        }
        return previous ?: normalizeListItemsToText(items, reverse, columnsNumIfFullLineCannot, separatingSpaceLength)
    }

    fun normalizeListItemsToText(
        originalItems: List<String>,
        reverse: Boolean,
        columnsNum: Int,
        separatingSpaceLength: Int
    ): String {
        var items = originalItems
        val maxLength = IntArray(columnsNum)
        items = items.map { item -> if (item.contains(" ")) "\"" + item + "\"" else item }
        if (reverse) items = items.reversed()
        for (i in items.indices) {
            val itemLength: Int = Utils.stringOccupiedSpacesLength(items[i])
            val columnIndex = i % columnsNum
            if (maxLength[columnIndex] < itemLength) {
                maxLength[columnIndex] = itemLength
            }
        }
        val stringBuilder = StringBuilder()
        for (i in items.indices) {
            val item = items[i]
            stringBuilder.append(item)
            if ((i + 1) % columnsNum != 0) {
                for (j in 0 until maxLength[i % columnsNum] - Utils.stringOccupiedSpacesLength(item) + separatingSpaceLength) {
                    stringBuilder.append(" ")
                }
            } else if (i + 1 != items.size) {
                stringBuilder.append("\n")
            }
        }
        return stringBuilder.toString()
    }

    fun printListItems(list: List<String>, reversed: Boolean, columnsNum: Int, separatingSpaceLength: Int) {
        println(normalizeListItemsToText(list, reversed, columnsNum, separatingSpaceLength))
    }

    fun printListItems(
        list: List<String>,
        reversed: Boolean,
        columnsNum: Int,
        separatingSpaceLength: Int,
        tryFullLine: Boolean
    ) {
        println(normalizeListItemsToText(list, reversed, columnsNum, separatingSpaceLength, tryFullLine))
    }

    fun printTable(
        headers: Array<String>,
        lengthsRatio: IntArray,
        splitSymbolForItems: Boolean,
        vararg items: Array<String>
    ) {
        println(TableUtils.toTable(headers, lengthsRatio, splitSymbolForItems, *items))
    }
}