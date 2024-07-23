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

import com.sun.management.OperatingSystemMXBean
import java.awt.Desktop
import java.io.IOException
import java.lang.management.ManagementFactory
import java.net.URI

object SystemUtils {
    private val linuxBrowsers = arrayOf(
        "xdg-open",
        "google-chrome",
        "firefox",
        "microsoft-edge",
        "opera",
        "konqueror",
        "mozilla"
    )

    fun openLink(link: String) {
        if (link.isBlank()) return
        if (!Desktop.isDesktopSupported()) return
        Thread(Runnable {
            if (OperatingSystem.CURRENT_OS == OperatingSystem.LINUX) {
                for (browser in linuxBrowsers) {
                    try {
                        Runtime.getRuntime().exec(arrayOf<String>("which", browser)).inputStream.use { `is` ->
                            if (`is`.read() != -1) {
                                Runtime.getRuntime().exec(arrayOf(browser, link))
                                return@Runnable
                            }
                        }
                    } catch (ignored: Throwable) {
                    }
                    //Logging.LOG.log(Level.WARNING, "No known browser found");
                }
            }
            try {
                Desktop.getDesktop().browse(URI(link))
            } catch (e: Throwable) {
                if (OperatingSystem.CURRENT_OS == OperatingSystem.OSX) try {
                    Runtime.getRuntime().exec(arrayOf("/usr/bin/open", link))
                } catch (e2: IOException) {
                    //Logging.LOG.log(Level.WARNING, "Unable to open link: " + link, e2);
                }
                //Logging.LOG.log(Level.WARNING, "Failed to open link: " + link, e);
            }
        }).start()

    }

    val isWindows: Boolean
        get() = OperatingSystem.CURRENT_OS == OperatingSystem.WINDOWS
    val defaultMemory: Long
        get() = (ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean).totalPhysicalMemorySize / (4 * 1024 * 1024)
    val archInt: String
        get() {
            val value = System.getProperty("os.arch").trim { it <= ' ' }.lowercase()
            return when (value) {
                "x8664", "x86-64", "x86_64", "amd64", "ia32e", "em64t", "x64", "arm64", "aarch64", "mips64", "mips64el", "riscv", "risc-v", "ia64", "ia64w", "itanium64", "sparcv9", "sparc64", "ppc64le", "powerpc64le", "loongarch64", "s390x", "ppc64", "powerpc64" -> "64"
                "x8632", "x86-32", "x86_32", "x86", "i86pc", "i386", "i486", "i586", "i686", "ia32", "x32", "arm", "arm32", "mips", "mips32", "mipsel", "mips32el", "ia64n", "sparc", "sparc32", "ppc", "ppc32", "powerpc", "powerpc32", "s390", "ppcle", "ppc32le", "powerpcle", "powerpc32le", "loongarch32" -> "32"
                else -> {
                    if (value.startsWith("armv7")) {
                        return "32"
                    }
                    if (value.startsWith("armv8") || value.startsWith("armv9")) {
                        "64"
                    } else "unknown"
                }
            }
        }
    val archCheckedName: String
        get() {
            val value = System.getProperty("os.arch").trim { it <= ' ' }.lowercase()
            return when (value) {
                "x8664", "x86-64", "x86_64", "amd64", "ia32e", "em64t", "x64" -> "x86_64"
                "x8632", "x86-32", "x86_32", "x86", "i86pc", "i386", "i486", "i586", "i686", "ia32", "x32" -> "x86"
                "arm64", "aarch64" -> "arm64"
                "arm", "arm32" -> "arm32"
                "mips64" -> "mips64"
                "mips64el" -> "mips64el"
                "mips", "mips32" -> "mips"
                "mipsel", "mips32el" -> "mipsel"
                "riscv", "risc-v" -> "riscv"
                "ia64", "ia64w", "itanium64" -> "ia64"
                "ia64n" -> "ia32"
                "sparcv9", "sparc64" -> "sparcv9"
                "sparc", "sparc32" -> "sparc"
                "ppc64", "powerpc64" -> if ("little" == System.getProperty("sun.cpu.endian")) "ppc64le" else "ppc64"
                "ppc64le", "powerpc64le" -> "ppc64le"
                "ppc", "ppc32", "powerpc", "powerpc32" -> "ppc"
                "ppcle", "ppc32le", "powerpcle", "powerpc32le" -> "ppcle"
                "s390" -> "s390"
                "s390x" -> "s390x"
                "loongarch32" -> "loongarch32"
                "loongarch64" -> "loongarch64"
                else -> {
                    if (value.startsWith("armv7")) {
                        return "arm32"
                    }
                    if (value.startsWith("armv8") || value.startsWith("armv9")) {
                        "arm64"
                    } else "unknown"
                }
            }
        }
}
