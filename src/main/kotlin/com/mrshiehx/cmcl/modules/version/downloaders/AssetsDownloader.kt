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
package com.mrshiehx.cmcl.modules.version.downloaders

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.api.download.DownloadSource
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import com.mrshiehx.cmcl.utils.internet.ThreadsDownloader
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

object AssetsDownloader {
    @Throws(ExceptionWithDescription::class)
    fun start(headVersionFile: JSONObject, threadCount: Int, onDownloaded: (() -> Unit)?) {
        val assetsDirPath: String = Utils.getConfig().optString("assetsPath")
        val assetsDir = if (!Utils.isEmpty(assetsDirPath)) File(assetsDirPath) else File(CMCL.gameDir, "assets")
        val indexesDir = File(assetsDir, "indexes")
        val objectsDir = File(assetsDir, "objects")
        assetsDir.mkdirs()
        indexesDir.mkdirs()
        objectsDir.mkdirs()
        val assetsIndex: String = headVersionFile.optString("assets")
        if (Utils.isEmpty(assetsIndex)) {
            throw ExceptionWithDescription(CMCL.getString("MESSAGE_INSTALL_DOWNLOAD_ASSETS_NO_INDEX"))
        }
        val assetsIndexFile = File(indexesDir, "$assetsIndex.json")
        val assetIndexObject: JSONObject? = headVersionFile.optJSONObject("assetIndex")
        val assetIndexUrl: String? = assetIndexObject?.optString("url")
            ?.replace("https://launchermeta.mojang.com/", DownloadSource.getProvider().versionAssetsIndex())
            ?.replace("https://piston-meta.mojang.com/", DownloadSource.getProvider().versionAssetsIndex())
        if (Utils.isEmpty(assetIndexUrl)) {
            throw ExceptionWithDescription(
                CMCL.getString(
                    "MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS",
                    CMCL.getString("MESSAGE_EXCEPTION_DETAIL_NOT_FOUND_URL")
                )
            )
        }
        try {
            DownloadUtils.downloadFile(assetIndexUrl, assetsIndexFile)
            val assetsJo = JSONObject(FileUtils.readFileContent(assetsIndexFile))
            val objectsJo: JSONObject = assetsJo.optJSONObject("objects")
            val map: Map<String, Any> = objectsJo.toMap()
            val nameList: List<String> = ArrayList(map.keys)
            val names = JSONArray(nameList)
            val objectsJa: JSONArray = objectsJo.toJSONArray(names)
            val list: MutableList<Pair<String, File>> = LinkedList()
            for (i in 0 until objectsJa.length()) {
                val `object`: JSONObject = objectsJa.optJSONObject(i) ?: continue
                val hash: String = `object`.optString("hash")
                try {
                    if (Utils.isEmpty(hash)) continue
                    val file: File
                    if (assetsIndex != "legacy") {
                        val dir = File(objectsDir, hash.substring(0, 2))
                        dir.mkdirs()
                        file = File(dir, hash)
                    } else {
                        file = File(assetsDir, "virtual/legacy/" + nameList[i])
                        file.getParentFile().mkdirs()
                    }
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    if (file.length() == 0L) list.add(
                        Pair(
                            DownloadSource.getProvider().assets() + hash.substring(
                                0,
                                2
                            ) + "/" + hash, file
                        )
                    )
                } catch (e1: Exception) {
                    throw ExceptionWithDescription(CMCL.getString("MESSAGE_FAILED_DOWNLOAD_FILE", hash))
                }
            }
            if (Constants.isDebug()) println("threadCount: $threadCount")
            val threadsDownloader = ThreadsDownloader(
                list,
                onDownloaded,
                if (threadCount > 0) threadCount else Constants.DEFAULT_DOWNLOAD_THREAD_COUNT,
                false
            )
            threadsDownloader.start()
        } catch (e1: Exception) {
            throw ExceptionWithDescription(CMCL.getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS", e1))
        }
    }
}
