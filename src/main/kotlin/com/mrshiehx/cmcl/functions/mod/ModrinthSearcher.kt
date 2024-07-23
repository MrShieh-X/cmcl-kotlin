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
package com.mrshiehx.cmcl.functions.mod

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.modSources.modrinth.ModrinthManager
import com.mrshiehx.cmcl.utils.Utils
import org.json.JSONObject

object ModrinthSearcher {
    fun search(mr: ModrinthManager, modNameInput: String?, modIdInput: String?, limit: Int): Result? {
        val mod: JSONObject?
        val modByID: JSONObject?
        val modName: String
        val modID: String
        if (!Utils.isEmpty(modNameInput)) {
            mod = mr.search(modNameInput, if (limit < 0) 50 else limit) ?: return null
            modName = mod.optString("title")
            modID = mod.optString("project_id")
            modByID = try {
                mr.getByID(modID)
            } catch (ignore: Exception) {
                null
            }
        } else {
            try {
                modByID = mr.getByID(modIdInput!!)
                modName = modByID.optString("title")
                modID = modByID.optString("id")
                mod = null
            } catch (e: ModrinthManager.IncorrectCategoryAddon) {
                val target = e.section
                println(
                    CMCL.getString("CF_GET_BY_ID_INCORRECT_CATEGORY_DETAIL")
                        .replace("\${NAME}", mr.section.nameAllLowerCase)
                        .replace("\${TARGET}", target?.nameAllLowerCase ?: "null")
                )
                return null
            } catch (e: Exception) {
                println(CMCL.getString("CF_GET_BY_ID_FAILED", e).replace("\${NAME}", mr.section.nameAllLowerCase))
                return null
            }
        }
        return Result(mod, modByID, modName, modID)
    }

    class Result(val mod: JSONObject?, val modByID: JSONObject?, val modName: String, val modID: String)
}
