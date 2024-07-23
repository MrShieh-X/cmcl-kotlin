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

object DefaultApiProvider : DownloadApiProvider {
    override fun versionManifest(): String {
        return "https://piston-meta.mojang.com/mc/game/version_manifest.json"
    }

    override fun versionClient(): String {
        return "https://launcher.mojang.com/"
    }

    override fun versionAssetsIndex(): String {
        return "https://piston-meta.mojang.com/"
    }

    override fun assets(): String {
        return "https://resources.download.minecraft.net/"
    }

    override fun libraries(): String {
        return "https://libraries.minecraft.net/"
    }

    override fun versionJSON(): String {
        return "https://piston-meta.mojang.com/"
    }

    override fun authlibInjectorFile(): String {
        return "https://authlib-injector.yushi.moe/artifact/latest.json"
    }

    override fun fabricMeta(): String {
        return "https://meta.fabricmc.net/"
    }

    override fun fabricMaven(): String {
        return "https://maven.fabricmc.net/"
    }

    override fun forgeMaven(): String {
        return "https://files.minecraftforge.net/maven/"
    }

    override fun liteLoaderVersion(): String {
        return "http://dl.liteloader.com/versions/versions.json"
    }
}
