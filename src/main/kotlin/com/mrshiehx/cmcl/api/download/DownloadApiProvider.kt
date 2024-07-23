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

interface DownloadApiProvider {
    fun versionManifest(): String //https://piston-meta.mojang.com/mc/game/version_manifest.json

    fun authlibInjectorFile(): String //https://authlib-injector.yushi.moe/artifact/latest.json

    fun nide8AuthFile(): String {
        return "https://login.mc-user.com:233/download/nide8auth.jar"
    }

    fun assets(): String //https://resources.download.minecraft.net/

    fun versionClient(): String //https://launcher.mojang.com/

    fun versionAssetsIndex(): String //https://piston-meta.mojang.com/

    fun libraries(): String //https://libraries.minecraft.net/

    fun versionJSON(): String //https://piston-meta.mojang.com/

    fun fabricMeta(): String //https://meta.fabricmc.net/

    fun fabricMaven(): String //https://maven.fabricmc.net/

    fun forge(): String {
        return "https://bmclapi2.bangbang93.com/forge/"
    }

    fun forgeMaven(): String //https://files.minecraftforge.net/maven/ or https://maven.minecraftforge.net/

    fun thirdPartyForge(): String {
        return "https://bmclapi2.bangbang93.com/forge/download"
    } //https://bmclapi2.bangbang93.com/forge/download

    fun liteLoaderVersion(): String //http://dl.liteloader.com/versions/versions.json

    fun thirdPartyLiteLoaderDownload(): String {
        return "https://bmclapi2.bangbang93.com/liteloader/download"
    } //https://bmclapi2.bangbang93.com/liteloader/download

    fun thirdPartyOptiFine(): String {
        return "https://bmclapi2.bangbang93.com/optifine/"
    } //https://bmclapi2.bangbang93.com/optifine/

    fun quiltMeta(): String {
        return "https://meta.quiltmc.org/"
    }

    fun quiltMaven(): String {
        return "https://maven.quiltmc.org/repository/release/"
    }
}
