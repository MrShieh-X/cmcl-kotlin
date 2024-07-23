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
package com.mrshiehx.cmcl.utils.cmcl

import java.util.jar.Attributes
import java.util.jar.JarFile
import java.util.jar.Manifest

object ManifestUtils {
    private val ATTRIBUTES: Attributes? by lazy {
        try {
            val resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME)
            while (resEnum.hasMoreElements()) {
                val url = resEnum.nextElement()
                val `is` = url.openStream()
                if (`is` != null) {
                    val manifest = Manifest(`is`)
                    manifest.mainAttributes
                }
            }
        } catch (ignore: Throwable) {
        }
        null
    }

    fun getString(name: String): String? {
        return try {
            ATTRIBUTES?.getValue(name)
        } catch (e: Exception) {
            null
        }
    }
}
