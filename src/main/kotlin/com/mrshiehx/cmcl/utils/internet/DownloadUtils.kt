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

import com.mrshiehx.cmcl.utils.FileUtils.createFile
import com.mrshiehx.cmcl.utils.Utils.getConfig
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.console.PercentageTextProgress
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

object DownloadUtils {
    @JvmOverloads
    @Throws(IOException::class)
    fun downloadFile(
        urlString: String,
        to: File,
        progressBar: PercentageTextProgress? = null,
        deleteIfExist: Boolean = true
    ) {
        createFile(to, deleteIfExist || to.length() == 0L)
        try {
            val url = URL(NetworkUtils.encodeURL(urlString))
            val httpConnection = url.openConnection() as HttpURLConnection
            val completeFileSize = httpConnection.getContentLength()
            progressBar?.maximum = completeFileSize
            httpConnection.setConnectTimeout(5000)
            httpConnection.setReadTimeout(5000)
            val `in` = BufferedInputStream(httpConnection.inputStream)
            val fos = FileOutputStream(to)
            val bout = BufferedOutputStream(fos, 1024)
            val data = ByteArray(1024)
            var downloadedFileSize: Long = 0
            var x: Int
            while (`in`.read(data, 0, 1024).also { x = it } >= 0) {
                downloadedFileSize += x.toLong()
                progressBar?.value = downloadedFileSize.toInt()
                bout.write(data, 0, x)
            }
            progressBar?.value = completeFileSize
            bout.close()
            fos.close()
            `in`.close()
        } catch (e: MalformedURLException) {
            throw e
        } catch (e: IOException) {
            if (progressBar != null && !progressBar.done) println()
            if (getConfig().optBoolean("proxyEnabled")) System.err.println(getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            to.delete() //万一没开始下载就失败了，如果deleteIfExist（是否原文件存在就删除）为true，那才删，否则没叫删除的，没开始下载就失败了，就不动原文件
            throw e
        }
    }

    @Throws(IOException::class)
    fun downloadBytes(url: String): ByteArray {
        return try {
            val `in` = BufferedInputStream(URL(NetworkUtils.encodeURL(url)).openStream())
            val byteArrayOutputStream = ByteArrayOutputStream()
            val dataBuffer = ByteArray(1024)
            var bytesRead: Int
            while (`in`.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                byteArrayOutputStream.write(dataBuffer, 0, bytesRead)
            }
            byteArrayOutputStream.toByteArray()
        } catch (e: MalformedURLException) {
            throw e
        } catch (e: IOException) {
            if (getConfig().optBoolean("proxyEnabled")) System.err.println(getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw e
        }
    }

    @Throws(IOException::class)
    fun downloadBytes(urlString: String, progressBar: PercentageTextProgress? = null): ByteArray {
        return try {
            val url = URL(NetworkUtils.encodeURL(urlString))
            val httpConnection = url.openConnection() as HttpURLConnection
            val completeFileSize = httpConnection.getContentLength()
            progressBar?.maximum = completeFileSize
            val `in` = BufferedInputStream(httpConnection.inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream(1024)
            val data = ByteArray(1024)
            var downloadedFileSize: Long = 0
            var x: Int
            while (`in`.read(data, 0, 1024).also { x = it } >= 0) {
                downloadedFileSize += x.toLong()
                progressBar?.value = downloadedFileSize.toInt()
                byteArrayOutputStream.write(data, 0, x)
            }
            progressBar?.value = completeFileSize
            `in`.close()
            byteArrayOutputStream.toByteArray()
        } catch (e: MalformedURLException) {
            throw e
        } catch (e: IOException) {
            if (progressBar != null && !progressBar.done) println()
            if (getConfig().optBoolean("proxyEnabled")) System.err.println(getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw e
        }
    }

    @Throws(IOException::class)
    fun multipleAttemptsDownload(url: String, to: File, deleteIfExist: Boolean) {
        multipleAttemptsDownload(url, to, null, 5, deleteIfExist)
    }

    @Throws(IOException::class)
    fun multipleAttemptsDownload(url: String, to: File, times: Int, deleteIfExist: Boolean) {
        multipleAttemptsDownload(url, to, null, times, deleteIfExist)
    }

    @Throws(IOException::class)
    fun multipleAttemptsDownload(
        urlString: String,
        to: File,
        progressBar: PercentageTextProgress?,
        times: Int,
        deleteIfExist: Boolean
    ) {
        var finalThrowable: IOException? = null
        createFile(to, deleteIfExist || to.length() == 0L)
        for (i in 0 until times) {
            try {
                multipleAttemptsDownloadInternal(urlString, to, progressBar, deleteIfExist)
                return
            } catch (e: MalformedURLException) {
                if (progressBar != null && progressBar.printed && !progressBar.done) {
                    progressBar.value = 0
                }
                throw e
            } catch (e: IOException) {
                finalThrowable = e
                if (progressBar != null && progressBar.printed && !progressBar.done) {
                    progressBar.value = 0
                }
            }
        }
        if (finalThrowable != null) {
            if (getConfig().optBoolean("proxyEnabled")) System.err.println(getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            throw finalThrowable
        }
    }

    @Throws(MalformedURLException::class, IOException::class)
    private fun multipleAttemptsDownloadInternal(
        urlString: String,
        to: File,
        progressBar: PercentageTextProgress?,
        deleteIfExist: Boolean
    ) {
        try {
            val url = URL(NetworkUtils.encodeURL(urlString))
            val httpConnection = url.openConnection() as HttpURLConnection
            val completeFileSize = httpConnection.getContentLength()
            progressBar?.maximum = completeFileSize
            httpConnection.setConnectTimeout(5000)
            httpConnection.setReadTimeout(5000)
            val `in` = BufferedInputStream(httpConnection.inputStream)
            val fos = FileOutputStream(to)
            val bout = BufferedOutputStream(fos, 1024)
            val data = ByteArray(1024)
            var downloadedFileSize: Long = 0
            var x: Int
            while (`in`.read(data, 0, 1024).also { x = it } >= 0) {
                downloadedFileSize += x.toLong()
                progressBar?.value = downloadedFileSize.toInt()
                bout.write(data, 0, x)
            }
            progressBar?.value = completeFileSize
            bout.close()
            fos.close()
            `in`.close()
        } catch (e: MalformedURLException) {
            throw e
        } catch (e: IOException) {
            if (deleteIfExist) to.delete() //万一没开始下载就失败了，如果deleteIfExist（是否原文件存在就删除）为true，那才删，否则没叫删除的，没开始下载就失败了，就不动原文件
            throw e
        }
    }
}
