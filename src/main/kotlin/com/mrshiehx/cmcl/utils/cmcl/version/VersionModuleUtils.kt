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
package com.mrshiehx.cmcl.utils.cmcl.version

import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import org.json.JSONArray
import org.json.JSONObject

object VersionModuleUtils {
    fun getModuleVersion(head: JSONObject, mainClass: String?, libraryFirstAndSecond: String): String? {
        if (!isEmpty(mainClass) && head.optString("mainClass") != mainClass) return null
        val libraries = head.optJSONArray("libraries")
        return libraries
            ?.filterIsInstance<JSONObject>()
            ?.mapNotNull { it.optString("name") }
            ?.firstOrNull { name -> name.startsWith(libraryFirstAndSecond) && name.length > libraryFirstAndSecond.length }
            ?.substring(libraryFirstAndSecond.length)
    }

    fun getModuleVersion(head: JSONObject, moduleName: String): String? {
        val version = head.optJSONObject(moduleName)?.optString("version")
        if (!isEmpty(version)) return version


        //兼容HMCL
        val patches = head.optJSONArray("patches")
        return patches?.filterIsInstance<JSONObject>()?.firstOrNull { it.optString("id") == moduleName }
            ?.optString("version", null)
    }

    fun getFabricVersion(head: JSONObject): String? {
        val first = getModuleVersion(head, "fabric")
        return if (!isEmpty(first)) first else getModuleVersion(
            head,
            "net.fabricmc.loader.impl.launch.knot.KnotClient",
            "net.fabricmc:fabric-loader:"
        )
    }

    fun getLiteloaderVersion(head: JSONObject): String? {
        val first = getModuleVersion(head, "liteloader")
        return if (!isEmpty(first)) first else getModuleVersion(
            head,
            null,
            "com.mumfrey:liteloader:"
        )
    }

    fun getForgeVersion(head: JSONObject): String? {
        val first = getModuleVersion(head, "forge")
        if (!isEmpty(first)) return first
        val game: JSONArray? = head.optJSONObject("arguments")?.optJSONArray("game")
        if (game != null) {
            val indexOf = game.indexOf("--fml.forgeVersion")
            if (indexOf in 0..<game.length() - 1)
                return game.opt(indexOf + 1) as? String
        }
        var version: String? = null
        var second = getModuleVersion(head, null, "net.minecraftforge:forge:")
        if (isEmpty(second)) {
            second = getModuleVersion(head, null, "net.minecraftforge:fmlloader:")
        }
        if (!second.isNullOrBlank()) {
            val split = second.splitByRegex("-")
            if (split.size >= 2) {
                version = split[1]
            }
        }
        return version
    }

    fun getOptifineVersion(head: JSONObject): String? {
        val first = getModuleVersion(head, "optifine")
        if (!isEmpty(first)) return first
        var version: String? = null
        val origin = getModuleVersion(head, null, "optifine:OptiFine:")
        if (!origin.isNullOrBlank()) {
            val indexOf = origin.indexOf('_')
            version = origin.substring(indexOf + 1)
        }
        return version
    }

    fun getQuiltVersion(head: JSONObject): String? {
        val first = getModuleVersion(head, "quilt")
        return if (!isEmpty(first)) first else getModuleVersion(
            head,
            "org.quiltmc.loader.impl.launch.knot.KnotClient",
            "org.quiltmc:quilt-loader:"
        )
    }

    fun getNeoForgeVersion(head: JSONObject): String? {
        val first = getModuleVersion(head, "neoforge")
        if (!isEmpty(first)) return first
        val game: JSONArray? = head.optJSONObject("arguments")?.optJSONArray("game")
        if (game != null) {
            val indexOf = game.indexOf("--fml.neoForgeVersion")
            if (indexOf in 0..<game.length() - 1)
                return game.opt(indexOf + 1) as? String
        }
        return null
    }
}
