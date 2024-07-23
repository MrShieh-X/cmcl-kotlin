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
package com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.nide8auth

import com.mrshiehx.cmcl.utils.Utils.isEmpty
import org.json.JSONObject

class Nide8AuthInformation(val serverId: String, val serverName: String) {
    companion object {
        fun valueOf(account: JSONObject): Nide8AuthInformation? {
            return if (account.optInt("loginMethod") == 3 && !isEmpty(account.optString("serverId"))) {
                Nide8AuthInformation(account.optString("serverId"), account.optString("serverName"))
            } else null
        }
    }
}