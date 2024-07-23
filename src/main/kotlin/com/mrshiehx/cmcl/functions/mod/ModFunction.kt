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
package com.mrshiehx.cmcl.functions.mod

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.bean.arguments.ArgumentRequirement
import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.functions.Function
import com.mrshiehx.cmcl.modSources.Manager
import com.mrshiehx.cmcl.modSources.curseforge.CurseForgeManager
import com.mrshiehx.cmcl.modSources.curseforge.CurseForgeModManager
import com.mrshiehx.cmcl.modSources.modrinth.ModrinthManager
import com.mrshiehx.cmcl.modSources.modrinth.ModrinthModManager
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.console.PercentageTextProgress
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import org.json.JSONObject
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*

object ModFunction : Function {
    override val usageName = "mod"
    val MOD_CF_DEPENDENCY_INSTALLER: Manager.DependencyInstaller = object : Manager.DependencyInstaller {
        override fun install(mcVersion: String, name: String?, id: String) {
            downloadMod(CurseForgeModManager().getDownloadLink(id, name, mcVersion, null, isModpack = false, this))
        }
    }

    val MOD_MR_DEPENDENCY_INSTALLER: Manager.DependencyInstaller = object : Manager.DependencyInstaller {
        override fun install(mcVersion: String, name: String?, id: String) {
            downloadMod(ModrinthModManager().getDownloadLink(id, name, mcVersion, null, isModpack = false, this))
        }
    }

    override fun execute(arguments: Arguments) {
        if (!Function.checkArgs(
                arguments,
                2,
                1,
                ArgumentRequirement.ofSingle("install"),
                ArgumentRequirement.ofSingle("info"),
                ArgumentRequirement.ofValue("source"),
                ArgumentRequirement.ofValue("n"),
                ArgumentRequirement.ofValue("name"),
                ArgumentRequirement.ofValue("id"),
                ArgumentRequirement.ofValue("limit"),
                ArgumentRequirement.ofValue("url"),
                ArgumentRequirement.ofValue("game-version"),
                ArgumentRequirement.ofValue("v"),
                ArgumentRequirement.ofValue("version")
            )
        ) return
        var count = 0
        if (arguments.contains("install")) count++
        if (arguments.contains("info")) count++
        if (arguments.contains("url")) count++
        if (count == 0) {
            println(getString("MOD_CONTAINS_NOTHING"))
            return
        } else if (count > 1) {
            println(getString("MOD_CONTAINS_TWO_OR_MORE"))
            return
        }
        var todo = -1
        if (arguments.contains("install")) todo = 0
        else if (arguments.contains("info")) todo = 1
        else if (arguments.contains("url")) todo = 3
        if (todo == 3) {
            downloadMod(arguments.opt("url"))
            return
        }
        var sourceStr = arguments.opt("source")
        if (Utils.isEmpty(sourceStr)) {
            val config = CMCL.config
            sourceStr = getModDownloadSource(config)
        }
        val source = when (Objects.requireNonNull(sourceStr).lowercase(Locale.getDefault())) {
            "cf", "curseforge" -> 0
            "mr", "modrinth" -> 1
            else -> {
                println(Utils.getString("MOD_UNKNOWN_SOURCE", sourceStr))
                return
            }
        }
        val modNameInput = arguments.opt("n", arguments.opt("name"))
        val modIdInput = arguments.opt("id")
        if (!Utils.isEmpty(modNameInput) && !Utils.isEmpty(modIdInput)) {
            println(getString("MOD_CONTAINS_BOTH_NAME_AND_ID"))
            return
        } else if (Utils.isEmpty(modNameInput) && Utils.isEmpty(modIdInput)) {
            println(getString("MOD_CONTAINS_BOTH_NOT_NAME_AND_ID"))
            return
        }
        if (!Utils.isEmpty(modIdInput) && arguments.contains("limit")) {
            println(getString("MOD_ID_LIMIT_COEXIST"))
            return
        }
        val limit = arguments.optInt("limit") ?: 50
        if (limit > 50 && source == 0) {
            println(getString("MOD_SEARCH_LIMIT_GREATER_THAN_FIFTY"))
            return
        }
        if (source == 0) {
            val cf: CurseForgeManager = CurseForgeModManager()
            val mod = CurseForgeSearcher.search(cf, modNameInput, modIdInput, limit) ?: return
            val modName = mod.optString("name")
            if (todo == 0) {
                val modId = mod.optInt("id")
                val modDownloadLink = cf.getDownloadLink(
                    modId.toString(),
                    modName,
                    arguments.opt("game-version"),
                    arguments.opt("v", arguments.opt("version")),
                    isModpack = false,
                    MOD_CF_DEPENDENCY_INSTALLER
                )
                if (Utils.isEmpty(modDownloadLink)) return
                downloadMod(modDownloadLink)
            } else if (todo == 1) {
                cf.printInformation(mod, modName)
            }
        } else {
            val mr: ModrinthManager = ModrinthModManager()
            val result = ModrinthSearcher.search(mr, modNameInput, modIdInput, limit) ?: return
            val mod = result.mod
            val modByID = result.modByID
            val modName = result.modName
            val modID = result.modID
            if (todo == 0) {
                val modDownloadLink = mr.getDownloadLink(
                    modID,
                    modName,
                    arguments.opt("game-version"),
                    arguments.opt("v", arguments.opt("version")),
                    isModpack = false,
                    MOD_MR_DEPENDENCY_INSTALLER
                )
                if (Utils.isEmpty(modDownloadLink)) return
                downloadMod(modDownloadLink)
            } else if (todo == 1) {
                mr.printInformation(mod, modByID, null, null)
            }
        }
    }

    fun askStorage(last: File, `$NAME`: String): File? {
        //0覆盖，返回last
        //1其他路径，返回新路径
        //2取消下载，返回null
        println(getString("CF_STORAGE_FILE_EXISTS_OPERATIONS"))
        val sel =
            InteractionUtils.inputInt(getString("CF_STORAGE_FILE_EXISTS_SELECT_OPERATION", last.absolutePath), 0, 2)
        return when (sel) {
            0 -> last
            1 -> askStorageForNewPath(last, `$NAME`)
            2 -> null
            else -> null
        }
    }

    private fun askStorageForNewPath(last: File, `$NAME`: String): File? {
        print(getString("CF_STORAGE_FILE_EXISTS").replace("\${NAME}", `$NAME`))
        val path: String = try {
            Scanner(System.`in`).nextLine()
        } catch (e: NoSuchElementException) {
            return null
        }
        if (Utils.isEmpty(path)) return askStorageForNewPath(last, `$NAME`)
        val file = File(path, last.getName())
        return if (!file.exists()) file else askStorage(file, `$NAME`)
    }

    fun downloadMod(modDownloadLink: String?) {
        if (Utils.isBlank(modDownloadLink)) return
        var mods = File(CMCL.gameDir, "mods")
        mods.mkdirs()
        var fileName = try {
            URLDecoder.decode(modDownloadLink.substring(modDownloadLink.lastIndexOf('/') + 1), "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
        if (Utils.isEmpty(fileName)) fileName = System.currentTimeMillis().toString() + ".jar"
        var modFile = File(mods, fileName)
        if (modFile.exists()) {
            val file = askStorage(modFile, getString("CF_BESEARCHED_MOD_ALC"))
            if (file != null) {
                modFile = file
                mods = file.getParentFile()
            } else {
                return
            }
        }
        try {
            print(getString("MESSAGE_DOWNLOADING_FILE_TO", fileName, mods.absolutePath))
            DownloadUtils.downloadFile(modDownloadLink, modFile, PercentageTextProgress())
        } catch (e: Exception) {
            if (Constants.isDebug()) e.printStackTrace()
            //Utils.downloadFileFailed(modDownloadLink, modFile, e);
            println(getString("MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON", fileName, e))
        }
    }

    fun getModDownloadSource(config: JSONObject): String {
        val sourceStr = config.optString("modDownloadSource")
        if (!sourceStr.equals("curseforge", ignoreCase = true) && !sourceStr.equals(
                "modrinth",
                ignoreCase = true
            ) && !sourceStr.equals("cf", ignoreCase = true) && !sourceStr.equals("mr", ignoreCase = true)
        ) {
            val sources: MutableList<Pair<String, Int>> = ArrayList(2)
            sources.add(0, Pair("CurseForge", 0))
            sources.add(1, Pair("Modrinth", 1))
            for (pair in sources) {
                System.out.printf("[%d]%s\n", pair.second, pair.first)
            }
            val defaultDownloadSource = 0
            var value = 0
            print(Utils.getString("CONSOLE_CHOOSE_DOWNLOAD_SOURCE_CF_OR_MR", defaultDownloadSource))
            try {
                value = Scanner(System.`in`).nextLine().toInt()
            } catch (ignore: NumberFormatException) {
            } catch (ignore: NoSuchElementException) {
            }
            val mds: String = if (value == 1) "modrinth" else "curseforge"
            config.put("modDownloadSource", mds)
            Utils.saveConfig(config)
        }
        return config.optString("modDownloadSource")
    }
}
