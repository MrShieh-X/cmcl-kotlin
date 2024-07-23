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
package com.mrshiehx.cmcl.functions.root

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.console.PrintingUtils
import java.io.File

object VersionsLister {
    fun execute(dir: String?) {
        val parent = if (!isEmpty(dir)) File(dir) else CMCL.gameDir
        val dir2 = if (!isEmpty(dir)) File(dir, "versions") else CMCL.versionsDir
        println(getString("MESSAGE_BEFORE_LIST_VERSIONS", parent.absolutePath))
        val list = CMCL.listVersions(dir2)
        PrintingUtils.printListItems(list, false, 4, 2, true)
    }
}
