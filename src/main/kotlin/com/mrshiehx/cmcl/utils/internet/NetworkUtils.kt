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
package com.mrshiehx.cmcl.utils.internet

import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.*
import java.util.*

object NetworkUtils {
    const val PARAMETER_SEPARATOR = "&"
    const val NAME_VALUE_SEPARATOR = "="

    fun parseQuery(uri: URI): List<Pair<String, String?>> = parseQuery(uri.rawQuery)
    fun parseQuery(queryParameterString: String): List<Pair<String, String?>> {
        val result: MutableList<Pair<String, String?>> = ArrayList()
        if (queryParameterString.isBlank()) return result
        try {
            Scanner(queryParameterString).use { scanner ->
                scanner.useDelimiter("&")
                while (scanner.hasNext()) {
                    val nameValue = scanner.next().splitByRegex(NAME_VALUE_SEPARATOR)
                    require(nameValue.isNotEmpty() && nameValue.size <= 2) { "bad query string" }
                    val name = URLDecoder.decode(nameValue[0], "UTF-8")
                    val value = if (nameValue.size == 2) URLDecoder.decode(nameValue[1], "UTF-8") else null
                    result.add(Pair(name, value))
                }
            }
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
        return result
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun post(
        url: String?, content: String?, contentType: String? = "application/json", accept: String? = null
    ): String {
        return try {
            val connectUrl = URL(url)
            val connection = connectUrl.openConnection() as HttpURLConnection
            //here is your code above
            connection.setDoInput(true)
            connection.setDoOutput(true)
            connection.setRequestMethod("POST")
            connection.setRequestProperty("Content-Type", contentType)
            accept?.let { connection.setRequestProperty("Accept", it) }
            val wrt = connection.outputStream
            content?.let { wrt.write(it.toByteArray()) }
            val s = httpURLConnection2String(connection)
            wrt.close()
            s
        } catch (e: ProtocolException) {
            throw e
        } catch (e: MalformedURLException) {
            throw e
        } catch (e: IOException) {
            if (Utils.getConfig()
                    .optBoolean("proxyEnabled")
            ) System.err.println(Utils.getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw e
        }
    }

    @Throws(IOException::class)
    fun getWithToken(url: String, tokenType: String, token: String): String {
        return getWithToken(url, tokenType, token, "application/json", "application/json")
    }

    @Throws(IOException::class)
    fun getWithToken(url: String, tokenType: String, token: String, contentType: String?, accept: String?): String {
        return try {
            val connectUrl = URL(url)
            val connection = connectUrl.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.setUseCaches(false)
            connection.setDoOutput(true)
            connection.setRequestMethod("GET")
            connection.setRequestProperty("Content-Type", contentType)
            connection.setRequestProperty("Authorization", "$tokenType $token")
            accept?.let { connection.setRequestProperty("Accept", it) }
            httpURLConnection2String(connection)
        } catch (e: ProtocolException) {
            throw e
        } catch (e: MalformedURLException) {
            throw e
        } catch (e: IOException) {
            if (Utils.getConfig()
                    .optBoolean("proxyEnabled")
            ) System.err.println(Utils.getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw e
        }
    }

    @JvmOverloads
    @Throws(IOException::class)
    operator fun get(
        url: String, contentType: String? = "application/json", accept: String? = "application/json"
    ): String {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.setUseCaches(false)
            connection.setDoOutput(true)
            connection.setRequestMethod("GET")
            connection.setRequestProperty("Content-Type", contentType)
            accept?.let { connection.setRequestProperty("Accept", it) }
            httpURLConnection2String(connection)
        } catch (e: ProtocolException) {
            throw e
        } catch (e: MalformedURLException) {
            throw e
        } catch (e: IOException) {
            if (Utils.getConfig()
                    .optBoolean("proxyEnabled")
            ) System.err.println(Utils.getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw e
        }
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun curseForgeGet(
        url: String, contentType: String? = "application/json", accept: String? = "application/json"
    ): String {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.setUseCaches(false)
            connection.setDoOutput(true)
            connection.setRequestMethod("GET")
            connection.setRequestProperty("Content-Type", contentType)
            connection.setRequestProperty("x-api-key", Constants.getCurseForgeApiKey())
            accept?.let { connection.setRequestProperty("Accept", it) }
            httpURLConnection2String(connection)
        } catch (e: ProtocolException) {
            throw e
        } catch (e: MalformedURLException) {
            throw e
        } catch (e: IOException) {
            if (Utils.getConfig()
                    .optBoolean("proxyEnabled")
            ) System.err.println(Utils.getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw e
        }
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun delete(
        url: String,
        tokenType: String,
        token: String,
        contentType: String? = "application/json",
        accept: String? = "application/json"
    ): String {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            //here is your code above
            connection.setDoInput(true)
            connection.setUseCaches(false)
            connection.setDoOutput(true)
            connection.setRequestMethod("DELETE")
            connection.setRequestProperty("Content-Type", contentType)
            connection.setRequestProperty("Authorization", "$tokenType $token")
            //System.out.println(tokenType);
            //System.out.println(token);
            accept?.let { connection.setRequestProperty("Accept", it) }
            //connection.getOutputStream();//.write("\"publicCreateProfileDTO\":\"45\"".getBytes(UTF_8));
            httpURLConnection2String(connection)
        } catch (e: ProtocolException) {
            throw e
        } catch (e: MalformedURLException) {
            throw e
        } catch (e: IOException) {
            if (Utils.getConfig()
                    .optBoolean("proxyEnabled")
            ) System.err.println(Utils.getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw e
        }
    }

    @Throws(IOException::class)
    fun httpURLConnection2String(con: HttpURLConnection): String {
        try {
            try {
                con.inputStream.use { stdout -> return Utils.inputStream2String(stdout) }
            } catch (e: IOException) {
                con.errorStream.use { stderr ->
                    if (stderr == null) throw e
                    return Utils.inputStream2String(stderr)
                }
            }
        } catch (e: IOException) {
            if (Utils.getConfig()
                    .optBoolean("proxyEnabled")
            ) System.err.println(Utils.getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw e
        }
    }

    @Throws(IOException::class)
    fun httpURLConnection2Bytes(con: HttpURLConnection): ByteArray {
        try {
            try {
                con.inputStream.use { stdout -> return FileUtils.inputStream2ByteArray(stdout) }
            } catch (e: IOException) {
                con.errorStream.use { stderr ->
                    if (stderr == null) throw e
                    return FileUtils.inputStream2ByteArray(stderr)
                }
            }
        } catch (e: IOException) {
            if (Utils.getConfig()
                    .optBoolean("proxyEnabled")
            ) System.err.println(Utils.getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw e
        }
    }

    fun setProxy(host: String, port: String, userName: String?, password: String?) {
        if (host.isBlank() || port.isBlank()) return
        System.setProperty("java.net.useSystemProxies", "true")
        System.setProperty("https.proxyHost", host)
        System.setProperty("http.proxyHost", host)
        System.setProperty("https.proxyPort", port)
        System.setProperty("http.proxyPort", port)
        if (!Utils.isBlank(userName)) {
            System.setProperty("http.proxyUserName", userName)
            System.setProperty("https.proxyUserName", userName)
            if (!Utils.isBlank(password)) {
                System.setProperty("http.proxyPassword", password)
                System.setProperty("https.proxyPassword", password)
            }
        }
    }

    @Throws(IOException::class)
    fun getWithToken(url: String): String {
        return try {
            val `in` = BufferedInputStream(URL(url).openStream())
            val byteOutputStream = ByteArrayOutputStream()
            val dataBuffer = ByteArray(1024)
            var bytesRead: Int
            while (`in`.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                byteOutputStream.write(dataBuffer, 0, bytesRead)
            }
            byteOutputStream.close()
            `in`.close()
            byteOutputStream.toString()
        } catch (e: MalformedURLException) {
            throw e
        } catch (e: IOException) {
            if (Utils.getConfig()
                    .optBoolean("proxyEnabled")
            ) System.err.println(Utils.getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw e
        }
    }

    fun addSlashIfMissing(url: String): String = if (!url.endsWith("/")) "$url/" else url
    fun encodeURL(url: String): String {
        val sb = StringBuilder()
        var left = true
        for (c in url.toCharArray()) {
            if (c == ' ') {
                sb.append(if (left) "%20" else '+')
                continue
            }
            if (c == '?') left = false
            try {
                sb.append(if (c.code >= 0x80) URLDecoder.decode(c.toString(), "UTF-8") else c)
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException(e)
            }
        }
        return sb.toString()
    }

    fun addHttpsIfMissing(url: String): String =
        if (!url.startsWith("https://", ignoreCase = true) && !url.startsWith("http://", ignoreCase = true))
            "https://$url" else url

    fun urlEqualsIgnoreSlash(aOrigin: String, bOrigin: String): Boolean {
        var a = aOrigin
        var b = bOrigin
        if (!a.endsWith("/")) a += "/"
        if (!b.endsWith("/")) b += "/"
        return a == b
    }
}
