package com.mrshiehx.cmcl.server

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.exceptions.AuthenticationException
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import fi.iki.elonen.NanoHTTPD
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

class MicrosoftAuthenticationServer(private val port: Int) : NanoHTTPD(port) {
    private val future = CompletableFuture<String?>()
    val redirectURI: String
        get() = "http%3A%2F%2Flocalhost%3A$port%2Fauthentication-response"

    @get:Throws(InterruptedException::class, ExecutionException::class)
    val code: String?
        get() = future.get()

    override fun serve(session: IHTTPSession): Response {
        if (session.method != Method.GET || "/authentication-response" != session.uri) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML, "")
        }
        val s = session.queryParameterString
        if (Utils.isEmpty(s)) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_HTML, "")
        }
        val query: Map<String, String?> = Utils.mapOf(NetworkUtils.parseQuery(s))
        if (query.containsKey("code")) {
            val c = query["code"]
            future.complete(c)
        } else {
            future.completeExceptionally(AuthenticationException("failed to authenticate"))
        }
        val html = """<!DOCTYPE html>
<html lang="en-US">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${CMCL.getString("WEB_TITLE_LOGIN_MICROSOFT_ACCOUNT_RESPONSE")}</title>
</head>

<body>
    <div>${CMCL.getString("ON_AUTHENTICATED_PAGE_TEXT")}</div>

    <script>
        setTimeout(function() {open("about:blank","_self").close();}, 10000);
    </script>
</body>

</html>"""
        Thread {
            try {
                Thread.sleep(1000)
                stop()
            } catch (ignored: InterruptedException) {
            }
        }.start()
        return newFixedLengthResponse(Response.Status.OK, "text/html; charset=UTF-8", html)
    }
}