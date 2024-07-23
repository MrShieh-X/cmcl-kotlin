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
package com.mrshiehx.cmcl.modules.account.skin

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.authlib.AuthlibInjectorApiProvider
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.nide8auth.Nide8AuthApiProvider
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getConfig
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object SkinDownloader {
    fun start(file: File, account: JSONObject) {
        val uuid = account.optString("uuid")
        if (Utils.isEmpty(uuid)) {
            println(CMCL.getString("CONSOLE_FAILED_OPERATE") + CMCL.getString("MESSAGE_UUID_ACCESSTOKEN_EMPTY"))
            return
        }
        try {
            var urlString = ""
            when (account.optInt("loginMethod")) {
                1 -> urlString = AuthlibInjectorApiProvider(account.optString("url")).getProfilePropertiesURL(uuid)
                2 -> urlString = "https://sessionserver.mojang.com/session/minecraft/profile/$uuid"
                3 -> urlString = Nide8AuthApiProvider(account.optString("serverId")).getProfilePropertiesURL(uuid)
            }
            val url = URL(urlString)
            val connection = try {
                url.openConnection()
            } catch (e: IOException) {
                if (getConfig().optBoolean("proxyEnabled")) System.err.println(getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
                throw e
            }
            val result = JSONObject(
                NetworkUtils.httpURLConnection2String(connection as HttpURLConnection)
            )
            val properties = result.getJSONArray("properties")
            for (i in 0 until properties.length()) {
                val property = properties.optJSONObject(i)
                if (property == null || property.optString("name") != "textures") continue
                val value = JSONObject(String(Base64.getDecoder().decode(property.optString("value"))))
                val textures = value.optJSONObject("textures")
                if (textures != null && textures.has("SKIN")) {
                    DownloadUtils.downloadFile(textures.optJSONObject("SKIN").optString("url"), file)
                } else {
                    println(CMCL.getString("MESSAGE_DOWNLOAD_SKIN_FILE_NOT_SET_TEXT"))
                }
                return
            }
            println(CMCL.getString("MESSAGE_DOWNLOAD_SKIN_FILE_NOT_SET_TEXT"))
        } catch (e: Exception) {
            e.printStackTrace()
            println(CMCL.getString("CONSOLE_FAILED_OPERATE") + e)
        }
    }
}
