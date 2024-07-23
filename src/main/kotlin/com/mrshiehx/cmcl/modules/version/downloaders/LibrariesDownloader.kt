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
package com.mrshiehx.cmcl.modules.version.downloaders

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.bean.Library
import com.mrshiehx.cmcl.modules.MinecraftLauncher
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.version.VersionLibraryUtils
import com.mrshiehx.cmcl.utils.console.PercentageTextProgress
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object LibrariesDownloader {
    fun downloadLibraries(list: List<Library>) {
        try {
            println(CMCL.getString("MESSAGE_INSTALL_DOWNLOADING_LIBRARIES"))
            if (list.isNotEmpty()) {
                for (library in list) {
                    val jsonObject: JSONObject = library.libraryJSONObject
                    downloadSingleLibrary(jsonObject)
                }
                println(CMCL.getString("MESSAGE_INSTALL_DOWNLOADED_LIBRARIES"))
            } else {
                println(CMCL.getString("MESSAGE_INSTALL_LIBRARIES_LIST_EMPTY"))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            println(CMCL.getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARIES", ex))
        }
    }

    fun downloadSingleLibrary(library: JSONObject) {
        var meet = true
        val rules: JSONArray? = library.optJSONArray("rules")
        if (rules != null) {
            meet = MinecraftLauncher.isMeetConditions(rules, emptyMap())
        }
        if (!meet) return
        val pair = VersionLibraryUtils.getLibraryDownloadURLAndStoragePath(library)
        if (pair == null || isEmpty(pair.first)) {
            println(CMCL.getString("MESSAGE_NOT_FOUND_LIBRARY_DOWNLOAD_URL", library.optString("name")))
            return
        }
        val pairValue: String = pair.second
        val file = File(CMCL.librariesDir, pairValue)
        try {
            FileUtils.createFile(file, false)
            if (file.length() == 0L) {
                print(CMCL.getString("MESSAGE_DOWNLOADING_FILE", pairValue.substring(pairValue.lastIndexOf("/") + 1)))
                DownloadUtils.downloadFile(pair.first!!, file, PercentageTextProgress())
            }
        } catch (e: Exception) {
            Utils.downloadFileFailed(pair.first!!, file, e)
            //System.out.println(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARY", pair.first, e));
        }

    }
}
