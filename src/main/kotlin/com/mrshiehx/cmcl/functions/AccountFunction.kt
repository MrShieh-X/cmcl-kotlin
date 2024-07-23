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
package com.mrshiehx.cmcl.functions

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.bean.arguments.ArgumentRequirement
import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.bean.arguments.SingleArgument
import com.mrshiehx.cmcl.bean.arguments.ValueArgument
import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.exceptions.NotSelectedException
import com.mrshiehx.cmcl.modules.account.authentication.AccountRefresher
import com.mrshiehx.cmcl.modules.account.authentication.microsoft.MicrosoftAuthentication
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.YggdrasilAuthentication
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.authlib.AuthlibInjectorApiProvider
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.authlib.AuthlibInjectorAuthentication
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.nide8auth.Nide8AuthAuthentication
import com.mrshiehx.cmcl.modules.account.skin.SkinDownloader
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.cmcl.AccountUtils
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.console.PrintingUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

object AccountFunction : Function {
    override val usageName: String = "account"
    override fun execute(arguments: Arguments) {
        if (!Function.checkArgs(
                arguments, 2, 1,
                ArgumentRequirement.ofSingle("l"),
                ArgumentRequirement.ofSingle("list"),
                ArgumentRequirement.ofSingle("r"),
                ArgumentRequirement.ofSingle("refresh"),
                ArgumentRequirement.ofSingle("cape"),
                ArgumentRequirement.ofSingle("skin"),
                ArgumentRequirement.ofSingle("s"),
                ArgumentRequirement.ofSingle("select"),
                ArgumentRequirement.ofValue("s"),
                ArgumentRequirement.ofValue("select"),
                ArgumentRequirement.ofValue("n"),
                ArgumentRequirement.ofValue("name"),
                ArgumentRequirement.ofValue("address"),
                ArgumentRequirement.ofValue("serverId"),
                ArgumentRequirement.ofValue("d"),
                ArgumentRequirement.ofValue("delete"),
                ArgumentRequirement.ofValue("cape"),
                ArgumentRequirement.ofValue("download-skin"),
                ArgumentRequirement.ofValue("skin"),
                ArgumentRequirement.ofValue("login")
            )
        ) return
        val config: JSONObject = Utils.getConfig()
        val accounts: JSONArray = config.optJSONArray("accounts") ?: JSONArray().also { config.put("accounts", it) }
        if (arguments.optArgument(1) is SingleArgument) {
            val argument = arguments.optArgument(1) as SingleArgument
            when (argument.key) {
                "l", "list" -> {
                    val i = AtomicInteger()
                    PrintingUtils.printTable(
                        arrayOf(
                            CMCL.getString("TABLE_ACCOUNTS_LIST_HEADER_SELECTED"),
                            CMCL.getString("TABLE_ACCOUNTS_LIST_HEADER_ORDER"),
                            CMCL.getString("TABLE_ACCOUNTS_LIST_HEADER_NAME"),
                            CMCL.getString("TABLE_ACCOUNTS_LIST_HEADER_TYPE"),
                            CMCL.getString("TABLE_ACCOUNTS_LIST_HEADER_OTHER_INFORMATION")
                        ), intArrayOf(8, 6, 20, 24, 50), false,
                        *getAllAccounts(accounts).map { account: JSONObject ->
                            accountInfoToTableItem(
                                account,
                                i.getAndIncrement(),
                                true
                            )
                        }.toTypedArray()
                    )
                }

                "r", "refresh" -> {
                    val account = try {
                        AccountUtils.getSelectedAccount(config, true)
                    } catch (ignore: NotSelectedException) {
                        return
                    }
                    try {
                        if (AccountRefresher.execute(account, accounts)) {
                            Utils.saveConfig(config)
                        }
                    } catch (e: ExceptionWithDescription) {
                        e.print()
                    }
                }

                "cape" -> {
                    val account = try {
                        AccountUtils.getSelectedAccount(config, true)
                    } catch (e: NotSelectedException) {
                        return
                    }
                    val loginMethod = account.optInt("loginMethod")
                    if (loginMethod != 0) {
                        println(CMCL.getString("ONLY_OFFLINE"))
                        return
                    }
                    account.remove("cape")
                    Utils.saveConfig(config)
                }

                "skin" -> {
                    val account = try {
                        AccountUtils.getSelectedAccount(config, true)
                    } catch (e: NotSelectedException) {
                        return
                    }
                    if (account.optInt("loginMethod") == 0) {
                        account.remove("providedSkin")
                        account.remove("offlineSkin")
                        account.remove("slim")
                        Utils.saveConfig(config)
                    } else {
                        println(CMCL.getString("SKIN_CANCEL_ONLY_FOR_OFFLINE"))
                    }
                }

                else -> println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", argument.originArray[0]))
            }
        } else if (arguments.optArgument(1) is ValueArgument) {
            val arg = arguments.optArgument(1) as ValueArgument
            val value = arg.value
            when (arg.key) {
                "cape" -> {
                    val account = try {
                        AccountUtils.getSelectedAccount(config, true)
                    } catch (e: NotSelectedException) {
                        return
                    }
                    val loginMethod = account.optInt("loginMethod")
                    if (loginMethod != 0) {
                        println(CMCL.getString("ONLY_OFFLINE"))
                        return
                    }
                    val file = File(value)
                    if (!file.exists() || file.isDirectory()) {
                        println(CMCL.getString("FILE_NOT_FOUND_OR_IS_A_DIRECTORY"))
                        return
                    }
                    account.put("cape", file.absolutePath)
                    Utils.saveConfig(config)
                }

                "skin" -> {
                    val account = try {
                        AccountUtils.getSelectedAccount(config, true)
                    } catch (e: NotSelectedException) {
                        return
                    }
                    val loginMethod = account.optInt("loginMethod")
                    if ("steve" != value && "alex" != value) {
                        if (loginMethod == 1) {
                            val file = File(value)
                            if (!file.exists() || file.isDirectory()) {
                                println(CMCL.getString("FILE_NOT_FOUND_OR_IS_A_DIRECTORY"))
                                return
                            }
                            var suffix = ""
                            val var2 = file.getName().lastIndexOf("\\.")
                            if (var2 != -1) {
                                suffix = "/" + file.getName().substring(var2 + 1)
                            }
                            val slim = InteractionUtils.yesOrNo(CMCL.getString("SKIN_TYPE_DEFAULT_OR_SLIM"))
                            try {
                                YggdrasilAuthentication.uploadSkin(
                                    AuthlibInjectorApiProvider(account.optString("url")),
                                    account.optString("uuid"),
                                    account.optString("accessToken"),
                                    file.getName(),
                                    suffix,
                                    FileUtils.getBytes(file),
                                    slim
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                println(CMCL.getString("UNABLE_SET_SKIN"))
                            }
                        } else if (loginMethod == 0) {
                            val file = File(value)
                            if (!file.exists() || file.isDirectory()) {
                                println(CMCL.getString("FILE_NOT_FOUND_OR_IS_A_DIRECTORY"))
                                return
                            }
                            if (InteractionUtils.yesOrNo(CMCL.getString("SKIN_TYPE_DEFAULT_OR_SLIM"))) {
                                account.put("slim", true)
                            } else {
                                account.remove("slim")
                            }
                            account.remove("providedSkin")
                            account.put("offlineSkin", file.absolutePath)
                            Utils.saveConfig(config)
                        } else {
                            println(CMCL.getString("UPLOAD_SKIN_ONLY_OAS_OR_OFFLINE"))
                        }
                    } else {
                        val steve = "steve" == value
                        if (loginMethod == 0) {
                            account.remove("offlineSkin")
                            account.remove("slim")
                            account.put("providedSkin", if (steve) "steve" else "alex")
                            Utils.saveConfig(config)
                        } else if (loginMethod == 1) {
                            val name: String
                            val skin: ByteArray
                            var slim = false
                            try {
                                if (steve) {
                                    name = "steve.png"
                                    val `is` = AccountFunction::class.java.getResourceAsStream("/skin/steve.png")
                                    if (`is` == null) {
                                        println(CMCL.getString("SKIN_STEVE_NOT_FOUND"))
                                        return
                                    }
                                    skin = FileUtils.inputStream2ByteArray(`is`)
                                } else {
                                    name = "alex.png"
                                    val `is` = AccountFunction::class.java.getResourceAsStream("/skin/alex.png")
                                    if (`is` == null) {
                                        println(CMCL.getString("SKIN_ALEX_NOT_FOUND"))
                                        return
                                    }
                                    skin = FileUtils.inputStream2ByteArray(`is`)
                                    slim = true
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                println(CMCL.getString(if (steve) "SKIN_STEVE_UNABLE_READ" else "SKIN_ALEX_UNABLE_READ"))
                                return
                            }
                            try {
                                YggdrasilAuthentication.uploadSkin(
                                    AuthlibInjectorApiProvider(account.optString("url")),
                                    account.optString("uuid"),
                                    account.optString("accessToken"),
                                    name,
                                    "png",
                                    skin,
                                    slim
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                println(CMCL.getString("UNABLE_SET_SKIN"))
                            }
                        } else {
                            println(CMCL.getString("UPLOAD_SKIN_ONLY_OAS_OR_OFFLINE"))
                        }
                    }
                }

                "s", "select" -> {
                    val order = try {
                        value.toInt()
                    } catch (e: NumberFormatException) {
                        Utils.printfln(CMCL.getString("CONSOLE_UNSUPPORTED_VALUE"), value)
                        return
                    }
                    if (order < 0) {
                        Utils.printfln(CMCL.getString("ACCOUNT_NOT_EXISTS"), order)
                        return
                    }
                    val account: JSONObject? = accounts.optJSONObject(order)
                    if (account == null) {
                        Utils.printfln(CMCL.getString("ACCOUNT_NOT_EXISTS"), order)
                        return
                    }
                    if (!AccountUtils.isValidAccount(account)) {
                        Utils.printfln(CMCL.getString("ACCOUNT_INVALID"), order)
                        return
                    }
                    account.put("selected", true)
                    var i = 0
                    while (i < accounts.length()) {
                        if (i != order) {
                            accounts.optJSONObject(i)?.put("selected", false)
                        }
                        i++
                    }
                    config.put("accounts", accounts)
                    Utils.saveConfig(config)
                }

                "d", "delete" -> try {
                    val order = value.toInt()
                    //accounts.remove(order);

                    //防止accounts内混入奇奇怪怪的东西
                    val map: MutableMap<Int, Int> = HashMap()
                    var accountCount = 0
                    var i = 0
                    while (i < accounts.length()) {
                        val `object`: Any = accounts.get(i)
                        if (AccountUtils.isValidAccount(`object`)) {
                            map[accountCount++] = i
                        }
                        i++
                    }
                    val integer = map[order]
                    if (integer != null) {
                        accounts.remove(integer)
                        Utils.saveConfig(config)
                    }
                } catch (e: NumberFormatException) {
                    Utils.printfln(CMCL.getString("CONSOLE_UNSUPPORTED_VALUE"), value)
                    return
                }

                "download-skin" -> {
                    val account = try {
                        AccountUtils.getSelectedAccount(config, true)
                    } catch (e: NotSelectedException) {
                        return
                    }
                    val loginMethod = account.optInt("loginMethod")
                    val file = File(value)
                    if ((loginMethod == 1 && account.optString("url").isNotEmpty())
                        || (loginMethod == 2 && account.optString("uuid").isNotEmpty())
                        || (loginMethod == 3 && account.optString("uuid").isNotEmpty() && account.optString("serverId")
                            .isNotEmpty())
                    ) {
                        if (!file.exists())
                            SkinDownloader.start(file, account)
                        else
                            Utils.printfln(Utils.getString("CONSOLE_FILE_EXISTS"), file.absolutePath)
                    } else {
                        println(CMCL.getString("CONSOLE_ACCOUNT_UN_OPERABLE_MISSING_INFO"))
                    }
                }

                "login" -> {
                    val select = arguments.contains("s") || arguments.contains("select")
                    if ("offline".equals(value, ignoreCase = true)) {
                        val nameArgument =
                            Optional.ofNullable(arguments.optArgument("name")).orElseGet { arguments.optArgument("n") }
                        if (nameArgument !is ValueArgument) {
                            println(Utils.getString("ACCOUNT_LOGIN_NEED_NAME"))
                            return
                        }
                        val name = nameArgument.value
                        var indexOf = -1
                        var i = 0
                        while (i < accounts.length()) {
                            val account: JSONObject? = accounts.optJSONObject(i)
                            if (account == null) {
                                i++
                                continue
                            }
                            if (account.optString("playerName") == name && account.optInt("loginMethod") == 0) {
                                indexOf = i
                            } else {
                                if (select) account.put("selected", false)
                            }
                            i++
                        }
                        val account = JSONObject()
                        account.put("playerName", name)
                        account.put("selected", select)
                        account.put("loginMethod", 0)
                        if (indexOf >= 0) {
                            if (InteractionUtils.yesOrNo(
                                    String.format(
                                        CMCL.getString("CONSOLE_REPLACE_LOGGED_ACCOUNT"),
                                        indexOf
                                    )
                                )
                            ) {
                                accounts.put(indexOf, account)
                                config.put("accounts", accounts)
                                Utils.saveConfig(config)
                            }
                        } else {
                            accounts.put(account)
                            Utils.saveConfig(config)
                        }
                    } else if ("microsoft".equals(value, ignoreCase = true)) {
                        val account = MicrosoftAuthentication.loginMicrosoftAccount()
                            ?: return
                        addOrReplace(account, config, select)
                        {
                            it.optInt("loginMethod") == 2 && account.optString("id") == it.optString("id") && AccountUtils.isValidAccount(
                                it
                            )
                        }
                    } else if ("authlib".equals(value, ignoreCase = true)) {
                        val addressArgument = arguments.optArgument("address")
                        if (addressArgument !is ValueArgument) {
                            println(Utils.getString("ACCOUNT_LOGIN_NEED_ADDRESS"))
                            return
                        }
                        val address = addressArgument.value
                        val account: JSONObject = try {
                            AuthlibInjectorAuthentication.authlibInjectorLogin(address, null, select)
                        } catch (e: Exception) {
                            Utils.printfln(CMCL.getString("FAILED_TO_LOGIN_OTHER_AUTHENTICATION_ACCOUNT"), e)
                            return
                        } ?: return
                        addOrReplace(account, config, select) { jsonObject ->
                            jsonObject.optInt("loginMethod") == 1 &&
                                    NetworkUtils.urlEqualsIgnoreSlash(
                                        account.optString("url"),
                                        jsonObject.optString("url")
                                    ) && account.optString("uuid") == jsonObject.optString("uuid") && AccountUtils.isValidAccount(
                                jsonObject
                            )
                        }
                    } else if ("nide8auth".equals(value, ignoreCase = true)) {
                        val serverIdArgument = arguments.optArgument("serverId")
                        if (serverIdArgument !is ValueArgument) {
                            println(Utils.getString("ACCOUNT_LOGIN_NEED_SERVER_ID"))
                            return
                        }
                        val serverId = serverIdArgument.value
                        val account = try {
                            Nide8AuthAuthentication.nide8authLogin(serverId, null, select)
                        } catch (e: Exception) {
                            Utils.printfln(CMCL.getString("FAILED_TO_LOGIN_NIDE8AUTH_ACCOUNT"), e)
                            return
                        } ?: return
                        addOrReplace(account, config, select) { jsonObject ->
                            jsonObject.optInt("loginMethod") == 3 &&
                                    account.optString("serverId").equals(
                                        jsonObject.optString("serverId"),
                                        ignoreCase = true
                                    ) && account.optString("uuid") == jsonObject.optString("uuid") && AccountUtils.isValidAccount(
                                jsonObject
                            )
                        }
                    } else {
                        println(CMCL.getString("ACCOUNT_LOGIN_UNKNOWN_LOGIN_METHOD", value))
                    }
                }

                else -> println(
                    CMCL.getString(
                        "CONSOLE_UNKNOWN_COMMAND_OR_MEANING",
                        (arguments.optArgument(1) as ValueArgument).originString
                    )
                )
            }
        } else {
            println(CMCL.getString("CONSOLE_ONLY_HELP", arguments.optArgument(1)!!.originString))
        }
    }


    private fun addOrReplace(
        account: JSONObject,
        config: JSONObject,
        select: Boolean,
        filter: (JSONObject) -> Boolean
    ) {
        val accounts: JSONArray = config.optJSONArray("accounts") ?: JSONArray()
        account.put("selected", select)
        var indexOf = -1
        for (i in 0 until accounts.length()) {
            val jsonObject1: JSONObject? = accounts.optJSONObject(i)
            if (jsonObject1 != null) {
                if (filter.invoke(jsonObject1)) {
                    indexOf = i
                } else {
                    if (select) jsonObject1.put("selected", false) //如果加了--select，那就是要选择新登录的账号，其他账号一律变为未选择的
                }
            }
        }
        if (indexOf < 0) {
            accounts.put(account)
            config.put("accounts", accounts)
            Utils.saveConfig(config)
            println(CMCL.getString("MESSAGE_LOGINED_TITLE"))
        } else {
            if (InteractionUtils.yesOrNo(String.format(CMCL.getString("CONSOLE_REPLACE_LOGGED_ACCOUNT"), indexOf))) {
                accounts.put(indexOf, account)
                config.put("accounts", accounts)
                Utils.saveConfig(config)
                println(CMCL.getString("MESSAGE_LOGINED_TITLE"))
            }
        }
    }

    fun getAllAccounts(accounts: JSONArray): List<JSONObject> {
        return Utils.iteratorToStream(accounts.iterator()).filter(AccountUtils::isValidAccount)
            .map { x -> x as JSONObject }.collect(Collectors.toList())
    }

    fun accountInfoToTableItem(account: JSONObject, order: Int, showSelected: Boolean): Array<String> {
        val accountType: String
        val otherInformation: String
        when (account.optInt("loginMethod")) {
            1 -> {
                accountType = CMCL.getString("ACCOUNT_TYPE_OAS")
                otherInformation = account.optString("serverName") + " " + account.optString("url")
            }

            2 -> {
                accountType = CMCL.getString("ACCOUNT_TYPE_MICROSOFT")
                otherInformation = ""
            }

            3 -> {
                accountType = CMCL.getString("ACCOUNT_TYPE_NIDE8AUTH")
                otherInformation = account.optString("serverName") + " " + account.optString("serverId")
            }

            else -> {
                accountType = CMCL.getString("ACCOUNT_TYPE_OFFLINE")
                otherInformation = ""
            }
        }
        return if (showSelected) arrayOf(
            if (account.optBoolean("selected")) CMCL.getString("YES_SHORT") else "", order.toString(),
            account.optString("playerName", "XPlayer"),
            accountType,
            otherInformation
        ) else arrayOf(
            order.toString(),
            account.optString("playerName", "XPlayer"),
            accountType,
            otherInformation
        )
    }

    fun getAccountType(account: JSONObject): String = when (account.optInt("loginMethod")) {
        1 -> CMCL.getString("ACCOUNT_TYPE_OAS")
        2 -> CMCL.getString("ACCOUNT_TYPE_MICROSOFT")
        3 -> CMCL.getString("ACCOUNT_TYPE_NIDE8AUTH")
        else -> CMCL.getString("ACCOUNT_TYPE_OFFLINE")
    }

    fun getAccountTypeWithInformation(account: JSONObject): String =
        when (account.optInt("loginMethod")) {
            1 -> CMCL.getString(
                "ACCOUNT_TYPE_OAS_WITH_DETAIL",
                account.optString("serverName"),
                account.optString("url")
            )

            2 -> CMCL.getString("ACCOUNT_TYPE_MICROSOFT")
            3 -> CMCL.getString(
                "ACCOUNT_TYPE_NIDE8AUTH_WITH_DETAIL",
                account.optString("serverName"),
                account.optString("serverId")
            )

            else -> CMCL.getString("ACCOUNT_TYPE_OFFLINE")
        }

}
