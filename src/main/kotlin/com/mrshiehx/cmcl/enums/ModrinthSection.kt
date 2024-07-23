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

package com.mrshiehx.cmcl.enums

import com.mrshiehx.cmcl.CMCL.getString

enum class ModrinthSection(
    val projectType: String,
    val informationNameTip: String,
    val informationIdTip: String,
    val nameAllLowerCase: String,
    val nameFirstUpperCase: String
) {
    MOD(
        "mod",
        getString("CF_INFORMATION_MOD_NAME"),
        getString("CF_INFORMATION_MOD_ID"),
        getString("CF_BESEARCHED_MOD_ALC"),
        getString("CF_BESEARCHED_MOD_FUC")
    ),
    MODPACK(
        "modpack",
        getString("CF_INFORMATION_MODPACK_NAME"),
        getString("CF_INFORMATION_MODPACK_ID"),
        getString("CF_BESEARCHED_MODPACK_ALC"),
        getString("CF_BESEARCHED_MODPACK_FUC")
    );

    override fun toString(): String = projectType

    companion object {
        fun xvalueOf(projectType: String?): ModrinthSection? {
            for (modrinthSection in entries) {
                if (projectType == modrinthSection.projectType) return modrinthSection
            }
            return null
        }
    }
}