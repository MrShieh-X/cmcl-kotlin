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
package com.mrshiehx.cmcl.utils.cmcl.version

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.bean.GameVersion
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.inputStream2String
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.json.JSONUtils
import com.mrshiehx.cmcl.utils.system.OperatingSystem
import com.mrshiehx.cmcl.utils.system.SystemUtils
import org.jenkinsci.constant_pool_scanner.ConstantPoolScanner
import org.jenkinsci.constant_pool_scanner.ConstantType
import org.jenkinsci.constant_pool_scanner.StringConstant
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.jar.JarFile

object VersionUtils {
    val VERSION_COMPARATOR = Comparator { o1Origin: String, o2Origin: String ->
        var o1 = o1Origin.replace("3D-Shareware-v1.34", "3D-Shareware-v1-34")
            .replace("3D Shareware v1.34", "3D-Shareware-v1-34")
            .replace(" Pre-Release ", "-pre")
        var o2 = o2Origin.replace("3D-Shareware-v1.34", "3D-Shareware-v1-34")
            .replace("3D Shareware v1.34", "3D-Shareware-v1-34")
            .replace(" Pre-Release ", "-pre")
        val o1s: List<String> = Utils.xsplit(o1, "\\.")
        val o2s: List<String> = Utils.xsplit(o2, "\\.")
        val one = -1
        val none = 1
        if (o1s.isEmpty() || o2s.isEmpty()) {
            if (o1s.isEmpty() && o2s.isEmpty()) {
                if ("20w14infinite" == o1) {
                    o1 = "20w13c"
                }
                if ("20w14infinite" == o2) {
                    o2 = "20w13c"
                }
                if ("22w13oneblockatatime" == o1) {
                    o1 = "22w13b"
                }
                if ("22w13oneblockatatime" == o2) {
                    o2 = "22w13b"
                }
                if ("3D-Shareware-v1-34" == o1) {
                    o1 = "19w13c"
                }
                if ("3D-Shareware-v1-34" == o2) {
                    o2 = "19w13c"
                }
                if ("1.RV-Pre1" == o1) {
                    o1 = "16w13a"
                }
                if ("1.RV-Pre1" == o2) {
                    o2 = "16w13a"
                }
                if ("23w13a_or_b" == o1) {
                    o1 = "23w13b"
                }
                if ("23w13a_or_b" == o2) {
                    o2 = "23w13b"
                }

                if ("24w14potato" == o1) {
                    o1 = "24w14`"//有24w13a在24w14potato后，a的ascii码比`后
                }
                if ("24w14potato" == o2) {
                    o2 = "24w14`"//有24w13a在24w14potato后，a的ascii码比`后
                }

                val o1i = intArrayOf(o1.substring(0, 2).toInt(), o1.substring(3, 5).toInt(), o1[5].code)
                val o2i = intArrayOf(o2.substring(0, 2).toInt(), o2.substring(3, 5).toInt(), o2[5].code)
                for (i in 0..2) {
                    val o1ii = o1i[i]
                    val o2ii = o2i[i]
                    if (o1ii > o2ii) {
                        return@Comparator none
                    } else if (o1ii < o2ii) {
                        return@Comparator one
                    }
                }
                return@Comparator 0
            } else if (o1s.isEmpty()) {
                return@Comparator one
            } else {
                return@Comparator none
            }
        }
        val bigger: Int = o1s.size.coerceAtLeast(o2s.size)
        val o1i = IntArray(bigger + 2)
        val o2i = IntArray(bigger + 2)
        for (i in 0 until bigger) {
            if (i < o1s.size) {
                val o1String = o1s[i]
                if (o1String.contains("-pre")) {
                    o1i[i + 2] = o1String.substring(0, o1String.indexOf("-pre")).toInt()
                    o1i[1] = o1String.substring(o1String.indexOf("-pre") + 4).toInt()
                } else if (o1String.contains("-rc")) {
                    o1i[i + 2] = o1String.substring(0, o1String.indexOf("-rc")).toInt()
                    o1i[0] = o1String.substring(o1String.indexOf("-rc") + 3).toInt()
                } else {
                    o1i[i + 2] = o1String.toInt()
                    o1i[0] = -1
                    o1i[1] = -1
                }
            }
        }
        for (i in 0 until bigger) {
            if (i < o2s.size) {
                val o2String = o2s[i]
                if (o2String.contains("-pre")) {
                    o2i[i + 2] = o2String.substring(0, o2String.indexOf("-pre")).toInt()
                    o2i[1] = o2String.substring(o2String.indexOf("-pre") + 4).toInt()
                } else if (o2String.contains("-rc")) {
                    o2i[i + 2] = o2String.substring(0, o2String.indexOf("-rc")).toInt()
                    o2i[0] = o2String.substring(o2String.indexOf("-rc") + 3).toInt()
                } else {
                    o2i[i + 2] = o2String.toInt()
                    o2i[0] = -1
                    o2i[1] = -1
                }
            }
        }
        for (i in 2 until bigger + 2) {
            val o1ii = o1i[i]
            val o2ii = o2i[i]
            if (o1ii > o2ii) {
                return@Comparator none
            } else if (o1ii < o2ii) {
                return@Comparator one
            } else {
                if (i + 1 == bigger + 2) {
                    for (j in 0..1) {
                        val o1rp = o1i[j]
                        val o2rp = o2i[j]
                        if (o1rp > o2rp) {
                            return@Comparator none
                        } else if (o1rp < o2rp) {
                            return@Comparator one
                        }
                    }
                }
            }
        }
        0
    }

    fun getNativeLibraryName(originalPath: String): String {
        var path = originalPath
        var splitter = File.separator
        if (!path.contains(splitter) && !path.contains("\\") && !path.contains("/")) return path
        path = path.replace(File.separatorChar, '/')
        splitter = "/"
        val strings = path.splitByRegex(splitter)
        return if (strings.size < 3) path else strings[strings.size - 3]
    }

    val nativesDirName: String
        get() = "natives-" + OperatingSystem.CURRENT_OS.checkedName + "-" + SystemUtils.archCheckedName

    fun versionExists(name: String): Boolean {
        return File(
            CMCL.versionsDir,
            "$name/$name.json"
        ).exists() /*&& new File(ConsoleMinecraftLauncher.versionsDir, name + "/" + name + ".jar").exists()*/
    }

    fun getNativesDir(versionFile: File): File {
        val defa = nativesDirName
        val defaul = File(versionFile, defa)
        if (defaul.exists()) {
            val files = defaul.listFiles { obj: File -> obj.isFile() }
            if (files != null && files.isNotEmpty()) return defaul
        }
        val files =
            versionFile.listFiles { pathname: File -> pathname.isDirectory() && pathname.getName().startsWith(defa) }
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                val files2 = file.listFiles { obj: File -> obj.isFile() }
                if (files2 != null && files2.isNotEmpty()) return file
            }
        }
        return defaul
    }

    fun getVersionByJar(jarFile: File): GameVersion? {
        try {
            JarFile(jarFile).use { jar ->
                val versionEntry = jar.getEntry("version.json")
                if (versionEntry != null) {
                    val jsonObject = JSONUtils.parseJSONObject(inputStream2String(jar.getInputStream(versionEntry)))
                    if (jsonObject != null) {
                        val name = jsonObject.optString("name")
                        var id = jsonObject.optString("id")
                        if (id.contains(" / ")) {
                            id = id.splitByRegex(" / ")[0]
                        }
                        return GameVersion(id, name)
                    }
                }
                val mainClass = jar.getEntry("net/minecraft/client/Minecraft.class")
                if (mainClass != null) {
                    val pool = ConstantPoolScanner.parse(jar.getInputStream(mainClass), ConstantType.STRING)
                    for (stringConstant in pool.list(
                        StringConstant::class.java
                    )) {
                        val v = stringConstant.get()
                        val prefix = "Minecraft Minecraft "
                        if (v.startsWith(prefix) && v.length > prefix.length) {
                            return GameVersion(v.substring(prefix.length), null)
                        }
                    }
                }
                val serverClass = jar.getEntry("net/minecraft/server/MinecraftServer.class")
                if (serverClass != null) {
                    val pool = ConstantPoolScanner.parse(jar.getInputStream(serverClass), ConstantType.STRING)
                    val strings: MutableList<String> = LinkedList()
                    for (stringConstant in pool.list(StringConstant::class.java)) {
                        val v = stringConstant.get()
                        strings.add(v)
                    }
                    var indexOf = -1
                    for (i in strings.indices) {
                        if (strings[i].startsWith("Can't keep up!")) {
                            indexOf = i
                            break
                        }
                    }
                    if (indexOf >= 0) {
                        for (i in indexOf - 1 downTo 0) {
                            val s = strings[i]
                            if (s.matches(".*[0-9].*".toRegex())) {
                                return GameVersion(s, null)
                            }
                        }
                    }
                }
            }
        } catch (ignore: Exception) {
        }
        return null
    }

    fun getGameVersion(json: JSONObject, jar: File): GameVersion {
        val v = json.optString("gameVersion")
        if (!isEmpty(v)) return GameVersion(v, null)
        val gameVersion = getVersionByJar(jar)
        if (gameVersion != null) {
            val id = gameVersion.id
            val name = gameVersion.name
            if (!isEmpty(id)) return GameVersion(id, name)
        }
        return GameVersion(null, null)
    }

    /**
     * x / x.x / x.x.x / x.x.x.x / x.x.x.x.x / x.x.x.x.x.x / ...
     *
     * @return 0 if un-comparable or they are the same, 1 if v1 > v2, -1 if v1 < v2
     */
    fun tryToCompareVersion(v1: String, v2: String): Int {
        if (v1.isBlank() || v2.isBlank()) return 0
        return if (v1 == v2) 0 else try {
            val split1 = v1.splitByRegex("\\.")
            val split2 = v2.splitByRegex("\\.")
            /*if (split1.length == 0 || split2.length == 0) {
                return Integer.compare(Integer.parseInt(v1),Integer.parseInt(v2));
            }*/
            var s1l = split1.size
            var s1 = IntArray(s1l)
            for (i in 0 until s1l) {
                val split = split1[i]
                s1[i] = split.toInt()
            }
            var s2l = split2.size
            var s2 = IntArray(s2l)
            for (i in 0 until s2l) {
                val split = split2[i]
                s2[i] = split.toInt()
            }
            if (s1l > s2l) {
                val newS2 = IntArray(s1l)
                System.arraycopy(s2, 0, newS2, 0, s2l)
                s2 = newS2
                s2l = s1l
            } else if (s1l < s2l) {
                val newS1 = IntArray(s2l)
                System.arraycopy(s1, 0, newS1, 0, s1l)
                s1 = newS1
                s1l = s2l
            }
            for (i in 0 until s1l) {
                val s11 = s1[i]
                val s22 = s2[i]
                if (s11 > s22) {
                    return 1
                } else if (s11 < s22) {
                    return -1
                }
            }
            0
        } catch (ignore: Throwable) {
            0
        }
    }
}
