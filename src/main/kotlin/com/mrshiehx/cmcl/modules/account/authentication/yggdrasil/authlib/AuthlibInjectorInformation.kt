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
import com.mrshiehx.cmcl.functions.AccountFunction
import com.mrshiehx.cmcl.server.OfflineSkinServer
import com.mrshiehx.cmcl.utils.BytesUtils
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject
import java.io.File

class AuthlibInjectorInformation(
    serverAddress: String,
    val serverName: String,
    val playerName: String,
    val token: String,
    val uuid: String,
    val metadataEncoded: String?
) {

    val serverAddress: String

    var forOfflineSkin = false

    init {
        var tempServerAddress = serverAddress
        tempServerAddress = NetworkUtils.addHttpsIfMissing(tempServerAddress)
        if (!tempServerAddress.endsWith("/")) tempServerAddress += "/"
        this.serverAddress = tempServerAddress
    }

    val isEmpty: Boolean
        get() = Utils.isEmpty(serverAddress) ||
                Utils.isEmpty(uuid) ||
                Utils.isEmpty(token)

    companion object {
        fun valuesOf(
            account: JSONObject,
            token: String,
            uuid: String,
            allowOfflineSkin: Boolean
        ): AuthlibInjectorInformation? {
            if (account.optInt("loginMethod") == 1) {
                return AuthlibInjectorInformation(
                    account.optString("url"),
                    account.optString("serverName"),
                    account.optString("playerName", "XPlayer"),
                    account.optString("accessToken"),
                    account.optString("uuid"),
                    account.optString("metadataEncoded")
                )
            } else if (account.optInt("loginMethod") == 0 && allowOfflineSkin) {
                if (account.has("offlineSkin") || account.has("providedSkin") || account.has("cape")) {
                    var cape: ByteArray? = null
                    var capeLength = 0
                    var capeHash: String? = null
                    val capeString: String = account.optString("cape")
                    if (!Utils.isEmpty(capeString)) {
                        val file = File(capeString)
                        if (file.exists()) {
                            try {
                                cape = FileUtils.getBytes(file)
                                capeLength = cape.size
                                capeHash = BytesUtils.getBytesHashSHA256String(cape)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Utils.printflnErr(Utils.getString("CAPE_FILE_FAILED_LOAD"), file.absolutePath)
                            }
                        } else {
                            Utils.printflnErr(Utils.getString("CAPE_FILE_NOT_FOUND"), file.absolutePath)
                        }
                    }
                    var slim = false
                    var skin: ByteArray? = null
                    var skinLength = 0
                    var skinHash: String? = null
                    if (account.has("offlineSkin") || account.has("providedSkin")) {
                        val providedSkin: String = account.optString("providedSkin")
                        val pair = getSkin(account, providedSkin)
                        if (pair != null) {
                            skin = pair.first
                            slim = pair.second.first
                            skinLength = skin.size
                            skinHash = try {
                                BytesUtils.getBytesHashSHA256String(skin)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Utils.printflnErr(pair.second.second)
                                return null
                            }
                        }
                    }
                    try {
                        val offlineSkinServer = OfflineSkinServer(
                            0,
                            uuid,
                            account.optString("playerName", "XPlayer"),
                            skin,
                            skinLength,
                            skinHash,
                            slim,
                            cape,
                            capeLength,
                            capeHash
                        )
                        offlineSkinServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true)
                        val authlibInjectorInformation = AuthlibInjectorInformation(
                            offlineSkinServer.rootUrl,
                            "CMCL",
                            account.optString("playerName", "XPlayer"),
                            token,
                            uuid,
                            null
                        )
                        authlibInjectorInformation.forOfflineSkin = true
                        return authlibInjectorInformation
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println(CMCL.getString("UNABLE_TO_START_OFFLINE_SKIN_SERVER"))
                    }
                }
            }
            return null
        }

        private fun getSkin(account: JSONObject, providedSkin: String): Pair<ByteArray, Pair<Boolean, String>>? {
            val skin: ByteArray
            return when (providedSkin) {
                "steve" -> {
                    try {
                        val `is` = AccountFunction::class.java.getResourceAsStream("/skin/steve.png")
                        if (`is` == null) {
                            println(CMCL.getString("UNABLE_OFFLINE_CUSTOM_SKIN_STEVE_NOT_FOUND"))
                            return null
                        }
                        skin = FileUtils.inputStream2ByteArray(`is`)
                        Pair(skin, Pair(false, CMCL.getString("UNABLE_OFFLINE_CUSTOM_SKIN_STEVE_UNABLE_LOAD")))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println(CMCL.getString("UNABLE_OFFLINE_CUSTOM_SKIN_STEVE_UNABLE_LOAD"))
                        null
                    }
                }

                "alex" -> {
                    try {
                        val `is` = AccountFunction::class.java.getResourceAsStream("/skin/alex.png")
                        if (`is` == null) {
                            println(CMCL.getString("UNABLE_OFFLINE_CUSTOM_SKIN_ALEX_NOT_FOUND"))
                            return null
                        }
                        skin = FileUtils.inputStream2ByteArray(`is`)
                        Pair(skin, Pair(true, CMCL.getString("UNABLE_OFFLINE_CUSTOM_SKIN_ALEX_UNABLE_LOAD")))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println(CMCL.getString("UNABLE_OFFLINE_CUSTOM_SKIN_ALEX_UNABLE_LOAD"))
                        null
                    }
                }

                else -> {
                    val offlineSkin: String = account.optString("offlineSkin")
                    if (Utils.isEmpty(offlineSkin)) {
                        return null
                    }
                    val file = File(offlineSkin)
                    if (!file.exists()) {
                        Utils.printflnErr(
                            CMCL.getString("UNABLE_OFFLINE_CUSTOM_SKIN_FILE_NOT_FOUND"), file.absolutePath
                        )
                        return null
                    }
                    try {
                        skin = FileUtils.getBytes(file)
                        Pair(
                            skin,
                            Pair(
                                account.optBoolean("slim"),
                                String.format(
                                    CMCL.getString("UNABLE_OFFLINE_CUSTOM_SKIN_FILE_FAILED_LOAD"),
                                    file.absolutePath
                                )
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Utils.printflnErr(
                            CMCL.getString("UNABLE_OFFLINE_CUSTOM_SKIN_FILE_FAILED_LOAD"), file.absolutePath
                        )
                        null
                    }
                }
            }
        }
    }
}
