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

import com.mrshiehx.cmcl.constants.Constants
import fi.iki.elonen.NanoHTTPD
import org.json.JSONArray
import org.json.JSONObject

object ServerUtils {
    fun errorHtml(error: String): String {
        return """
               <html>
               <head><title>$error - CMCL ${Constants.CMCL_VERSION_NAME}</title></head>
               <body>
               <center><h1>$error</h1></center>
               <hr><center>CMCL ${Constants.CMCL_VERSION_NAME}</center>
               </body>
               </html>""".trimIndent()
    }

    fun ok(response: JSONObject): NanoHTTPD.Response =
        NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/json", response.toString(2))

    fun ok(response: JSONArray): NanoHTTPD.Response =
        NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/json", response.toString(2))

    fun notFound(): NanoHTTPD.Response = NanoHTTPD.newFixedLengthResponse(
        NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, errorHtml("404 Not Found")
    )

    fun noContent(): NanoHTTPD.Response =
        NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NO_CONTENT, NanoHTTPD.MIME_HTML, "")

    fun badRequest(): NanoHTTPD.Response = NanoHTTPD.newFixedLengthResponse(
        NanoHTTPD.Response.Status.BAD_REQUEST, NanoHTTPD.MIME_HTML, errorHtml("400 Bad Request")
    )

    fun internalError(): NanoHTTPD.Response = NanoHTTPD.newFixedLengthResponse(
        NanoHTTPD.Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_HTML, errorHtml("500 internal error")
    )

}
