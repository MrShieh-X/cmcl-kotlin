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

package com.mrshiehx.cmcl.utils

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.api.download.DownloadSource
import com.mrshiehx.cmcl.bean.SplitLibraryName
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import org.json.JSONObject
import java.io.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

object Utils {
    fun getString(name: String): String = CMCL.getString(name)
    fun getString(name: String, vararg objects: Any): String = CMCL.getString(name, *objects)
    fun <K, V> mapOf(pairs: Iterable<Pair<K, V>>): Map<K, V> {
        val map: MutableMap<K, V> = LinkedHashMap()
        for ((key, value) in pairs) map[key] = value
        return map
    }

    @OptIn(ExperimentalContracts::class)
    fun isEmpty(c: CharSequence?): Boolean {
        contract {
            returns(false) implies (c != null)
        }
        return c.isNullOrEmpty()
    }

    @OptIn(ExperimentalContracts::class)
    fun isBlank(c: CharSequence?): Boolean {
        contract {
            returns(false) implies (c != null)
        }
        return c.isNullOrBlank()
    }

    fun valueOf(value: Any?): String = value?.toString() ?: ""
    fun saveConfig(config: JSONObject) = CMCL.saveConfig(config)
    fun getConfig(): JSONObject = CMCL.config
    fun <X> removeDuplicate(list: MutableList<X>): MutableList<X> {
        for (i in 0..<list.size - 1) {
            for (j in list.size - 1 downTo i + 1) {
                if (list[j] == list[i]) {
                    list.removeAt(j)
                }
            }
        }
        return list
    }

    fun printfln(str: String, vararg `object`: Any) {
        System.out.printf(str, *`object`) //legal
        println()
    }

    fun printflnErr(str: String, vararg `object`: Any?) {
        System.out.printf(str, *`object`) //legal
        println()
    }

    @Throws(IOException::class)
    fun inputStream2String(stream: InputStream): String {
        return stream.use { stream1 ->
            val result = ByteArrayOutputStream()
            val buf = ByteArray(8192)
            while (true) {
                val len = stream1.read(buf)
                if (len == -1) break
                result.write(buf, 0, len)
            }
            return@use result.toString(StandardCharsets.UTF_8.name())
        }
    }

    @Throws(IOException::class)
    fun inputStream2String(stream: InputStream, length: Int): String {
        return stream.use { `is` ->
            val result = ByteArrayOutputStream()
            val buf = ByteArray(length)
            val len = `is`.read(buf, 0, length)
            result.write(buf, 0, len)
            return@use result.toString(StandardCharsets.UTF_8.name())
        }
    }

    @Throws(IOException::class)
    fun downloadVersionsFile(): File {
        val cmcl = CMCL.CMCLWorkingDirectory
        cmcl.mkdirs()
        val versionsFile = File(cmcl, "versions.json")
        FileUtils.createFile(versionsFile, true)
        DownloadUtils.downloadFile(DownloadSource.getProvider().versionManifest(), versionsFile)
        return versionsFile
    }

    fun getTypeText(simpleName: String): String = when (simpleName) {
        "String" -> getString("DATATYPE_STRING")
        "Boolean" -> getString("DATATYPE_BOOLEAN")
        "Integer" -> getString("DATATYPE_INTEGER")
        "Double", "Float", "BigDecimal" -> getString("DATATYPE_FRACTION")
        "ArrayList" -> getString("DATATYPE_JSON_ARRAY")
        "HashMap" -> getString("DATATYPE_JSON_OBJECT")
        else -> simpleName
    }

    @Throws(Exception::class)
    fun parseWithPrompting(value: String): Int = try {
        value.toInt()
    } catch (e: NumberFormatException) {
        println(getString("CONSOLE_UNSUPPORTED_VALUE", value))
        throw Exception()
    }

    fun randomUUIDNoSymbol(): String = UUID.randomUUID().toString().replace("-", "")
    fun getPathFromLibraryName(nameSplit: SplitLibraryName): String =
        nameSplit.first.replace(".", "/") + "/" + nameSplit.second + "/" + nameSplit.version

    fun getExtension(string: String?): String? {
        if (isBlank(string)) return null
        val indexOf = string.lastIndexOf('.')
        return if (indexOf < 0 || indexOf == string.length - 1) null else string.substring(indexOf + 1)
    }

    fun close(t: Closeable) {
        try {
            t.close()
        } catch (ignore: IOException) {
        }
    }

    fun downloadFileFailedText(url: String, file: File, e: java.lang.Exception): String =
        if (url.endsWith("/" + file.getName())) getString(
            "MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON_WITH_URL",
            e,
            url,
            file.getParentFile().absolutePath
        )
        else getString(
            "MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON_WITH_URL_WITH_NAME",
            e,
            url,
            file.getParentFile().absolutePath,
            file.getName()
        )

    fun downloadFileFailed(url: String, file: File, e: java.lang.Exception) {
        println(downloadFileFailedText(url, file, e))
    }

    fun xsplit(s: String, regex: String): List<String> {
        val ss = s.splitByRegex(regex)
        return if (ss[0] == s) emptyList() else ss.asList()
    }

    fun <T> iteratorToStream(iterator: Iterator<T>?): Stream<T> {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
    }

    fun getExecutableFilePath(): String = try {
        URLDecoder.decode(Utils::class.java.getProtectionDomain().codeSource.location.path, "UTF-8")
    } catch (e: UnsupportedEncodingException) {
        throw RuntimeException(e)
    }

    fun stringOccupiedSpacesLength(str: String): Int {
        val chars = str.toCharArray()
        var length = 0
        for (aChar in chars) {
            if (isFullWidth(aChar)) length++
            length++
        }
        return length
    }

    fun isFullWidth(aChar: Char): Boolean { //对于其他语言不一定准确
        return aChar.toString().matches("[^\\x00-\\xff]".toRegex())
    }
}

