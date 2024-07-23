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
package com.mrshiehx.cmcl.utils.system

import com.mrshiehx.cmcl.utils.Utils
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.regex.Pattern

object JavaUtils {
    val defaultJavaPath: String
        get() {
            val s = System.getProperty("java.home")
            return if (Utils.isEmpty(s)) ""
            else File(s, if (SystemUtils.isWindows) "bin\\java.exe" else "bin/java").absolutePath
        }

    fun getOriginalJavaVersion(javaFile: String): String? {
        try {
            var version: String? = null
            var process = ProcessBuilder(javaFile, "-XshowSettings:properties", "-version").start()
            BufferedReader(InputStreamReader(process.errorStream, OperatingSystem.NATIVE_CHARSET)).use { reader ->
                var line: String
                while (reader.readLine().also { line = it } != null) {
                    val m = Pattern.compile("java\\.version = (?<version>.*)").matcher(line)
                    if (m.find()) {
                        version = m.group("version")
                        break
                    }
                }
            }
            if (version == null) {
                process = ProcessBuilder(javaFile, "-version").start()
                BufferedReader(InputStreamReader(process.errorStream, OperatingSystem.NATIVE_CHARSET)).use { reader ->
                    var line: String
                    while (reader.readLine().also { line = it } != null) {
                        val m = Pattern.compile("version \"(?<version>(.*?))\"").matcher(line)
                        if (m.find()) {
                            version = m.group("version")
                            break
                        }
                    }
                }
            }
            return version
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    fun getJavaVersion(javaFile: String): Int? {
        try {
            var version = getOriginalJavaVersion(javaFile)
            if (version != null) {
                if (version.startsWith("1.")) {
                    version = version.substring(2, 3)
                } else {
                    val dot = version.indexOf(".")
                    if (dot != -1) {
                        version = version.substring(0, dot)
                    }
                }
                return version.toInt()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    val javaVersion: Int
        get() {
            var version = System.getProperty("java.version")
            if (version.startsWith("1.")) {
                version = version.substring(2, 3)
            } else {
                val dot = version.indexOf(".")
                if (dot != -1) {
                    version = version.substring(0, dot)
                }
            }
            return version.toInt()
        }
}
