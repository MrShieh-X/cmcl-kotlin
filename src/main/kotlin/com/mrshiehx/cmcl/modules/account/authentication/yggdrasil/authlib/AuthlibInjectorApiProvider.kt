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

import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.YggdrasilAuthenticationApiProvider
import com.mrshiehx.cmcl.utils.internet.NetworkUtils

class AuthlibInjectorApiProvider(url: String) : YggdrasilAuthenticationApiProvider {
    private val main = NetworkUtils.addSlashIfMissing(url)

    override val authenticationURL = main + "authserver/authenticate"

    override val refreshmentURL = main + "authserver/refresh"

    override val validationURL = main + "authserver/validate"

    override fun getSkinUploadURL(uuid: String): String =
        main + "api/user/profile/" + uuid.replace("-", "") + "/skin"


    override fun getProfilePropertiesURL(uuid: String): String =
        main + "sessionserver/session/minecraft/profile/" + uuid.replace("-", "")


    override fun toString() = main

}
