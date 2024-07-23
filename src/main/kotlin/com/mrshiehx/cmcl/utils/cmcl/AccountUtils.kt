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
package com.mrshiehx.cmcl.utils.cmcl

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.CMCL.saveConfig
import com.mrshiehx.cmcl.constants.Constants.isDebug
import com.mrshiehx.cmcl.exceptions.NotSelectedException
import com.mrshiehx.cmcl.functions.AccountFunction
import com.mrshiehx.cmcl.modules.account.authentication.microsoft.MicrosoftAuthentication
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.authlib.AuthlibInjectorAuthentication
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.nide8auth.Nide8AuthAuthentication
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.Utils.printfln
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.console.PrintingUtils
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

object AccountUtils {
    @Throws(NotSelectedException::class)
    fun getSelectedAccount(config: JSONObject, prompt: Boolean): JSONObject {
        val accounts = config.optJSONArray("accounts")
        if (accounts == null || accounts.length() == 0) {
            if (prompt) println(getString("NOT_SELECTED_AN_ACCOUNT"))
            throw NotSelectedException()
        }
        for (o in accounts) {
            if (!isValidAccount(o)) continue
            val jsonObject = o as JSONObject
            if (jsonObject.optBoolean("selected")) {
                return jsonObject
            }
        }
        if (prompt) println(getString("NOT_SELECTED_AN_ACCOUNT"))
        throw NotSelectedException()
    }

    fun getSelectedAccountIfNotLoginNow(config: JSONObject): JSONObject? {
        var accounts = config.optJSONArray("accounts")
        var valid = 0
        if (accounts != null) {
            for (o in accounts) {
                if (!isValidAccount(o)) continue
                valid++
                if ((o as JSONObject).optBoolean("selected")) {
                    return o
                }
            }
        }
        return if (accounts != null && valid > 0) {
            val i1 = AtomicInteger()
            PrintingUtils.printTable(
                arrayOf(
                    CMCL.getString("TABLE_ACCOUNTS_LIST_HEADER_ORDER"),
                    CMCL.getString("TABLE_ACCOUNTS_LIST_HEADER_NAME"),
                    CMCL.getString("TABLE_ACCOUNTS_LIST_HEADER_TYPE"),
                    CMCL.getString("TABLE_ACCOUNTS_LIST_HEADER_OTHER_INFORMATION")
                ),
                intArrayOf(6, 20, 24, 50),
                false,
                *AccountFunction.getAllAccounts(accounts).stream().map { account: JSONObject ->
                    AccountFunction.accountInfoToTableItem(
                        account,
                        i1.getAndIncrement(),
                        false
                    )
                }.collect(Collectors.toList()).toTypedArray()
            )
            val order =
                InteractionUtils.inputInt(getString("MESSAGE_SELECT_ACCOUNT", 0, valid - 1), 0, valid - 1)
                    ?: return null
            val account = accounts.optJSONObject(order)
            if (!isValidAccount(account)) {
                printfln(getString("ACCOUNT_INVALID"), order)
                return null
            }
            account.put("selected", true)
            for (i in 0 until accounts.length()) {
                if (i != order) {
                    val accountInFor = accounts.optJSONObject(i)
                    accountInFor?.put("selected", false)
                }
            }
            saveConfig(config)
            account
        } else {
            println("[0]" + getString("ACCOUNT_TYPE_OFFLINE"))
            println("[1]" + getString("ACCOUNT_TYPE_MICROSOFT"))
            println("[2]" + getString("ACCOUNT_TYPE_OAS"))
            println("[3]" + getString("ACCOUNT_TYPE_NIDE8AUTH"))
            val sel = InteractionUtils.inputInt(getString("MESSAGE_SELECT_ACCOUNT_TYPE", 0, 3), 0, 3)
            if (accounts == null) {
                config.put("accounts", JSONArray().also { accounts = it })
            }
            when (sel) {
                0 -> {
                    val account = JSONObject().put(
                        "playerName",
                        InteractionUtils.inputString(getString("ACCOUNT_TIP_LOGIN_OFFLINE_PLAYERNAME"))
                    ).put("selected", true).put("loginMethod", 0)
                    accounts.put(account)
                    saveConfig(config)
                    account
                }

                1 -> {
                    try {
                        val account = MicrosoftAuthentication.loginMicrosoftAccount() ?: return null
                        accounts.put(account.put("selected", true))
                        saveConfig(config)
                        return account
                    } catch (e: Exception) {
                        if (isDebug()) e.printStackTrace()
                        println(e.message)
                        null
                    }
                }

                2 -> {
                    try {
                        val account = AuthlibInjectorAuthentication.authlibInjectorLogin(
                            InteractionUtils.inputString(getString("ACCOUNT_TIP_LOGIN_OAS_ADDRESS")) ?: return null,
                            null,
                            true
                        )
                            ?: return null
                        accounts.put(account)
                        saveConfig(config)
                        return account
                    } catch (e: Exception) {
                        if (isDebug()) e.printStackTrace()
                        println(e.message)
                        null
                    }
                }

                3 -> {
                    try {
                        val account = Nide8AuthAuthentication.nide8authLogin(
                            InteractionUtils.inputString(getString("ACCOUNT_TIP_LOGIN_NIDE8AUTH_SERVER_ID"))
                                ?: return null,
                            null,
                            true
                        )
                            ?: return null
                        accounts.put(account)
                        saveConfig(config)
                        return account
                    } catch (e: Exception) {
                        if (isDebug()) e.printStackTrace()
                        println(e.message)
                        null
                    }
                }

                else -> null
            }
        }
    }

    fun getUUIDByName(playerName: String): String {
        return UUID.nameUUIDFromBytes("OfflinePlayer:$playerName".toByteArray(StandardCharsets.UTF_8)).toString()
            .replace("-", "")
    }

    fun isValidAccount(`object`: Any?): Boolean {
        return `object` is JSONObject && isValidAccount(`object`)
    }

    fun isValidAccount(account: JSONObject?): Boolean {
        return account != null && account.optString("playerName").isNotEmpty() && account.has("loginMethod")
    }
}
