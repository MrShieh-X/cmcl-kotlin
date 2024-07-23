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
package com.mrshiehx.cmcl.modules.account.authentication

import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.modules.account.authentication.microsoft.MicrosoftAuthentication
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.authlib.AuthlibInjectorAuthentication
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.nide8auth.Nide8AuthAuthentication
import org.json.JSONArray
import org.json.JSONObject

object AccountRefresher {
    /**
     * Refresh account
     *
     * @return whether the account has been modified
     */
    @Throws(ExceptionWithDescription::class)
    fun execute(selectedAccount: JSONObject, accounts: JSONArray): Boolean {
        val loginMethod = selectedAccount.optInt("loginMethod")
        return when (loginMethod) {
            1 -> AuthlibInjectorAuthentication.refresh(selectedAccount, accounts)
            2 -> MicrosoftAuthentication.refresh(selectedAccount, accounts)
            3 -> Nide8AuthAuthentication.refresh(selectedAccount, accounts)
            else -> false
        }
    }
}
