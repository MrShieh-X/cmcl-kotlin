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

object BMCLApiProvider : DownloadApiProvider {
    override fun versionManifest(): String {
        return "https://bmclapi2.bangbang93.com/mc/game/version_manifest.json"
    }

    override fun versionClient(): String {
        return "https://bmclapi2.bangbang93.com/"
    }

    override fun versionAssetsIndex(): String {
        return "https://bmclapi2.bangbang93.com/"
    }

    override fun assets(): String {
        return "https://bmclapi2.bangbang93.com/assets/"
    }

    override fun libraries(): String {
        return "https://bmclapi2.bangbang93.com/maven/"
    }

    override fun versionJSON(): String {
        return "https://bmclapi2.bangbang93.com/"
    }

    override fun authlibInjectorFile(): String {
        return "https://bmclapi2.bangbang93.com/mirrors/authlib-injector/artifact/latest.json"
    }

    override fun fabricMeta(): String {
        return "https://bmclapi2.bangbang93.com/fabric-meta/"
    }

    override fun fabricMaven(): String {
        return "https://bmclapi2.bangbang93.com/maven/"
    }

    override fun forgeMaven(): String {
        return "https://bmclapi2.bangbang93.com/maven/"
    }

    override fun liteLoaderVersion(): String {
        return "https://bmclapi.bangbang93.com/maven/com/mumfrey/liteloader/versions.json"
    }
}
