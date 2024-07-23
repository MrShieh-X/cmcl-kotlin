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

import com.mrshiehx.cmcl.utils.Utils.isFullWidth
import org.jline.terminal.TerminalBuilder
import java.io.IOException
import java.util.*
import kotlin.math.max
import kotlin.math.min

object TableUtils {
    fun toTable(
        headersStringArray: Array<String>?,
        lengthsRatio: IntArray,
        splitSymbolForItems: Boolean,
        vararg itemsStringArray: Array<String>
    ): String {
        val actualMaxLengths = IntArray(lengthsRatio.size)
        var windowWidth = 71
        try {
            TerminalBuilder.terminal().use { terminal ->
                windowWidth = terminal.width
                if (windowWidth == 0) windowWidth = 71
            }
        } catch (ignore: IOException) {
        }
        var maximumLengthsRatioSum = 0
        for (i in lengthsRatio) {
            maximumLengthsRatioSum += i
        }
        val maxLengths = IntArray(lengthsRatio.size)
        for (i in maxLengths.indices) {
            maxLengths[i] = max(
                ((windowWidth - 1).toFloat() * (lengthsRatio[i].toFloat() / maximumLengthsRatioSum.toFloat())).toInt() - 3,
                2
            ) //"2" for a full-width character
        }

        val headers = headersStringArray?.let { MutableList(it.size) { i -> TableString(it[i]) } }
        var items: List<List<TableString>>? = null
        if (itemsStringArray.isNotEmpty()) {
            items = itemsStringArray.map { it.map { value -> TableString(value) } }
        }

        for (i in lengthsRatio.indices) {
            var maxLength = 0
            headers?.let { maxLength = it[i].maxLengthForLines }
            items?.forEach { item ->
                val length = item[i].maxLengthForLines
                if (length > maxLength) {
                    maxLength = length
                }
            }
            actualMaxLengths[i] = min(maxLength, maxLengths[i])
        }
        val stringBuilder = StringBuilder()
        stringBuilder.append('+')
        for (i in lengthsRatio.indices) {
            for (j in 0 until actualMaxLengths[i] + 2) {
                stringBuilder.append('-')
            }
            stringBuilder.append('+')
        }
        val split = stringBuilder.toString()
        stringBuilder.append('\n')
        if (headers != null) {
            printTableItem(stringBuilder, actualMaxLengths, headers)
            stringBuilder.append(split).append('\n')
        }
        items?.forEachIndexed { i, item ->
            printTableItem(stringBuilder, actualMaxLengths, item)
            if (splitSymbolForItems) {
                stringBuilder.append(split)
                if (i + 1 != items.size) stringBuilder.append('\n')
            }
        }
        if (!splitSymbolForItems) stringBuilder.append(split)
        return stringBuilder.toString()
    }

    private fun printTableItem(stringBuilder: StringBuilder, actualMaxLengths: IntArray, item: List<TableString>) {
        stringBuilder.append('|')
        val rests =
            arrayOfNulls<TableString?>(actualMaxLengths.size)//为什么不用item.length？这是为了避免用户给多了，item的length比header数还大
        var shouldPrintNext = false
        for (i in actualMaxLengths.indices) { //为什么不用item.length？这是为了避免用户给多了，item的length比header数还大
            stringBuilder.append(' ')
            var printedLength: Int
            var value = item[i]
            val multiLines = value.lines.size > 1
            if (multiLines) {
                value = TableString(value.lines[0])
            }
            var stringOccupiedSpacesLength: Int
            if (value.length.also { stringOccupiedSpacesLength = it } <= actualMaxLengths[i]) {
                stringBuilder.append(value)
                rests[i] = null
                printedLength = stringOccupiedSpacesLength
            } else {
                if (value.tableChars[actualMaxLengths[i] - 1] is TableHalfChar || value.tableChars[actualMaxLengths[i] - 1] is TableFullCharRight) {
                    val tableCharsTemp = List(actualMaxLengths[i]) { j -> value.tableChars[j] }
                    stringBuilder.append(TableString(tableCharsTemp))
                    val tableCharsTemp2 =
                        List(value.length - actualMaxLengths[i]) { j -> value.tableChars[j + actualMaxLengths[i]] }
                    rests[i] = TableString(tableCharsTemp2)
                    printedLength = actualMaxLengths[i]
                    shouldPrintNext = true
                } else if (value.tableChars[actualMaxLengths[i] - 1] is TableFullCharLeft) {
                    val tableCharsTemp = List(actualMaxLengths[i] - 1) { j -> value.tableChars[j] }
                    stringBuilder.append(TableString(tableCharsTemp))
                    val tableCharsTemp2 =
                        List(value.length - (actualMaxLengths[i] - 1)) { j -> value.tableChars[j + (actualMaxLengths[i] - 1)] }
                    rests[i] = TableString(tableCharsTemp2)
                    printedLength = actualMaxLengths[i] - 1
                    shouldPrintNext = true
                } else {
                    //this situation will not happen
                    printedLength = 0
                }
            }
            if (multiLines) {
                shouldPrintNext = true
                val list: MutableList<MutableList<TableChar>> = item[i].lines.map { it.toMutableList() }.toMutableList()
                val temp = rests[i]
                if (temp == null) {
                    list.removeAt(0)
                } else {
                    list[0] = temp.tableChars.toMutableList()
                }
                rests[i] = TableString(list.toTypedArray())
            }
            if (rests[i] == null)
                rests[i] = TableString("") //不设为null是因为打印其他value时这个也要读取
            for (j in 0 until actualMaxLengths[i] - printedLength + 1) {
                stringBuilder.append(' ')
            }
            stringBuilder.append('|')
        }
        stringBuilder.append('\n')
        if (shouldPrintNext) printTableItem(
            stringBuilder,
            actualMaxLengths,
            rests.map { it!! })//因为上面的 if (rests[i] == null) rests[i] = TableString("") 所以逻辑上不会为null
    }

    private class TableString {
        companion object {
            val LINE_SEPARATOR: TableChar = object : TableChar('\n') {}
        }

        val tableChars: List<TableChar>

        val length: Int

        val lines: List<List<TableChar>>

        val lengthsForLines: List<Int>

        val maxLengthForLines: Int

        constructor(lines: Array<List<TableChar>>) {
            tableChars = LinkedList()
            for (i in lines.indices) {
                val line: List<TableChar> = lines[i]
                tableChars.addAll(line)
                if (i + 1 != lines.size) {
                    tableChars.add(LINE_SEPARATOR)
                }
            }
            length = tableChars.size
            this.lines = lines.asList()
            lengthsForLines = this.lines.map { it.size }
            maxLengthForLines = Collections.max(lengthsForLines)
        }

        constructor(tableChars: List<TableChar>) {
            this.tableChars = tableChars
            length = tableChars.size
            lines = LinkedList<LinkedList<TableChar>>()
            lines.add(LinkedList())
            for (tableChar in tableChars) {
                if (tableChar === LINE_SEPARATOR) {
                    lines.add(LinkedList())
                } else {
                    lines[lines.size - 1].add(tableChar)
                }
            }
            lengthsForLines = this.lines.map { obj: List<TableChar?> -> obj.size }
            maxLengthForLines = Collections.max(lengthsForLines)
        }

        constructor(originalString: String) {
            val string = originalString.replace("\r\n", "\n").replace('\r', '\n') //此二者顺序不可变
            val charArray = string.toCharArray()
            tableChars = LinkedList()
            for (c in charArray) {
                if (c == '\n') {
                    tableChars.add(LINE_SEPARATOR)
                } else {
                    if (isFullWidth(c)) {
                        tableChars.add(TableFullCharLeft(c))
                        tableChars.add(TableFullCharRight(c))
                    } else {
                        tableChars.add(TableHalfChar(c))
                    }
                }
            }
            length = tableChars.size
            this.lines = LinkedList<LinkedList<TableChar>>()
            lines.add(LinkedList())
            for (tableChar in tableChars) {
                if (tableChar === LINE_SEPARATOR) {
                    lines.add(LinkedList())
                } else {
                    lines[lines.size - 1].add(tableChar)
                }
            }
            lengthsForLines = this.lines.map { it.size }
            maxLengthForLines = Collections.max(lengthsForLines)
        }

        override fun toString(): String {
            val list: MutableList<Char> = LinkedList()
            run {
                var i = 0
                while (i < tableChars.size) {
                    when (val tableChar = tableChars[i]) {
                        is TableHalfChar, LINE_SEPARATOR -> list.add(tableChar.c)
                        is TableFullCharLeft -> {
                            list.add(tableChar.c)
                            i++
                        }

                        is TableFullCharRight -> {
                            //ignore
                        }

                        else -> {}
                    }
                    i++
                }
            }
            val chars = CharArray(list.size)
            for (i in list.indices) {
                chars[i] = list[i]
            }
            return String(chars)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            val that = other as TableString
            return tableChars == that.tableChars
        }

        override fun hashCode(): Int {
            return tableChars.hashCode()
        }

    }

    private abstract class TableChar(val c: Char) {
        override fun toString(): String {
            return c.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            val tableChar = other as TableChar
            return c == tableChar.c
        }

        override fun hashCode(): Int {
            return Objects.hash(c)
        }
    }

    private class TableHalfChar(c: Char) : TableChar(c)
    private class TableFullCharLeft(c: Char) : TableChar(c)
    private class TableFullCharRight(c: Char) : TableChar(c)
}
