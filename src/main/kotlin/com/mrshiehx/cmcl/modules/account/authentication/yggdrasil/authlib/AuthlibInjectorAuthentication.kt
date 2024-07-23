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
package com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.authlib

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.api.download.DownloadSource
import com.mrshiehx.cmcl.constants.Constants.isDebug
import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.YggdrasilAuthentication
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.YggdrasilAuthenticationApiProvider
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getConfig
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.AccountUtils
import com.mrshiehx.cmcl.utils.console.PercentageTextProgress
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

object AuthlibInjectorAuthentication {
    @Throws(ExceptionWithDescription::class)
    fun authlibInjectorLogin(address: String, username: String?, select: Boolean): JSONObject? {
        var username = username
        if (isEmpty(username)) {
            print(CMCL.getString("INPUT_ACCOUNT"))
            username = try {
                Scanner(System.`in`).nextLine()
            } catch (ignore: NoSuchElementException) {
                return null
            }
        }
        val console = System.console()
        val password = if (console != null) {
            print(CMCL.getString("INPUT_PASSWORD"))
            val input = console.readPassword()
            if (input != null) String(input) else ""
        } else {
            println(CMCL.getString("WARNING_SHOWING_PASSWORD"))
            print(CMCL.getString("INPUT_PASSWORD"))
            try {
                Scanner(System.`in`).nextLine()
            } catch (ignore: NoSuchElementException) {
                return null
            }
        }
        return login(address, username!!, password, select)
    }

    @Throws(ExceptionWithDescription::class)
    fun login(address: String, username: String, password: String, selected: Boolean): JSONObject? {
        var url = NetworkUtils.addHttpsIfMissing(address)
        var serverName = "AuthlibServer"
        /*metadata存储时变成base64，启动时直接拿来用*/
        var metadata: String
        try {
            var conn: HttpURLConnection
            try {
                if (isDebug()) println(url)
                conn = URL(url).openConnection() as HttpURLConnection
                val ali = conn.getHeaderField("x-authlib-injector-api-location")
                if (ali != null) {
                    val absoluteAli = URL(conn.url, ali)
                    if (!NetworkUtils.urlEqualsIgnoreSlash(url, absoluteAli.toString())) {
                        conn.disconnect()
                        url = absoluteAli.toString()
                        conn = absoluteAli.openConnection() as HttpURLConnection
                    }
                }
            } catch (e: MalformedURLException) {
                throw e
            } catch (e: IOException) {
                if (getConfig().optBoolean("proxyEnabled")) System.err.println(getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
                throw e
            }
            if (!url.endsWith("/")) url += "/"
            val firstRequest = JSONObject(NetworkUtils.httpURLConnection2String(conn).also { metadata = it })
            val meta = firstRequest.optJSONObject("meta")
            if (meta != null) {
                serverName = meta.optString("serverName", serverName)
            }
        } catch (e: Exception) {
            throw ExceptionWithDescription(CMCL.getString("FAILED_TO_LOGIN_YGGDRASIL_ACCOUNT_UNAVAILABLE_SERVER"))
        }
        val provider = AuthlibInjectorApiProvider(url)
        val authenticationURL = provider.authenticationURL
        val request = JSONObject()
        request.put("agent", JSONObject().put("name", "Minecraft").put("version", 1))
        request.put("username", username)
        request.put("password", password)
        request.put("clientToken", UUID.randomUUID().toString().replace("-", ""))
        val firstResponse: JSONObject? = try {
            JSONUtils.parseJSONObject(NetworkUtils.post(authenticationURL, request.toString()))
        } catch (e: IOException) {
            throw ExceptionWithDescription(CMCL.getString("EXCEPTION_OF_NETWORK_WITH_URL", authenticationURL, e))
        }
        if (firstResponse == null) {
            println(CMCL.getString("CONSOLE_FAILED_REFRESH_OFFICIAL_NO_RESPONSE"))
            return null
        }
        if (firstResponse.has("error")) {
            if (firstResponse.has("errorMessage")) {
                Utils.printfln(
                    CMCL.getString("FAILED_TO_LOGIN_OTHER_AUTHENTICATION_ACCOUNT"),
                    "\n${
                        firstResponse.optString("error").ifEmpty { "Error" }
                    }: ${firstResponse.optString("errorMessage")}"
                )
            } else {
                Utils.printfln(
                    CMCL.getString("FAILED_TO_LOGIN_OTHER_AUTHENTICATION_ACCOUNT"),
                    firstResponse.optString("error")
                )
            }
            return null
        }
        val clientToken = firstResponse.optString("clientToken")
        val playerName: String
        val uuid: String
        val selectedProfile = firstResponse.optJSONObject("selectedProfile")
        if (selectedProfile != null) {
            playerName = selectedProfile.optString("name")
            uuid = selectedProfile.optString("id")
        } else {
            val availableProfiles = JSONUtils.jsonArrayToJSONObjectList(firstResponse.optJSONArray("availableProfiles"))
            if (availableProfiles.isNotEmpty()) {
                val profile = YggdrasilAuthentication.selectCharacter(availableProfiles)
                    ?: return null
                playerName = profile.optString("name")
                uuid = profile.optString("id")
            } else {
                println(CMCL.getString("FAILED_TO_LOGIN_OAA_NO_SELECTED_CHARACTER"))
                return null
            }
        }
        val accessToken = firstResponse.optString("accessToken")
        val account = JSONObject()
        account.put("selected", selected)
        account.put("loginMethod", 1)
        account.put("uuid", uuid)
        account.put("accessToken", accessToken)
        account.put("playerName", playerName)
        account.put("serverName", serverName)
        account.put("metadataEncoded", Base64.getEncoder().encodeToString(metadata.toByteArray(StandardCharsets.UTF_8)))
        account.put("clientToken", clientToken)
        account.put("url", url)
        account.put("username", username)
        return account
    }

    @Throws(ExceptionWithDescription::class)
    fun refresh(selectedAccount: JSONObject, accounts: JSONArray): Boolean {
        val accessToken = selectedAccount.optString("accessToken")
        val clientToken = selectedAccount.optString("clientToken")
        val url = selectedAccount.optString("url")
        if (isEmpty(accessToken) || Utils.isEmpty(clientToken) || Utils.isEmpty(url)) {
            throw ExceptionWithDescription(CMCL.getString("MESSAGE_AUTHLIB_ACCOUNT_INCOMPLETE"))
        }
        val provider: YggdrasilAuthenticationApiProvider = AuthlibInjectorApiProvider(url)
        val validate = try {
            YggdrasilAuthentication.validate(provider, accessToken, clientToken)
        } catch (e: IOException) {
            if (isDebug()) e.printStackTrace()
            throw ExceptionWithDescription(CMCL.getString("MESSAGE_ACCOUNT_FAILED_TO_VALIDATE", e))
        }
        if (validate == null) {
            //无需刷新
            val uuid = selectedAccount.optString("uuid")
            if (uuid.isNotEmpty()) {
                try {
                    val newPlayerName =
                        JSONObject(NetworkUtils[provider.getProfilePropertiesURL(uuid)]).optString("name")
                    if (!Utils.isEmpty(newPlayerName)) selectedAccount.put("playerName", newPlayerName)
                    return true
                } catch (e: Exception) {
                    if (isDebug()) e.printStackTrace()
                    println(CMCL.getString("EXCEPTION_GET_USER_PROPERTIES", e))
                }
            }
            return false
        }
        if (validate.optString("error") != "ForbiddenOperationException") {
            throw ExceptionWithDescription(
                CMCL.getString(
                    "MESSAGE_ACCOUNT_FAILED_TO_VALIDATE",
                    "\n${
                        validate.optString("error").ifEmpty { "Error" }
                    }: ${validate.optString("errorMessage")}".trimIndent()
                )
            )
        }
        val refreshResponse = try {
            YggdrasilAuthentication.refresh(AuthlibInjectorApiProvider(url), accessToken, clientToken)
        } catch (e: IOException) {
            if (isDebug()) e.printStackTrace()
            throw ExceptionWithDescription(CMCL.getString("MESSAGE_FAILED_REFRESH_TITLE") + ": " + e)
        }
        if (refreshResponse.optString("error") == "ForbiddenOperationException") {
            println(CMCL.getString("MESSAGE_ACCOUNT_INFO_EXPIRED_NEED_RELOGIN"))
            val accountNew = authlibInjectorLogin(url, selectedAccount.optString("username"), true)
                ?: throw ExceptionWithDescription(null)
            for (i in 0 until accounts.length()) {
                if (!AccountUtils.isValidAccount(accounts.opt(i))) continue
                val accountInFor = accounts.opt(i) as JSONObject
                if (accountInFor.optBoolean("selected")) {
                    accounts.put(i, accountNew)
                    break
                }
            }
            return true
        } else if (refreshResponse.optString("error").isNotEmpty()) {
            //報錯
            throw ExceptionWithDescription(
                CMCL.getString(
                    "ERROR_WITH_MESSAGE",
                    refreshResponse.optString("error").ifEmpty { "Error" },
                    refreshResponse.optString("errorMessage")
                )
            )
        }
        //真刷新
        val newAccessToken = refreshResponse.optString("accessToken")
        val newClientToken = refreshResponse.optString("clientToken")
        val availableProfiles = refreshResponse.optJSONArray("availableProfiles")
        val availableProfilesList = JSONUtils.jsonArrayToJSONObjectList(availableProfiles)
        if (availableProfilesList.isNotEmpty()) {
            val profileConsistent = availableProfilesList.stream()
                .filter { profile: JSONObject -> profile.optString("id") == selectedAccount.optString("uuid") }
                .findFirst()
            if (profileConsistent.isPresent) {
                selectedAccount.put("playerName", profileConsistent.get().optString("name"))
            } else {
                println(CMCL.getString("MESSAGE_YGGDRASIL_ACCOUNT_REFRESH_OLD_CHARACTER_DELETED"))
                val selectProfile = YggdrasilAuthentication.selectCharacter(availableProfilesList)
                    ?: return false
                selectedAccount.put("uuid", selectProfile.optString("id"))
                selectedAccount.put("playerName", selectProfile.optString("name"))
            }
        } else {
            //提示此角色已被删除
            println(CMCL.getString("MESSAGE_AUTHLIB_ACCOUNT_REFRESH_NO_CHARACTERS"))
        }
        selectedAccount.put("accessToken", newAccessToken)
        selectedAccount.put("clientToken", newClientToken)
        return true
    }

    @get:Throws(IOException::class)
    val authlibInjectorFile: File
        get() {
            val cmcl = CMCL.CMCLWorkingDirectory
            cmcl.mkdirs()
            val file = File(cmcl, "authlib-injector.jar")
            if (file.exists() && file.isFile() && file.length() > 0) return file
            val latest = JSONObject(NetworkUtils.getWithToken(DownloadSource.getProvider().authlibInjectorFile()))
            print(CMCL.getString("MESSAGE_DOWNLOADING_FILE", "authlib-injector.jar"))
            DownloadUtils.downloadFile(latest.optString("download_url"), file, PercentageTextProgress())
            return file
        }
}