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
package com.mrshiehx.cmcl.modules.extra.quilt

import com.mrshiehx.cmcl.api.download.DownloadSource
import com.mrshiehx.cmcl.modules.extra.fabric.AbstractFabricMerger

object QuiltMerger : AbstractFabricMerger() {
    override val modLoaderName = "Quilt"
    override val mavenUrl = DownloadSource.getProvider().quiltMaven()
    override val metaUrl = DownloadSource.getProvider().quiltMeta() + "v3/"
    override val isQuilt = true
    override val storageName = "quilt"
}
