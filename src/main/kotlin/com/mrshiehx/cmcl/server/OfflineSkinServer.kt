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
package com.mrshiehx.cmcl.server

import com.mrshiehx.cmcl.constants.Constants.isDebug
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.isBlank
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class OfflineSkinServer(
    port: Int,
    val uuid: String,
    val playerName: String,
    val skin: ByteArray? = null,
    val skinLength: Int? = null,
    val skinHash: String? = null,
    val isSlim: Boolean? = null,
    val cape: ByteArray? = null,
    val capeLength: Int? = null,
    val capeHash: String? = null
) : HttpServer(port) {


    companion object {
        val keyPair: KeyPair? by lazy {
            try {
                val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
                keyPairGenerator.initialize(4096, SecureRandom())
                return@lazy keyPairGenerator.genKeyPair()
            } catch (e: NoSuchAlgorithmException) {
                if (isDebug()) e.printStackTrace()
                return@lazy null
            }
        }

        private fun sign(data: String): String {
            return try {
                val signature = Signature.getInstance("SHA1withRSA")
                signature.initSign(keyPair!!.private, SecureRandom())
                signature.update(data.toByteArray(StandardCharsets.UTF_8))
                Base64.getEncoder().encodeToString(signature.sign())
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    override fun serve(session: IHTTPSession): Response {
        val method = session.method
        val uri = session.uri
        if (method == Method.GET) {
            val matcherForProfile =
                Pattern.compile("/sessionserver/session/minecraft/profile/(?<uuid>[a-f0-9]{32})").matcher(uri)
            val matcherForTextures = Pattern.compile("/textures/(?<hash>[a-f0-9]{64})").matcher(uri)
            if (Pattern.compile("^/$").matcher(uri).find()) {
                return root(session)
            } else if (Pattern.compile("/status").matcher(uri).find()) {
                return status(session)
            } else if (Pattern.compile("/sessionserver/session/minecraft/hasJoined").matcher(uri).find()) {
                val query: Map<String, String?> = Utils.mapOf(NetworkUtils.parseQuery(session.queryParameterString))
                return hasJoined(session, query)
            } else if (matcherForProfile.find()) {
                return profile(session, matcherForProfile)
            } else if (matcherForTextures.find()) {
                return textures(session, matcherForTextures)
            } else if (Pattern.compile("/sessionserver/session/minecraft/join").matcher(uri).find()) {
                return ServerUtils.badRequest()
            } else if (Pattern.compile("/api/profiles/minecraft").matcher(uri).find()) {
                return ServerUtils.badRequest()
            }
        }
        if (method == Method.POST) {
            if (Pattern.compile("/sessionserver/session/minecraft/join").matcher(uri).find()) {
                return ServerUtils.noContent()
            } else if (Pattern.compile("/api/profiles/minecraft").matcher(uri).find()) {
                return try {
                    profiles(session)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }
        return ServerUtils.notFound()
    }

    @Throws(IOException::class)
    private fun profiles(session: IHTTPSession): Response {
        val i = session.inputStream
        val s: String = Utils.inputStream2String(i, i.available())
        val jsonArray = JSONUtils.parseJSONArray(s)
            ?: return ServerUtils.badRequest()
        val result = JSONArray()
        if (jsonArray.any { it == playerName }) {
            result.put(JSONObject().put("id", uuid).put("name", playerName))
        }
        return ServerUtils.ok(result)
    }

    private fun textures(session: IHTTPSession, matcher: Matcher): Response {
        val hash = matcher.group("hash")
        var inputStream: InputStream? = null
        var inputStreamLength = 0
        if (hash == skinHash && skin != null) {
            inputStream = ByteArrayInputStream(skin)
            inputStreamLength = skinLength!!
        } else if (hash == capeHash && cape != null) {
            inputStream = ByteArrayInputStream(cape)
            inputStreamLength = capeLength!!
        }
        if (inputStream != null && inputStreamLength > 0) {
            val response =
                newFixedLengthResponse(Response.Status.OK, "image/png", inputStream, inputStreamLength.toLong())
            response.addHeader("Etag", String.format("\"%s\"", hash))
            response.addHeader("Cache-Control", "max-age=2592000, public")
            return response
        }
        return ServerUtils.notFound()
    }

    private fun profile(session: IHTTPSession, matcher: Matcher): Response {
        val uuid = matcher.group("uuid")
        return if (this.uuid == uuid) {
            profile
        } else ServerUtils.noContent()
    }

    private val profile: Response
        get() {
            val textures = JSONObject()
            if (skin != null && !isBlank(skinHash)) {
                val skinJSONObject = JSONObject().put("url", rootUrl + "textures/" + skinHash)
                if (isSlim == true) {
                    skinJSONObject.put("metadata", JSONObject().put("model", "slim"))
                }
                textures.put("SKIN", skinJSONObject)
            }
            if (cape != null && !isBlank(capeHash)) {
                textures.put("CAPE", JSONObject().put("url", rootUrl + "textures/" + capeHash))
            }

            val texturesBase64 = JSONObject()
            with(texturesBase64) {
                put("timestamp", System.currentTimeMillis())
                put("profileId", uuid)
                put("profileName", playerName)
                put("textures", textures)
            }

            val value =
                Base64.getEncoder().encodeToString(texturesBase64.toString(2).toByteArray(StandardCharsets.UTF_8))
            val properties = JSONArray()

            val texturesProperty = JSONObject()
            with(texturesProperty) {
                put("name", "textures")
                put("value", value)
                put("signature", sign(value))
            }

            properties.put(texturesProperty)

            val main = JSONObject()
            with(main) {
                put("id", uuid)
                put("name", playerName)
                put("properties", properties)
            }
            return ServerUtils.ok(main)
        }

    private fun hasJoined(session: IHTTPSession, query: Map<String, String?>): Response {
        val username = query["username"]
        if (isEmpty(username)) {
            return ServerUtils.badRequest()
        }
        return if (playerName == username) {
            profile
        } else ServerUtils.noContent()
    }

    private fun status(session: IHTTPSession): Response = ServerUtils.ok(JSONObject().apply {
        put("user.count", 1 /*角色数量*/)
        put("token.count", 0)
        put("pendingAuthentication.count", 0)
    })

    private fun root(session: IHTTPSession): Response {
        val keyPair = OfflineSkinServer.keyPair ?: return ServerUtils.internalError()
        val encoded = keyPair.public.encoded
        /*val key = """
            -----BEGIN PUBLIC KEY-----
            ${Base64.getMimeEncoder(76, byteArrayOf('\n'.code.toByte())).encodeToString(encoded)}
            -----END PUBLIC KEY-----
            
            """.trimIndent()*/
        val key = "-----BEGIN PUBLIC KEY-----\n" + Base64.getMimeEncoder(76, byteArrayOf('\n'.code.toByte()))
            .encodeToString(encoded) + "\n-----END PUBLIC KEY-----\n";
        return ServerUtils.ok(JSONObject().apply {
            put("signaturePublickey", key)
            put("meta", JSONObject().apply {
                put("serverName", "CMCL")
                put("implementationName", "CMCL")
                put("implementationVersion", "1.0")
                put("feature.non_email_login", true)
            })
            put("skinDomains", JSONArray().apply {
                put("127.0.0.1")
                put("localhost")
            })
        })


    }
}