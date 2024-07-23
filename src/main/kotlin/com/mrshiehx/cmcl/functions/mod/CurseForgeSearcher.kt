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
import com.mrshiehx.cmcl.enums.CurseForgeSection.Companion.valueOf
import com.mrshiehx.cmcl.modSources.curseforge.CurseForgeManager
import com.mrshiehx.cmcl.utils.Utils
import org.json.JSONObject

object CurseForgeSearcher {
    fun search(cf: CurseForgeManager, modNameInput: String?, modIdInput: String?, limit: Int): JSONObject? =
        if (!Utils.isEmpty(modNameInput)) {
            cf.search(modNameInput, if (limit < 0) 50 else limit)
        } else {
            val alc: String = cf.section.nameAllLowerCase
            try {
                cf.getByID(modIdInput!!)
            } catch (e: CurseForgeManager.NotMinecraftAddon) {
                println(CMCL.getString("CF_GET_BY_ID_NOT_OF_MC", e.gameId).replace("\${NAME}", alc))
                null
            } catch (e: CurseForgeManager.IncorrectCategoryAddon) {
                val target = valueOf(e.gameCategoryId)
                if (target == null) {
                    println(
                        CMCL.getString("CF_GET_BY_ID_INCORRECT_CATEGORY", e.gameCategoryId).replace("\${NAME}", alc)
                    )
                } else {
                    println(
                        CMCL.getString("CF_GET_BY_ID_INCORRECT_CATEGORY_DETAIL").replace("\${NAME}", alc)
                            .replace("\${TARGET}", target.nameAllLowerCase)
                    )
                }
                null
            } catch (e: Exception) {
                println(CMCL.getString("CF_GET_BY_ID_FAILED", e).replace("\${NAME}", alc))
                null
            }
        }

}
