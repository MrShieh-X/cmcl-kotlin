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

package com.mrshiehx.cmcl.constants

import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.ManifestUtils
import java.io.File

object Constants {
    const val CMCL_VERSION_NAME = "2.2.2"
    const val CMCL_VERSION_CODE = 22
    const val COPYRIGHT = "Copyright (C) 2021-2024  MrShiehX"
    const val SOURCE_CODE = "https://www.github.com/MrShieh-X/cmcl-kotlin"
    const val INDENT_FACTOR = 2 //JsonObject转String的间隔
    const val DEFAULT_DOWNLOAD_THREAD_COUNT = 64

    val DEFAULT_CONFIG_FILE = File("cmcl.json")

    val CLIENT_ID: String? by lazy {
        getMicrosoftAuthenticationClientID()
    }

    var ECHO_OPEN_FOR_IMMERSIVE = true

    val CHECK_FOR_UPDATES_INFORMATION_URLS = arrayOf(
        "https://gitee.com/MrShiehX/cmcl-kotlin/raw/master/new_version.json",
        "https://raw.githubusercontent.com/MrShieh-X/cmcl-kotlin/master/new_version.json"
    )

    fun getCurseForgeApiKey(): String? {
        val s = System.getProperty("cmcl.curseforge.apikey")
        return if (!isEmpty(s)) s else ManifestUtils.getString("CurseForge-ApiKey")
    }

    fun getMicrosoftAuthenticationClientID(): String? {
        val s = System.getProperty("cmcl.authentication.clientId")
        return if (!isEmpty(s)) s else ManifestUtils.getString("Microsoft-Authentication-ClientID")
    }

    fun isRelease(): Boolean {
        return "true" == ManifestUtils.getString("Is-Release")
    }

    fun isDebug(): Boolean {
        return !isRelease() || System.getProperty("debug") == "true"
    }
}
