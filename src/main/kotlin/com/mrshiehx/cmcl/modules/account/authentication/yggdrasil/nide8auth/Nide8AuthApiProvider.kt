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
package com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.nide8auth

import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.YggdrasilAuthenticationApiProvider

class Nide8AuthApiProvider(serverId: String) : YggdrasilAuthenticationApiProvider {
    val baseUrl: String = "https://auth.mc-user.com:233/$serverId/"

    override val authenticationURL: String = baseUrl + "authserver/authenticate"
    override val refreshmentURL: String = baseUrl + "authserver/refresh"
    override val validationURL: String = baseUrl + "authserver/validate"

    @Throws(UnsupportedOperationException::class)
    override fun getSkinUploadURL(uuid: String): String = throw UnsupportedOperationException()


    override fun getProfilePropertiesURL(uuid: String): String =
        baseUrl + "sessionserver/session/minecraft/profile/" + uuid.replace("-", "")

}
