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

package com.mrshiehx.cmcl.api.download

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.utils.Utils.getConfig
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.Utils.saveConfig
import org.json.JSONObject
import java.util.*

class DownloadSource {
    companion object {
        fun getProvider(): DownloadApiProvider {
            return getProvider(getConfig())
        }

        fun getProvider(config: JSONObject): DownloadApiProvider {
            if (!config.has("downloadSource")) {
                val sources = listOf(
                    getString("DOWNLOAD_SOURCE_OFFICIAL") to 0,
                    getString("DOWNLOAD_SOURCE_BMCLAPI") to 1,
                )
                for (pair in sources) {
                    println("[${pair.second}]${pair.first}")
                }
                val defaultDownloadSource = if (CMCL.language.locale == Locale.CHINA) 1 else 0
                var value = defaultDownloadSource
                print(getString("MESSAGE_SELECT_DOWNLOAD_SOURCE", defaultDownloadSource))
                try {
                    value = Scanner(System.`in`).nextLine().toInt()
                } catch (ignore: NumberFormatException) {
                } catch (ignore: NoSuchElementException) {
                }
                config.put("downloadSource", value)
                saveConfig(config)
            }
            return when (config.optInt("downloadSource")) {
                1 -> BMCLApiProvider
                else -> DefaultApiProvider
            }
        }
    }
}