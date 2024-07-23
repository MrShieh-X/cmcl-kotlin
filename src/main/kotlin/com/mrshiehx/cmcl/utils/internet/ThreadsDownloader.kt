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

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.utils.Utils
import java.io.File
import java.io.IOException
import java.util.*

/**
 * 多线程下载文件（无控制台百分比进度）
 *
 * @author MrShiehX
 */
class ThreadsDownloader(
    files: List<Pair<String, File>>,
    private val onDownloaded: (() -> Unit)?,
    totalThreadsCount: Int,
    private val deleteTargetFileIfExist: Boolean
) {
    private var doneThreadsCount = 0

    private val totalFilesCount: Int = files.size
    private val totalThreadsCount: Int
    private var doneFilesCount = 0

    private val preMessage: String

    private var started = false

    val maps: Map<Int, List<Pair<String, File>>>

    constructor(files: List<Pair<String, File>>, deleteTargetFileIfExist: Boolean) : this(
        files,
        null,
        deleteTargetFileIfExist
    )

    constructor(files: List<Pair<String, File>>, onDownloaded: (() -> Unit)?, deleteTargetFileIfExist: Boolean) : this(
        files,
        onDownloaded,
        Constants.DEFAULT_DOWNLOAD_THREAD_COUNT,
        deleteTargetFileIfExist
    )

    init {
        if (totalFilesCount > 0) {


            this.totalThreadsCount = Math.min(totalFilesCount, totalThreadsCount)
            val mutableMaps: MutableMap<Int, MutableList<Pair<String, File>>> = HashMap(this.totalThreadsCount)
            for (i in 0 until this.totalThreadsCount) {
                mutableMaps[i] = LinkedList()
            }
            preMessage = "[%d/$totalFilesCount]"
            if (totalFilesCount <= totalThreadsCount) {//注意：不是this.totalThreadsCount
                for ((i, entry) in files.withIndex()) {
                    mutableMaps[i]?.add(entry)//其实根本不需要?.
                }
            } else {
                val quotient = totalFilesCount / this.totalThreadsCount
                val remainder = totalFilesCount % this.totalThreadsCount
                for (i in 0 until this.totalThreadsCount) {
                    val map = mutableMaps[i]
                    val start = quotient * i
                    for (j in start until start + quotient) {
                        val one = files[j]
                        map?.add(one)//其实根本不需要?.
                    }
                }
                if (remainder > 0) {
                    val start = quotient * this.totalThreadsCount
                    for (i in 0 until this.totalThreadsCount) {
                        val map = mutableMaps[i]
                        val j = start + i
                        if (j < totalFilesCount) {
                            val one = files[j]
                            map?.add(one)//其实根本不需要?.
                        }
                    }
                }
            }
            maps = mutableMaps.toMap()
        } else {
            this.totalThreadsCount = 0
            maps = emptyMap()
            preMessage = "[%d/0]"
        }
    }

    fun start() {
        if (totalFilesCount <= 0) {
            started = true
            onDownloaded?.let { it() }//or onDownloaded?.invoke()
            return
        }
        if (started) return
        for (i in 0 until this.totalThreadsCount) {
            Thread {
                val map: List<Pair<String, File>> = maps[i]!!//其实根本不需要!!
                for ((key, value) in map) {
                    val url: String = key
                    val file: File = value
                    if (Utils.isEmpty(url)) {
                        doneAddOneFile()
                        println(
                            String.format(preMessage, doneFilesCount)
                                    + CMCL.getString("EXCEPTION_NOT_FOUND_DOWNLOAD_LINK_WITH_FILENAME", file.getName())
                        )
                        continue
                    }
                    try {
                        DownloadUtils.multipleAttemptsDownload(url, file, deleteTargetFileIfExist)
                        doneAddOneFile()
                        //System.out.println(getString("MESSAGE_DOWNLOADING_FILE", url.substring(url.lastIndexOf('/') + 1)));
                        //不知为何多线程读取MAP会出现读取不了的问题（直接输出MESSAGE_DOWNLOADING_FILE（正在下载%s）），所以干脆直接输出文件名
                        println(String.format(preMessage, doneFilesCount) + url.substring(url.lastIndexOf('/') + 1))
                    } catch (e: IOException) {
                        doneAddOneFile()
                        println(String.format(preMessage, doneFilesCount) + Utils.downloadFileFailedText(url, file, e))
                    }
                }
                //System.out.println(done+"/"+threadsCount);
                doneAddOneThread()
            }.start()
        }
        started = true
        while (true) {
            //System.out.println(done+"/"+threadsCount);
            try {
                Thread.sleep(1)
            } catch (ignore: Exception) {
            }
            if (doneThreadsCount >= this.totalThreadsCount) {
                onDownloaded?.let { it() }//or onDownloaded?.invoke()
                break
            }
        }
    }

    private fun doneAddOneThread() {
        synchronized(this) { doneThreadsCount++ }
    }

    private fun doneAddOneFile() {
        synchronized(this) { doneFilesCount++ }
    }
}
