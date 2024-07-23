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
import com.mrshiehx.cmcl.modSources.curseforge.CurseForgeManager
import com.mrshiehx.cmcl.modSources.curseforge.CurseForgeModpackManager
import com.mrshiehx.cmcl.modSources.modrinth.ModrinthManager
import com.mrshiehx.cmcl.modSources.modrinth.ModrinthModpackManager
import com.mrshiehx.cmcl.modules.modpack.CurseForgeModpackInstaller
import com.mrshiehx.cmcl.modules.modpack.MCBBSModpackInstaller
import com.mrshiehx.cmcl.modules.modpack.ModrinthModpackInstaller
import com.mrshiehx.cmcl.modules.modpack.MultiMCModpackInstaller
import com.mrshiehx.cmcl.utils.FileUtils.deleteDirectory
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.Utils.inputStream2String
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.console.PercentageTextProgress
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import com.mrshiehx.cmcl.utils.json.XJSONObject
import org.json.JSONObject
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*
import java.util.zip.ZipFile

object ModpackFunction : Function {
    override val usageName = "modpack"

    class NotValidModPackFormat(message: String?) : Exception(message)

    override fun execute(arguments: Arguments) {
        if (!Function.checkArgs(
                arguments, 2, 1,
                ArgumentRequirement.ofSingle("install"),
                ArgumentRequirement.ofSingle("info"),
                ArgumentRequirement.ofSingle("no-assets"),
                ArgumentRequirement.ofSingle("no-libraries"),
                ArgumentRequirement.ofSingle("no-natives"),
                ArgumentRequirement.ofSingle("k"),
                ArgumentRequirement.ofSingle("keep-file"),
                ArgumentRequirement.ofValue("file"),
                ArgumentRequirement.ofValue("storage"),
                ArgumentRequirement.ofValue("source"),
                ArgumentRequirement.ofValue("n"),
                ArgumentRequirement.ofValue("name"),
                ArgumentRequirement.ofValue("id"),
                ArgumentRequirement.ofValue("limit"),
                ArgumentRequirement.ofValue("t"),
                ArgumentRequirement.ofValue("thread"),
                ArgumentRequirement.ofValue("url"),
                ArgumentRequirement.ofValue("game-version"),
                ArgumentRequirement.ofValue("v"),
                ArgumentRequirement.ofValue("version")
            )
        ) return
        var count = 0
        if (arguments.contains("install")) count++
        if (arguments.contains("info")) count++
        if (arguments.contains("file")) count++
        if (arguments.contains("url")) count++
        if (count == 0) {
            println(CMCL.getString("MODPACK_CONTAINS_NOTHING"))
            return
        } else if (count > 1) {
            println(CMCL.getString("MODPACK_CONTAINS_TWO_OR_MORE"))
            return
        }
        var todo = -1
        if (arguments.contains("install")) todo = 0
        else if (arguments.contains("info")) todo = 1
        else if (arguments.contains("file")) todo = 2
        else if (arguments.contains("url")) todo = 3

        if (todo == 2) {
            val modpack = File(arguments.opt("file")!!)
            if (!modpack.exists() || modpack.isDirectory()) {
                println(CMCL.getString("FILE_NOT_FOUND_OR_IS_A_DIRECTORY"))
                return
            }
            var versionStorageName = arguments.opt("storage")
            if (Utils.isEmpty(versionStorageName)) versionStorageName = InteractionUtils.inputStringInFilter(
                CMCL.getString("MESSAGE_INPUT_VERSION_NAME"),
                CMCL.getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS")
            )
            { string -> !isEmpty(string) && !VersionUtils.versionExists(string) }
            if (Utils.isEmpty(versionStorageName)) return
            if (VersionUtils.versionExists(versionStorageName)) {
                println(CMCL.getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS", versionStorageName))
                return
            }
            val threadCount =
                arguments.optInt("t", arguments.optInt("thread")) ?: Constants.DEFAULT_DOWNLOAD_THREAD_COUNT
            val versionDir = File(CMCL.versionsDir, versionStorageName)
            try {
                ZipFile(modpack).use { zipFile ->
                    installModpack(
                        zipFile,
                        modpack,
                        versionDir,
                        true,
                        !arguments.contains("no-assets"),
                        !arguments.contains("no-natives"),
                        !arguments.contains("no-libraries"),
                        if (threadCount > 0) threadCount else Constants.DEFAULT_DOWNLOAD_THREAD_COUNT
                    )
                }
            } catch (e: Exception) {
                println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e))
                deleteDirectory(versionDir)
            }
            return
        } else if (todo == 3) {
            val url = arguments.opt("url")!!
            var versionStorageName = arguments.opt("storage")
            if (Utils.isEmpty(versionStorageName)) versionStorageName = InteractionUtils.inputStringInFilter(
                CMCL.getString("MESSAGE_INPUT_VERSION_NAME"),
                CMCL.getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS")
            )
            { string -> !isEmpty(string) && !VersionUtils.versionExists(string) }
            if (Utils.isEmpty(versionStorageName)) return
            if (VersionUtils.versionExists(versionStorageName)) {
                println(CMCL.getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS", versionStorageName))
                return
            }
            downloadModpackWithInstalling(versionStorageName, url, arguments, -1)
            return
        }
        var sourceStr = arguments.opt("source")
        if (Utils.isEmpty(sourceStr)) {
            val config = CMCL.config
            sourceStr = ModFunction.getModDownloadSource(config)
        }
        val source: Int = when (Objects.requireNonNull(sourceStr).lowercase(Locale.getDefault())) {
            "cf", "curseforge" -> 0
            "mr", "modrinth" -> 1
            else -> {
                println(getString("MOD_UNKNOWN_SOURCE", sourceStr))
                return
            }
        }
        val modNameInput = arguments.opt("n", arguments.opt("name"))
        val modIdInput = arguments.opt("id")
        if (!Utils.isEmpty(modNameInput) && !Utils.isEmpty(modIdInput)) {
            println(CMCL.getString("MOD_CONTAINS_BOTH_NAME_AND_ID"))
            return
        } else if (Utils.isEmpty(modNameInput) && Utils.isEmpty(modIdInput)) {
            println(CMCL.getString("MOD_CONTAINS_BOTH_NOT_NAME_AND_ID"))
            return
        }
        if (!Utils.isEmpty(modIdInput) && arguments.contains("limit")) {
            println(CMCL.getString("MOD_ID_LIMIT_COEXIST"))
            return
        }
        val limit = arguments.optInt("limit") ?: 50
        if (limit > 50 && source == 0) {
            println(CMCL.getString("MOD_SEARCH_LIMIT_GREATER_THAN_FIFTY"))
            return
        }
        if (source == 0) {
            val cf: CurseForgeManager = CurseForgeModpackManager()
            val mod = CurseForgeSearcher.search(cf, modNameInput, modIdInput, limit) ?: return
            val modpackName = mod.optString("name")
            if (todo == 0) {
                val modId = mod.optInt("id")
                val modpackDownloadLink = cf.getDownloadLink(
                    modId.toString(),
                    modpackName,
                    arguments.opt("game-version"),
                    arguments.opt("v", arguments.opt("version")),
                    isModpack = true,
                ) { _, _, _ ->/*don't know what to do*/ }
                if (Utils.isEmpty(modpackDownloadLink)) return
                var versionStorageName = arguments.opt("storage")
                if (Utils.isEmpty(versionStorageName)) versionStorageName = InteractionUtils.inputStringInFilter(
                    CMCL.getString("MESSAGE_INPUT_VERSION_NAME"),
                    CMCL.getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS")
                )
                { string -> !isEmpty(string) && !VersionUtils.versionExists(string) }
                if (Utils.isEmpty(versionStorageName)) return
                if (VersionUtils.versionExists(versionStorageName)) {
                    println(CMCL.getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS", versionStorageName))
                    return
                }
                downloadModpackWithInstalling(versionStorageName, modpackDownloadLink, arguments, source)
            } else if (todo == 1) {
                cf.printInformation(mod, modpackName)
            }
        } else {
            val mr: ModrinthManager = ModrinthModpackManager()
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
                    true
                ) { _, _, _ ->/*don't know what to do*/ }
                if (Utils.isEmpty(modDownloadLink)) return
                var versionStorageName = arguments.opt("storage")
                if (Utils.isEmpty(versionStorageName)) versionStorageName = InteractionUtils.inputStringInFilter(
                    CMCL.getString("MESSAGE_INPUT_VERSION_NAME"),
                    CMCL.getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS")
                )
                { string -> !isEmpty(string) && !VersionUtils.versionExists(string) }
                if (Utils.isEmpty(versionStorageName)) return
                if (VersionUtils.versionExists(versionStorageName)) {
                    println(CMCL.getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS", versionStorageName))
                    return
                }
                downloadModpackWithInstalling(versionStorageName, modDownloadLink, arguments, source)
            } else if (todo == 1) {
                mr.printInformation(mod, modByID, null, null)
            }
        }
    }

    private fun installModpack(
        zipFile: ZipFile,
        file: File,
        versionDir: File,
        keepFile: Boolean,
        installAssets: Boolean,
        installNatives: Boolean,
        installLibraries: Boolean,
        threadCount: Int
    ): Int {
        //MCBBS一定要放在 CurseForge 之前，否则 MCBBS 的会被误认为是 CurseForge
        try {
            return MCBBSModpackInstaller.tryToInstallMCBBSModpack(
                zipFile,
                file,
                versionDir,
                keepFile,
                installAssets,
                installNatives,
                installLibraries,
                threadCount
            )
        } catch (ignore: NotValidModPackFormat) {
        }
        try {
            return CurseForgeModpackInstaller.tryToInstallCurseForgeModpack(
                zipFile,
                file,
                versionDir,
                keepFile,
                installAssets,
                installNatives,
                installLibraries,
                threadCount
            )
        } catch (ignore: NotValidModPackFormat) {
        }
        try {
            return MultiMCModpackInstaller.tryToInstallMultiMCModpack(
                zipFile,
                file,
                versionDir,
                keepFile,
                installAssets,
                installNatives,
                installLibraries,
                threadCount
            )
        } catch (ignore: NotValidModPackFormat) {
        }
        try {
            return ModrinthModpackInstaller.tryToInstallModrinthModpack(
                zipFile,
                file,
                versionDir,
                keepFile,
                installAssets,
                installNatives,
                installLibraries,
                threadCount
            )
        } catch (ignore: NotValidModPackFormat) {
        }
        println(CMCL.getString("MESSAGE_INSTALL_MODPACK_UNKNOWN_TYPE"))
        return -1
    }

    private fun downloadModpackWithInstalling(
        versionName: String,
        modDownloadLink: String,
        arguments: Arguments,
        source: Int
    ) {
        var modpacks = File(CMCL.CMCLWorkingDirectory, "modpacks")
        modpacks.mkdirs()
        var fileName = try {
            URLDecoder.decode(modDownloadLink.substring(modDownloadLink.lastIndexOf('/') + 1), "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
        if (isEmpty(fileName)) fileName = System.currentTimeMillis().toString() + ".zip"
        var modpackFile = File(modpacks, fileName)
        if (modpackFile.exists()) {
            val file = ModFunction.askStorage(modpackFile, CMCL.getString("CF_BESEARCHED_MODPACK_ALC"))
            if (file != null) {
                modpackFile = file
                modpacks = file.getParentFile()
            } else {
                return
            }
        }
        try {
            print(CMCL.getString("MESSAGE_DOWNLOADING_FILE_TO", fileName, modpacks.absolutePath))
            DownloadUtils.downloadFile(modDownloadLink, modpackFile, PercentageTextProgress())
        } catch (e: Exception) {
            println(CMCL.getString("MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON", fileName, e))
            return
        }
        val threadCount = arguments.optInt("t", arguments.optInt("thread")) ?: Constants.DEFAULT_DOWNLOAD_THREAD_COUNT
        val versionDir = File(CMCL.versionsDir, versionName)
        try {
            ZipFile(modpackFile).use { zipFile ->
                if (source == 0 || source == 1) {
                    val entry = zipFile.getEntry(if (source == 0) "manifest.json" else "modrinth.index.json")
                    //FileUtils.inputStream2File(zipFile.getInputStream(entry),new File(versionDir,"modpack.json"));
                    val manifest: JSONObject = XJSONObject(inputStream2String(zipFile.getInputStream(entry)))
                    if (source == 0)
                        CurseForgeModpackInstaller.installCurseForgeModpack(
                            manifest,
                            zipFile,
                            modpackFile,
                            versionDir,
                            arguments.contains("k") || arguments.contains("keep-file"),
                            !arguments.contains("no-assets"),
                            !arguments.contains("no-natives"),
                            !arguments.contains("no-libraries"),
                            if (threadCount > 0) threadCount else Constants.DEFAULT_DOWNLOAD_THREAD_COUNT
                        )
                    else
                        ModrinthModpackInstaller.installModrinthModpack(
                            manifest,
                            zipFile,
                            modpackFile,
                            versionDir,
                            arguments.contains("k") || arguments.contains("keep-file"),
                            !arguments.contains("no-assets"),
                            !arguments.contains("no-natives"),
                            !arguments.contains("no-libraries"),
                            if (threadCount > 0) threadCount else Constants.DEFAULT_DOWNLOAD_THREAD_COUNT
                        )
                } else {
                    installModpack(
                        zipFile,
                        modpackFile,
                        versionDir,
                        true,
                        !arguments.contains("no-assets"),
                        !arguments.contains("no-natives"),
                        !arguments.contains("no-libraries"),
                        if (threadCount > 0) threadCount else Constants.DEFAULT_DOWNLOAD_THREAD_COUNT
                    )
                }
            }
        } catch (e: Exception) {
            println(CMCL.getString("EXCEPTION_INSTALL_MODPACK", e))
            deleteDirectory(versionDir)
        }
    }
}
