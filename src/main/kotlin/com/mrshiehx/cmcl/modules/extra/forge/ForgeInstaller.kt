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
package com.mrshiehx.cmcl.modules.extra.forge

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.modules.extra.ExtraInstaller
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.version.VersionModuleUtils
import org.json.JSONObject

object ForgeInstaller : ExtraInstaller() {
    override val extraName = "Forge"
    override val extraMerger = ForgeMerger

    override fun checkInstallable(gameJSON: JSONObject): Boolean {
        if (!isEmpty(VersionModuleUtils.getForgeVersion(gameJSON))) {
            println(CMCL.getString("INSTALL_MODLOADER_ALREADY_INSTALL", extraName))
            return false
        }
        if (!isEmpty(VersionModuleUtils.getFabricVersion(gameJSON))) {
            println(CMCL.getString("INSTALL_MODLOADER_ALREADY_INSTALL_ANOTHER_ONE", extraName, "Fabric"))
            return false
        }
        if (!isEmpty(VersionModuleUtils.getQuiltVersion(gameJSON))) {
            println(CMCL.getString("INSTALL_MODLOADER_ALREADY_INSTALL_ANOTHER_ONE", extraName, "Quilt"))
            return false
        }
        if (!isEmpty(VersionModuleUtils.getNeoForgeVersion(gameJSON))) {
            println(CMCL.getString("INSTALL_MODLOADER_ALREADY_INSTALL_ANOTHER_ONE", extraName, "NeoForge"))
            return false
        }
        return true
    }
}
