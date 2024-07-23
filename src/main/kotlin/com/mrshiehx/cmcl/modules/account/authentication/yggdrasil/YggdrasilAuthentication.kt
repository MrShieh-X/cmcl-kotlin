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
package com.mrshiehx.cmcl.modules.account.authentication.yggdrasil

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getConfig
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.Utils.isBlank
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

object YggdrasilAuthentication {
    fun uploadSkin(
        apiProvider: YggdrasilAuthenticationApiProvider,
        uuid: String,
        accessToken: String,
        fileName: String,
        suffix: String,
        skin: ByteArray,
        slim: Boolean
    ) {
        if (isBlank(uuid) || Utils.isBlank(accessToken)) {
            println(CMCL.getString("CONSOLE_ACCOUNT_UN_OPERABLE_NEED_UUID_AND_URL_AND_TOKEN"))
            return
        }
        try {
            val boundary = "~~~~~~~~~~~~~~~~~~~~~~~~~"

            val skinUploadURL = apiProvider.getSkinUploadURL(uuid)
            val connection = URL(skinUploadURL).openConnection() as HttpURLConnection
            with(connection) {
                setUseCaches(false)
                setConnectTimeout(15000)
                setReadTimeout(15000)
                setRequestMethod("PUT")
                setRequestProperty("Accept-Language", Locale.getDefault().toString())
                setRequestProperty("Authorization", "Bearer $accessToken")
                setDoOutput(true)
                setRequestProperty("Accept", "*/*")
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                setDoOutput(true)
            }
            val byteArrayOutputStream = ByteArrayOutputStream()
            val sl = "\r\n".toByteArray(StandardCharsets.UTF_8)


            with(byteArrayOutputStream) {
                write("--$boundary".toByteArray(StandardCharsets.UTF_8))
                write(sl)
                write(
                    "Content-Disposition: form-data; name=\"model\"".toByteArray(
                        StandardCharsets.UTF_8
                    )
                )
                write(sl)
                write(sl)
                var model = ""
                if (slim) {
                    model = "slim"
                }
                write(model.toByteArray(StandardCharsets.UTF_8))
                write(sl)
                write("--$boundary".toByteArray(StandardCharsets.UTF_8))
                write(sl)
                write(
                    "Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"".toByteArray(
                        StandardCharsets.UTF_8
                    )
                )
                write(sl)
                write("Content-Type: image$suffix".toByteArray(StandardCharsets.UTF_8))
                write(sl)
                write(sl)
                write(skin)
                write(sl)
                write(sl)
            }
            //byteArrayOutputStream.write(("--" + boundary + "--").getBytes(UTF_8));
            connection.setRequestProperty("Content-Length", byteArrayOutputStream.size().toString())
            val outputStream = connection.outputStream
            outputStream.write(byteArrayOutputStream.toByteArray())
            byteArrayOutputStream.close()

            val resultString = NetworkUtils.httpURLConnection2String(connection)
            if (!isEmpty(resultString) && resultString.startsWith("{")) {
                val result = JSONObject(resultString)
                Utils.printfln(
                    CMCL.getString("ERROR_WITH_MESSAGE"),
                    result.optString("error"),
                    result.optString("errorMessage")
                )
            } else {
                println(CMCL.getString("SUCCESSFULLY_SET_SKIN"))
            }
        } catch (e: ProtocolException) {
            e.printStackTrace()
            Utils.printfln(CMCL.getString("UNABLE_SET_SKIN"))
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            Utils.printfln(CMCL.getString("UNABLE_SET_SKIN"))
        } catch (e: IOException) {
            e.printStackTrace()
            if (getConfig().optBoolean("proxyEnabled")) System.err.println(getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
            Utils.printfln(CMCL.getString("UNABLE_SET_SKIN"))
        }
    }

    fun selectCharacter(availableProfiles: List<JSONObject>): JSONObject? {
        for (i in availableProfiles.indices) {
            val jsonObject = availableProfiles[i]
            println((i + 1).toString() + "." + jsonObject.optString("name"))
        }
        val number = InteractionUtils.inputInt(
            getString("MESSAGE_YGGDRASIL_LOGIN_SELECT_PROFILE", 1, availableProfiles.size),
            1,
            availableProfiles.size
        )
        return if (number != null) {
            availableProfiles[number - 1]
        } else {
            null
        }
    }

    @Throws(IOException::class)
    fun validate(
        provider: YggdrasilAuthenticationApiProvider,
        accessToken: String,
        clientToken: String
    ): JSONObject? {
        val validationURL = provider.validationURL
        val request = JSONObject().put("accessToken", accessToken).put("clientToken", clientToken)
        val response = NetworkUtils.post(validationURL, request.toString())
        return if (isEmpty(response)) null else JSONUtils.parseJSONObject(response)
    }

    @Throws(IOException::class)
    fun refresh(provider: YggdrasilAuthenticationApiProvider, accessToken: String, clientToken: String): JSONObject {
        val refreshmentURL = provider.refreshmentURL
        val request = JSONObject().put("accessToken", accessToken).put("clientToken", clientToken)
        return JSONObject(NetworkUtils.post(refreshmentURL, request.toString()))
    }
}
