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

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils.getPathFromLibraryName
import java.io.File

class SplitLibraryName(
    val first: String,
    val second: String,
    val version: String,
    val classifier: String? = null,
    val extension: String? = ".jar"
) {
    companion object {
        fun valueOf(originLibraryName: String): SplitLibraryName? {
            if (originLibraryName.isEmpty()) return null//万一给的是""呢？所以要做此判断
            var extension = ".jar"
            var libraryName = originLibraryName
            if (libraryName.contains("@")) {
                extension = "." + libraryName.substring(libraryName.indexOf('@') + 1)
                libraryName = libraryName.substring(0, libraryName.indexOf('@'))
            }
            val nameSplit = libraryName.splitByRegex(":")
            if (nameSplit.size < 3) return null
            return SplitLibraryName(
                nameSplit[0],
                nameSplit[1],
                nameSplit[2],
                if (nameSplit.size >= 4) nameSplit[3] else null,
                extension
            )
        }
    }

    val fileName: String
        get() = second + "-" + version + (if (!classifier.isNullOrBlank()) "-$classifier" else "") + (extension ?: "")


    val physicalFile: File
        get() {
            val libraryFileName = fileName
            val libraryFileAndDirectoryName = getPathFromLibraryName(this)
            return File(File(CMCL.librariesDir, libraryFileAndDirectoryName), libraryFileName)
        }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(first).append(':').append(second).append(':').append(version)
        if (!classifier.isNullOrEmpty()) {
            sb.append(':').append(classifier)
        }
        if (".jar" != extension && !extension.isNullOrEmpty()) {
            sb.append('@').append(extension.substring(1))
        }
        return sb.toString()
    }
}