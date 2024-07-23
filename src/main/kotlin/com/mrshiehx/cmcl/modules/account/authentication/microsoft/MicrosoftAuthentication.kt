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
package com.mrshiehx.cmcl.modules.account.authentication.microsoft

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.constants.Constants.CLIENT_ID
import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.server.MicrosoftAuthenticationServer
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.Utils.valueOf
import com.mrshiehx.cmcl.utils.cmcl.AccountUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import com.mrshiehx.cmcl.utils.system.SystemUtils
import fi.iki.elonen.NanoHTTPD
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.ExecutionException

object MicrosoftAuthentication {
    const val ACCESS_TOKEN_URL = "https://login.live.com/oauth20_token.srf"
    fun loginMicrosoftAccount(): JSONObject? {
        var server: MicrosoftAuthenticationServer? = null
        try {
            server = MicrosoftAuthentication.server
        } catch (ignore: IOException) {
        }
        if (server != null) {
            val url =
                "https://login.live.com/oauth20_authorize.srf?client_id=$CLIENT_ID&response_type=code&scope=XboxLive.signin+offline_access&prompt=select_account&redirect_uri=${server.redirectURI}"
            println(getString("CONSOLE_LOGIN_MICROSOFT_WAIT_FOR_RESPONSE", url))
            SystemUtils.openLink(url)
            return try {
                onGotCode(valueOf(server.code), server.redirectURI)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            } catch (e: ExecutionException) {
                throw RuntimeException(e)
            }
        } else {
            println(CMCL.getString("MESSAGE_UNABLE_TO_LOGIN_MICROSOFT"))
        }
        return null
    }

    private fun onGotCode(code: String, rUrl: String): JSONObject? {
        try {
            val secondString =
                "client_id=$CLIENT_ID&grant_type=authorization_code&scope=XboxLive.signin+offline_access&client_id=$CLIENT_ID&redirect_uri=$rUrl&code=$code"
            val first = NetworkUtils.post(ACCESS_TOKEN_URL, secondString, "application/x-www-form-urlencoded", null)
            val result = JSONUtils.parseJSONObject(first)
            if (result == null) {
                println(CMCL.getString("MESSAGE_OFFICIAL_LOGIN_FAILED_TITLE"))
                return null
            }
            if (result.has("error") || result.has("error_description")) {
                val `var` = result.optString("error_description")
                println(CMCL.getString("ERROR_WITH_MESSAGE", result.optString("error"), `var`))
                return null
            }
            val tokenType = result.optString("token_type")
            val refreshToken = result.optString("refresh_token")
            val mcFirst = continueAuthentication(refreshToken)
                ?: return null
            val expiresIn = mcFirst.optInt("expires_in") * 1000L + System.currentTimeMillis()
            val accessToken = mcFirst.optString("access_token")
            val profile = getProfile(tokenType, accessToken)
            if (profile == null) {
                println(CMCL.getString("MESSAGE_OFFICIAL_LOGIN_FAILED_TITLE"))
                return null
            }
            if (profile.has("error") || profile.has("errorMessage")) {
                val `var` = profile.optString("errorMessage")
                println(CMCL.getString("ERROR_WITH_MESSAGE", profile.optString("error"), `var`))
                return null
            }
            val accountID = mcFirst.optString("username")
            val account = JSONObject()
            with(account) {
                put("id", accountID)
                put("loginMethod", 2)
                put("accessToken", accessToken)
                put("refreshToken", refreshToken) //in 2.0
                put("expiresIn", expiresIn) //in 2.0
                put("tokenType", tokenType)
                put("uuid", profile.optString("id"))
                put("playerName", profile.optString("name"))
            }
            return account
        } catch (e: Exception) {
            println(CMCL.getString("MESSAGE_OFFICIAL_LOGIN_FAILED_TITLE") + ": " + e)
        }
        return null
    }

    @Throws(IOException::class)
    fun continueAuthentication(refreshToken: String): JSONObject? {
        val secondSecond =
            "client_id=$CLIENT_ID&refresh_token=$refreshToken&grant_type=refresh_token" /* + "&redirect_uri=" + rUrl*/
        val second = NetworkUtils.post(ACCESS_TOKEN_URL, secondSecond, "application/x-www-form-urlencoded", null)
        val result2 = JSONUtils.parseJSONObject(second)
        if (result2 == null) {
            println(CMCL.getString("MESSAGE_OFFICIAL_LOGIN_FAILED_TITLE"))
            return null
        }
        if (result2.has("error") || result2.has("error_description")) {
            val `var` = result2.optString("error_description")
            println(CMCL.getString("ERROR_WITH_MESSAGE", result2.optString("error"), `var`))
            return null
        }
        val xboxLive = NetworkUtils.post(
            "https://user.auth.xboxlive.com/user/authenticate",
            "{\"Properties\":{\"AuthMethod\":\"RPS\",\"SiteName\":\"user.auth.xboxlive.com\",\"RpsTicket\":\"d=${
                result2.optString(
                    "access_token"
                )
            }\"},\"RelyingParty\":\"http://auth.xboxlive.com\",\"TokenType\":\"JWT\"}",
            "application/json",
            "application/json"
        )
        val xboxLiveFirst = JSONUtils.parseJSONObject(xboxLive)
        if (xboxLiveFirst == null || xboxLiveFirst.has("error")) {
            println(CMCL.getString("MESSAGE_OFFICIAL_LOGIN_FAILED_TITLE"))
            return null
        }
        val token = xboxLiveFirst.optString("Token")
        var uhs = ""
        val displayClaims = xboxLiveFirst.optJSONObject("DisplayClaims")
        if (displayClaims != null) {
            val xui = displayClaims.optJSONArray("xui")
            if (xui != null && xui.length() > 0) {
                val firsta = xui.optJSONObject(0)
                if (firsta != null) uhs = firsta.optString("uhs")
            }
        }
        val xstsResult = JSONUtils.parseJSONObject(
            NetworkUtils.post(
                "https://xsts.auth.xboxlive.com/xsts/authorize",
                "{\"Properties\":{\"SandboxId\":\"RETAIL\",\"UserTokens\":[\"$token\"]},\"RelyingParty\":\"rp://api.minecraftservices.com/\",\"TokenType\":\"JWT\"}",
                "application/json",
                "application/json"
            )
        )
        if (xstsResult == null || xstsResult.has("error")) {
            println(CMCL.getString("MESSAGE_OFFICIAL_LOGIN_FAILED_TITLE"))
            return null
        }
        val xstsToken = xstsResult.optString("Token")
        val mcFirst = JSONUtils.parseJSONObject(
            NetworkUtils.post(
                "https://api.minecraftservices.com/authentication/login_with_xbox",
                "{\"identityToken\":\"XBL3.0 x=$uhs;$xstsToken\"}",
                "application/json",
                "application/json"
            )
        )
        if (mcFirst == null || mcFirst.has("error")) {
            println(CMCL.getString("MESSAGE_OFFICIAL_LOGIN_FAILED_MESSAGE"))
            return null
        }
        return mcFirst
    }

    @Throws(IOException::class)
    fun getProfile(tokenType: String?, accessToken: String): JSONObject? {
        return JSONUtils.parseJSONObject(
            NetworkUtils.getWithToken(
                "https://api.minecraftservices.com/minecraft/profile",
                tokenType ?: "Bearer",
                accessToken
            )
        )
    }

    @get:Throws(IOException::class)
    private val server: MicrosoftAuthenticationServer
        get() {
            var server: MicrosoftAuthenticationServer
            var exception: IOException? = null
            for (port in intArrayOf(29116, 29117, 29118, 29119, 29120, 29121, 29122, 29123, 29124, 29125, 29126)) {
                ////stem.ut.println(port);
                try {
                    server = MicrosoftAuthenticationServer(port)
                    server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true)
                    return server
                } catch (e: IOException) {
                    //e.printStackTrace();
                    exception = e
                }
            }
            throw exception!!
        }

    @Throws(ExceptionWithDescription::class)
    fun refresh(selectedAccount: JSONObject, accounts: JSONArray): Boolean {
        val needRefresh: Boolean
        var profile: JSONObject? = null
        try {
            profile =
                getProfile(selectedAccount.optString("tokenType", "Bearer"), selectedAccount.optString("accessToken"))
        } catch (ignored: IOException) {
        }
        needRefresh = ((selectedAccount.optString("accessToken").isEmpty()
                || selectedAccount.optString("uuid").isEmpty()
                || selectedAccount.optString("playerName").isEmpty()
                || selectedAccount.optString("id").isEmpty()
                || System.currentTimeMillis() > selectedAccount.optLong("expiresIn"))
                || profile == null
                || profile.has("error")
                || profile.has("errorMessage"))
        if (!needRefresh) {
            selectedAccount.put("uuid", profile!!.optString("id"))
            selectedAccount.put("playerName", profile.optString("name"))
            return true
        }
        val refreshToken = selectedAccount.optString("refreshToken")
        val needReLogin = refreshToken.isEmpty()
        if (needReLogin) {
            println(CMCL.getString("MESSAGE_ACCOUNT_INFO_MISSING_NEED_RELOGIN"))
            val accountNew = loginMicrosoftAccount()
                ?: throw ExceptionWithDescription(null)
            if (accountNew.optString("id") != selectedAccount.optString("id")) {
                throw ExceptionWithDescription(CMCL.getString("ACCOUNT_MICROSOFT_REFRESH_NOT_SAME"))
            }
            accountNew.put("selected", true)
            for (i in 0 until accounts.length()) {
                val accountInFor = accounts.optJSONObject(i)
                if (!AccountUtils.isValidAccount(accountInFor)) continue
                if (accountInFor.optBoolean("selected")) {
                    accounts.put(i, accountNew)
                    break
                }
            }
            return true
        }
        return try {
            val auth = continueAuthentication(refreshToken)
                ?: throw ExceptionWithDescription(null)
            val expiresIn = auth.optInt("expires_in") * 1000L + System.currentTimeMillis()
            val accessToken = auth.optString("access_token")
            val newProfile = getProfile(selectedAccount.optString("tokenType", "Bearer"), accessToken)
                ?: throw ExceptionWithDescription(CMCL.getString("MESSAGE_OFFICIAL_LOGIN_FAILED_TITLE"))
            if (newProfile.has("error") || newProfile.has("errorMessage")) {
                val `var` = newProfile.optString("errorMessage")
                throw ExceptionWithDescription(
                    CMCL.getString("ERROR_WITH_MESSAGE", newProfile.optString("error"), `var`)
                )
            }
            val accountID = auth.optString("username")
            with(selectedAccount) {
                put("id", accountID)
                put("accessToken", accessToken)
                put("expiresIn", expiresIn) //in 2.0
                put("uuid", newProfile.optString("id"))
                put("playerName", newProfile.optString("name"))
            }
            true
        } catch (e: Exception) {
            //if(Constants.isDebug())e.printStackTrace();
            throw ExceptionWithDescription(CMCL.getString("MESSAGE_FAILED_REFRESH_TITLE") + if (!isEmpty(valueOf(e))) ": $e" else "")
        }
    }

    /*public static boolean validate(long expiresIn, String tokenType, String accessToken) throws IOException {
        if (System.currentTimeMillis() > expiresIn) {
            return false;
        }
        JSONObject profile = getProfile(tokenType, accessToken);
        return profile != null && !profile.has("error") && !profile.has("errorMessage");
    }*/
}
