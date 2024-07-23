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
package com.mrshiehx.cmcl.modules.extra

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.modules.version.downloaders.LibrariesDownloader
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils
import com.mrshiehx.cmcl.utils.json.XJSONObject
import org.json.JSONObject
import java.io.File

abstract class ExtraInstaller {
    protected abstract val extraName: String
    protected abstract val extraMerger: ExtraMerger
    protected abstract fun checkInstallable(gameJSON: JSONObject): Boolean

    fun install(jsonFile: File, jarFile: File, extraVersion: String?): Boolean {
        val fileContent = try {
            FileUtils.readFileContent(jsonFile)
        } catch (e: Exception) {
            if (Constants.isDebug()) e.printStackTrace()
            println(Utils.getString("EXCEPTION_READ_FILE_WITH_PATH", jsonFile.getAbsoluteFile()))
            return false
        }
        val gameJSON = try {
            XJSONObject(fileContent)
        } catch (e: Exception) {
            if (Constants.isDebug()) e.printStackTrace()
            println(Utils.getString("EXCEPTION_PARSE_FILE_WITH_PATH", jsonFile.getAbsoluteFile()))
            return false
        }
        if (!checkInstallable(gameJSON)) return false
        val mcVersion = VersionUtils.getGameVersion(gameJSON, jarFile).id
        if (Utils.isEmpty(mcVersion)) {
            println(CMCL.getString("INSTALL_MODLOADER_EMPTY_MC_VERSION", extraName))
            return false
        }
        val pair = extraMerger.merge(mcVersion, gameJSON, jarFile, false, extraVersion)
        return if (pair.first) {
            val list: List<JSONObject>? = pair.second
            if (!list.isNullOrEmpty()) {
                for (library in list) {
                    LibrariesDownloader.downloadSingleLibrary(library)
                }
            }
            try {
                FileUtils.writeFile(jsonFile, gameJSON.toString(2), false)
                println(CMCL.getString("INSTALLED_MODLOADER", extraName))
                true
            } catch (e: Exception) {
                if (Constants.isDebug()) e.printStackTrace()
                println(
                    CMCL.getString(
                        "INSTALL_MODLOADER_FAILED_WITH_REASON",
                        extraName,
                        CMCL.getString("EXCEPTION_WRITE_FILE_WITH_PATH", jsonFile.absolutePath)
                    )
                )
                false
            }
        } else false
    }
}
