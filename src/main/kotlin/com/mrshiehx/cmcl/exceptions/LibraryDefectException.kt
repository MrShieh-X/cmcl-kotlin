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

package com.mrshiehx.cmcl.exceptions

import com.mrshiehx.cmcl.bean.Library

class LibraryDefectException(val list: List<Library>) : LaunchException
    (
    String.format(
        if (list.size == 1) "the library file is not found: %s" else "library files below are not found:\n%s",
        toS(list)
    )
) {
    companion object {
        private fun toS(list: List<Library>): String {
            if (list.size == 1) {
                return list[0].libraryJSONObject.optString("name")
            }
            val stringBuilder = java.lang.StringBuilder()
            for (i in list.indices) {
                val library = list[i]
                stringBuilder.append("       ").append(library.libraryJSONObject.optString("name"))
                if (i + 1 != list.size) {
                    stringBuilder.append('\n')
                }
            }
            return stringBuilder.toString()
        }
    }
}