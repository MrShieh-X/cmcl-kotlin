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
package com.mrshiehx.cmcl.bean

import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex

class XDate(val year: Int, val month: Int, val day: Int) {
    override fun toString(): String {
        return "$year/$month/$day"
    }

    companion object {
        fun valueOf(date: String): XDate? {
            if (date.isBlank()) return null
            val dates = date.splitByRegex("/")
            if (dates.size < 3) return null
            try {
                return XDate(dates[0].toInt(), dates[1].toInt(), dates[2].toInt())
            } catch (ignore: Throwable) {
            }
            return null
        }

        /**
         * 比较日期
         *
         * @return 0 first > second;
         * 1 first < second;
         * 2 first = second
         */
        fun compareDate(first: XDate, second: XDate): Int {
            if (first.year > second.year) return 0
            if (first.year < second.year) return 1
            if (first.month > second.month) return 0
            if (first.month < second.month) return 1
            if (first.day > second.day) return 0
            return if (first.day < second.day) 1 else 2
        }
    }
}
