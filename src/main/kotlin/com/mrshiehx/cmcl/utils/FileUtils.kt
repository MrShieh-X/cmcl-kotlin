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

import com.mrshiehx.cmcl.utils.console.PercentageTextProgress
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object FileUtils {
    @Throws(IOException::class, Exception::class)
    fun hexWrite(bytes: String, file: File) {
        hexWrite(hexString2Bytes(bytes), file)
    }


    @Throws(IOException::class)
    fun hexWrite(bytes: ByteArray, file: File) {
        createFile(file)
        val fop = FileOutputStream(file)
        fop.write(bytes)
        fop.flush()
        fop.close()
    }

    @Throws(Exception::class)
    fun hexString2Bytes(hex: String): ByteArray {
        val length = hex.length
        if (length == 0 || length % 2 != 0) throw Exception()
        val bytes = ByteArray(length / 2)
        var j = 0
        var i = 0
        while (i < length) {
            bytes[j++] = hex.substring(i, i + 2).toInt(16).toByte()
            i += 2
        }
        return bytes

    }

    fun bytesToString(bytes: ByteArray): String {
        val builder = StringBuilder()
        for (aByte in bytes) {
            builder.append(String.format("%02X", aByte))
        }
        return builder.toString().uppercase(Locale.getDefault())
    }

    private fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }


    @Throws(IOException::class)
    fun bytes2File(file: File, bytes: ByteArray, append: Boolean) {
        createFile(file, false)
        val fileOutputStream = FileOutputStream(file, append)
        fileOutputStream.write(bytes, 0, bytes.size)
        fileOutputStream.flush()
        fileOutputStream.close()
    }


    @Throws(IOException::class)
    fun toByteArray(input: File): ByteArray {
        return toByteArray(FileInputStream(input))
    }


    @Throws(IOException::class)
    fun toByteArray(input: InputStream): ByteArray {
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(4096)
        var n: Int
        while (-1 != input.read(buffer).also { n = it }) {
            output.write(buffer, 0, n)
        }
        return output.toByteArray()
    }


    @Throws(IOException::class)
    fun inputStream2File(ins: InputStream, file: File) {
        createFile(file, true)
        val bos = BufferedOutputStream(FileOutputStream(file))
        val bis = BufferedInputStream(ins)
        var bytesRead: Int
        val buffer = ByteArray(8192)
        while (bis.read(buffer, 0, 8192).also { bytesRead = it } != -1) {
            bos.write(buffer, 0, bytesRead)
        }
        bos.close()
        bis.close()
    }


    @Throws(IOException::class)
    fun inputStream2ByteArray(resourceAsStream: InputStream): ByteArray {

        /*byte[] bytes = new byte[0];
    bytes = new byte[resourceAsStream.available()];
    resourceAsStream.read(bytes);
    return new String(bytes);*/
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(4096)
        var n: Int
        while (-1 != resourceAsStream.read(buffer).also { n = it }) {
            output.write(buffer, 0, n)
        }
        return output.toByteArray()

    }

    /*不用这个因为容易报错（如不存在等情况）
    @Throws(Exception::class)
    fun deleteDirectory(directory: File) {
        // 使用 Files.walk 来遍历文件树
        Files.walk(directory.toPath())
            .sorted(Comparator.reverseOrder())  // 倒序，确保先删除子项再删除父项
            .forEach { Files.delete(it) }      // 删除每个文件或文件夹
    }*/


    fun deleteDirectory(directory: File) {
        if (!directory.exists() || !directory.isDirectory()) return
        val files = directory.listFiles()

        files?.forEach { file -> if (file.isFile()) file.delete() else deleteDirectory(file) }

        /*if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isFile()) {
                    file.delete()
                } else {
                    deleteDirectory(file)
                }
            }
        }*/
        directory.delete()
    }


    @Throws(IOException::class)
    fun copyDirectory(from: File, destParent: String, destName: String) {
        if (destParent.isEmpty() || !from.exists()) return
        if (from.isFile()) {
            copyFile(from, File(destParent, destName))
            return
        }
        val to = File(File(destParent), destName)
        if (!to.exists()) to.mkdirs()

        from.listFiles()?.forEach { file ->
            if (file.isFile()) copyFile(file, File(to, file.getName()))
            else copyDirectory(file, to.absolutePath, file.getName())
        }
    }


    @JvmOverloads
    @Throws(IOException::class)
    fun createFile(file: File, delete: Boolean = true): File {
        val parent = file.getParentFile()
        parent?.let { if (!it.exists()) it.mkdirs() }
        if (delete && file.exists()) file.delete()
        if (!file.exists()) file.createNewFile()
        return file
    }


    @Throws(IOException::class)
    fun copyFile(source: File, to: File) {
        if (source.isDirectory()) copyDirectory(source, to.getParent(), to.getName())
        createFile(to, true)
        val input: InputStream = FileInputStream(source)
        val output: OutputStream = FileOutputStream(to)
        val buf = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buf).also { bytesRead = it } != -1) {
            output.write(buf, 0, bytesRead)
        }
        input.close()
        output.close()
    }


    @Throws(IOException::class)
    fun readFileContent(file: File): String {
        val reader: BufferedReader
        val sbf = StringBuilder()
        val fr = FileReader(file)
        reader = BufferedReader(fr)
        var tempStr: String?
        while (reader.readLine().also { tempStr = it } != null) {
            sbf.append(tempStr).append('\n')
        }
        fr.close()
        reader.close()
        return if (sbf.isEmpty()) "" else sbf.substring(0, sbf.length - 1)
    }


    @Throws(IOException::class)
    fun getBytes(file: File): ByteArray {
        return toByteArray(file)
    }


    @Throws(IOException::class)
    fun writeFile(file: File, content: String, append: Boolean) {
        createFile(file, false)
        val writer = FileWriter(file, append)
        writer.write(content)
        writer.close()
    }


    @Throws(IOException::class)
    fun unZip(
        zipFileSource: File,
        to: File,
        progressBar: PercentageTextProgress?,
        acceptFileName: ((String) -> Boolean)?
    ) {
        val bufferSize = 2048
        if (!zipFileSource.exists()) return
        val zipFile = ZipFile(zipFileSource)
        val size = zipFile.size()
        progressBar?.maximum = size
        val entries: Enumeration<*> = zipFile.entries()
        var progress = 0
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry

            if (acceptFileName != null && !acceptFileName(entry.name)) continue

            val targetFile = File(to, entry.name)
            if (entry.isDirectory) {
                targetFile.mkdirs()
            } else {
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs()
                }
                if (targetFile.exists()) targetFile.delete()
                targetFile.createNewFile()
                val `is` = zipFile.getInputStream(entry)
                val fos = FileOutputStream(targetFile)
                var len: Int
                val buf = ByteArray(bufferSize)
                while (`is`.read(buf).also { len = it } != -1) {
                    fos.write(buf, 0, len)
                }
                fos.close()
                `is`.close()
            }
            progress++
            progressBar?.value = progress
        }
        zipFile.close()
        progressBar?.value = size

    }
}
