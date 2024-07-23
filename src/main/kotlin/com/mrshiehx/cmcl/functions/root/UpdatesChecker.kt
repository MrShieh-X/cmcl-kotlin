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
package com.mrshiehx.cmcl.functions.root

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object UpdatesChecker {
    fun execute() {
        var newVersion: JSONObject? = null
        for (url in Constants.CHECK_FOR_UPDATES_INFORMATION_URLS) {
            try {
                newVersion = JSONObject(String(DownloadUtils.downloadBytes(url)))
                break
            } catch (ignored: Exception) {
            }
        }
        if (newVersion == null) {
            println(getString("MESSAGE_FAILED_TO_CHECK_FOR_UPDATES"))
            return
        }
        val latestVersionCode = newVersion.optInt("latestVersionCode")
        if (latestVersionCode > Constants.CMCL_VERSION_CODE) {
            val latestVersionName = newVersion.optString("latestVersionName")
            val updateDate = newVersion.optString("updateDate")
            val updateContentEn = newVersion.optString("updateContentEn")
            val updateContentZh = newVersion.optString("updateContentZh")
            val urls = StringBuilder()
            val latestVersionDownloadUrls = newVersion.optJSONArray("latestVersionDownloadUrls") ?: JSONArray()
            for (o in latestVersionDownloadUrls) {
                if (o is String) {
                    urls.append("  ").append(o).append('\n')
                }
            }
            println(
                getString(
                    "MESSAGE_NEW_VERSION",
                    latestVersionName,
                    updateDate,
                    urls.toString(),
                    if (CMCL.language.locale === Locale.CHINA) updateContentZh else updateContentEn
                )
            )
        } else {
            println(getString("MESSAGE_CURRENT_IS_LATEST_VERSION"))
        }
    }
}
