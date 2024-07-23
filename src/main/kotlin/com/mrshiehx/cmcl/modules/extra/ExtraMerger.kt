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
package com.mrshiehx.cmcl.modules.extra

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.utils.Utils
import org.json.JSONObject
import java.io.File
import java.util.*

interface ExtraMerger {
    fun merge(
        minecraftVersion: String,
        headJSONObject: JSONObject,
        jarFile: File,
        askContinue: Boolean,
        extraVersion: String?
    ): Pair<Boolean, List<JSONObject>?>

    companion object {
        fun <V> selectExtraVersion(
            text: String?,
            extras: Map<String, V>,
            defaultVersion: String,
            extraName: String
        ): String? {
            text?.let { print(it) } //legal
            val scanner = Scanner(System.`in`)
            return try {
                val s = scanner.nextLine()
                if (!Utils.isEmpty(s)) {
                    val versionObject = extras[s]
                    if (versionObject != null) s else
                        selectExtraVersion(
                            CMCL.getString(
                                "INSTALL_MODLOADER_SELECT_NOT_FOUND",
                                s,
                                extraName,
                                defaultVersion
                            ), extras, defaultVersion, extraName
                        )
                } else {
                    defaultVersion
                }
            } catch (ignore: NoSuchElementException) {
                null
            }
        }
    }
}
