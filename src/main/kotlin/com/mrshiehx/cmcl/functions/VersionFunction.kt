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
package com.mrshiehx.cmcl.functions

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.bean.Library
import com.mrshiehx.cmcl.bean.arguments.*
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.exceptions.CommandTooLongException
import com.mrshiehx.cmcl.functions.mod.ModFunction
import com.mrshiehx.cmcl.functions.root.LaunchCommands
import com.mrshiehx.cmcl.modSources.modrinth.ModrinthModManager
import com.mrshiehx.cmcl.modules.MinecraftLauncher
import com.mrshiehx.cmcl.modules.extra.ExtraInstaller
import com.mrshiehx.cmcl.modules.version.PrintVersionInfo
import com.mrshiehx.cmcl.modules.version.VersionCompleter
import com.mrshiehx.cmcl.modules.version.downloaders.AssetsDownloader
import com.mrshiehx.cmcl.modules.version.downloaders.LibrariesDownloader
import com.mrshiehx.cmcl.modules.version.downloaders.NativesDownloader
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import com.mrshiehx.cmcl.utils.json.XJSONObject
import com.mrshiehx.cmcl.utils.system.SystemUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

object VersionFunction : Function {
    override val usageName = "version"
    override fun execute(arguments: Arguments) {
        //因为版本名可能有以-或--开头的，所以不检查
        /*ArgumentRequirement[] argumentRequirements = {
                ArgumentRequirement.ofSingle("info"),
                ArgumentRequirement.ofSingle("d"),
                ArgumentRequirement.ofSingle("delete"),
                ArgumentRequirement.ofValue("rename"),
                ArgumentRequirement.ofSingle("complete"),
                ArgumentRequirement.ofValue("complete"),
                ArgumentRequirement.ofValue("t"),
                ArgumentRequirement.ofValue("thread"),
                ArgumentRequirement.ofValue("config"),
                ArgumentRequirement.ofSingle("api"),
                ArgumentRequirement.ofSingle("fabric"),
                ArgumentRequirement.ofSingle("forge"),
                ArgumentRequirement.ofSingle("liteloader"),
                ArgumentRequirement.ofSingle("optifine"),
                ArgumentRequirement.ofSingle("quilt"),
                ArgumentRequirement.ofValue("fabric"),
                ArgumentRequirement.ofValue("forge"),
                ArgumentRequirement.ofValue("liteloader"),
                ArgumentRequirement.ofValue("optifine"),
                ArgumentRequirement.ofValue("quilt")
        };
        if (!Function.checkArgs(arguments, 2, 1, argumentRequirements)) return;*/
        val first = arguments.optArgument(1)
        val second = arguments.optArgument(2)
        if (first is TextArgument) {
            //hasVersion
            if (second is ValueArgument || second is SingleArgument) {
                operate(arguments, second, arguments.optArgument(3), first.key)
            } else {
                println(CMCL.getString("CONSOLE_ONLY_HELP"))
            }
        } else {
            val selectedVersion: String = Utils.getConfig().optString("selectedVersion")
            if (second == null) {
                if (Utils.isEmpty(selectedVersion)) {
                    println(CMCL.getString("MESSAGE_TO_SELECT_VERSION"))
                    return
                }
                operate(arguments, first, null, selectedVersion)
            } else {
                //cmcl version --forge 12 -a
                //cmcl version "-a" --forge 12
                if (VersionUtils.versionExists(first!!.originArray[0])) {
                    operate(arguments, second, arguments.optArgument(3), first.originArray[0])
                } else {
                    if (Utils.isEmpty(selectedVersion)) {
                        println(CMCL.getString("MESSAGE_TO_SELECT_VERSION"))
                        return
                    }
                    operate(arguments, first, second, selectedVersion)
                }
            }
        }
    }

    private fun operate(arguments: Arguments, operateArg: Argument?, operateArgNext: Argument?, versionName: String) {
        val versionDir = File(CMCL.versionsDir, versionName)
        val jsonFile = File(versionDir, "$versionName.json")
        val jarFile = File(versionDir, "$versionName.jar")
        if (!jsonFile.exists()) {
            println(CMCL.getString("EXCEPTION_VERSION_NOT_FOUND", versionName))
            return
        }
        when (operateArg) {
            is SingleArgument -> {
                val key = operateArg.key
                when (key) {
                    "info" -> PrintVersionInfo.execute(jsonFile, jarFile, versionDir, versionName)
                    "d", "delete" -> FileUtils.deleteDirectory(versionDir)
                    "complete" -> VersionCompleter.execute(jsonFile, jarFile, versionName)
                    "fabric" -> installFabric(jsonFile, jarFile, null, arguments.contains("api"), arguments.opt("api"))
                    "forge" -> installExtra(
                        jsonFile,
                        jarFile,
                        com.mrshiehx.cmcl.modules.extra.forge.ForgeInstaller,
                        null
                    )

                    "liteloader" -> installExtra(
                        jsonFile,
                        jarFile,
                        com.mrshiehx.cmcl.modules.extra.liteloader.LiteloaderInstaller,
                        null
                    )

                    "optifine" -> installExtra(
                        jsonFile,
                        jarFile,
                        com.mrshiehx.cmcl.modules.extra.optifine.OptiFineInstaller,
                        null
                    )

                    "quilt" -> installExtra(
                        jsonFile,
                        jarFile,
                        com.mrshiehx.cmcl.modules.extra.quilt.QuiltInstaller,
                        null
                    )

                    "isolate" -> {
                        val cfgFile = File(versionDir, "cmclversion.json")
                        val versionCfg: JSONObject = if (cfgFile.exists()) {
                            try {
                                JSONObject(FileUtils.readFileContent(cfgFile))
                            } catch (ignored: Throwable) {
                                JSONObject()
                            }
                        } else {
                            JSONObject()
                        }
                        versionCfg.put("isolate", true)
                        try {
                            FileUtils.writeFile(cfgFile, versionCfg.toString(), false)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    "unset-isolate" -> {
                        val cfgFile = File(versionDir, "cmclversion.json")
                        val versionCfg: JSONObject = if (cfgFile.exists()) {
                            try {
                                JSONObject(FileUtils.readFileContent(cfgFile))
                            } catch (ignored: Throwable) {
                                JSONObject()
                            }
                        } else {
                            JSONObject()
                        }
                        versionCfg.put("isolate", false)
                        try {
                            FileUtils.writeFile(cfgFile, versionCfg.toString(), false)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    "p", "print-command" -> LaunchCommands.print(versionName)
                    else -> println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", operateArg.originString))
                }
            }

            is ValueArgument -> {
                val key = operateArg.key
                val value = operateArg.value
                when (key) {
                    "rename" -> {
                        if (VersionUtils.versionExists(value)) {
                            println(CMCL.getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS", value))
                            return
                        }
                        try {
                            val head: JSONObject = XJSONObject(FileUtils.readFileContent(jsonFile))
                            head.put("id", value)
                            FileUtils.writeFile(jsonFile, head.toString(2), false)
                        } catch (e: Exception) {
                            println(CMCL.getString("MESSAGE_FAILED_RENAME_VERSION", e))
                            return
                        }
                        val newFile = File(CMCL.versionsDir, value)
                        val file2 = File(newFile, "$versionName.jar")
                        val file3 = File(newFile, "$versionName.json")
                        versionDir.renameTo(newFile)
                        file2.renameTo(File(newFile, "$value.jar"))
                        file3.renameTo(File(newFile, "$value.json"))
                    }

                    "complete" -> if ("assets".equals(value, ignoreCase = true)) {
                        try {
                            AssetsDownloader.start(
                                JSONObject(FileUtils.readFileContent(jsonFile)),
                                arguments.optInt("t", arguments.optInt("thread"))
                                    ?: Constants.DEFAULT_DOWNLOAD_THREAD_COUNT
                            ) { println(CMCL.getString("MESSAGE_INSTALL_DOWNLOADED_ASSETS")) }
                        } catch (e: Exception) {
                            println(e.message)
                        }
                    } else if ("libraries".equals(value, ignoreCase = true)) {
                        try {
                            val libraries: JSONArray =
                                JSONObject(FileUtils.readFileContent(jsonFile)).optJSONArray("libraries") ?: JSONArray()
                            val pair: Triple<List<Library>, List<Library>, Boolean> =
                                MinecraftLauncher.getLibraries(libraries)
                            val notFound: List<Library> = pair.second
                            if (notFound.isEmpty()) {
                                println(CMCL.getString("VERSION_COMPLETE_LIBRARIES_NO_NEED_TO"))
                                return
                            }
                            executeNotFound(notFound)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if ("natives".equals(value, ignoreCase = true)) {
                        try {
                            val libraries: JSONArray =
                                JSONObject(FileUtils.readFileContent(jsonFile)).optJSONArray("libraries") ?: JSONArray()
                            println(CMCL.getString("MESSAGE_INSTALL_DOWNLOADING_LIBRARIES"))
                            NativesDownloader.download(versionDir, JSONUtils.jsonArrayToJSONObjectList(libraries))
                            println(CMCL.getString("MESSAGE_REDOWNLOADED_NATIVES"))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        println(CMCL.getString("VERSION_UNKNOWN_COMPLETING", value))
                    }

                    "config" -> {
                        val cfgFile = File(versionDir, "cmclversion.json")
                        val versionCfg: JSONObject = if (cfgFile.exists()) {
                            try {
                                JSONObject(FileUtils.readFileContent(cfgFile))
                            } catch (ignored: Throwable) {
                                JSONObject()
                            }
                        } else {
                            JSONObject()
                        }
                        if (operateArgNext != null) versionCfg.put(
                            value,
                            operateArgNext.originArray[0]
                        ) else versionCfg.remove(value)
                        try {
                            FileUtils.writeFile(cfgFile, versionCfg.toString(), false)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    "fabric" -> installFabric(jsonFile, jarFile, value, arguments.contains("api"), arguments.opt("api"))
                    "forge" -> installExtra(
                        jsonFile,
                        jarFile,
                        com.mrshiehx.cmcl.modules.extra.forge.ForgeInstaller,
                        value
                    )

                    "liteloader" -> installExtra(
                        jsonFile,
                        jarFile,
                        com.mrshiehx.cmcl.modules.extra.liteloader.LiteloaderInstaller,
                        value
                    )

                    "optifine" -> installExtra(
                        jsonFile,
                        jarFile,
                        com.mrshiehx.cmcl.modules.extra.optifine.OptiFineInstaller,
                        value
                    )

                    "quilt" -> installExtra(
                        jsonFile,
                        jarFile,
                        com.mrshiehx.cmcl.modules.extra.quilt.QuiltInstaller,
                        value
                    )

                    "export-script" -> {
                        val scriptFilePs = File(value)
                        if (scriptFilePs.exists()) {
                            println(CMCL.getString("CONSOLE_FILE_EXISTS", value))
                            return
                        }
                        try {
                            (if (SystemUtils.isWindows) LaunchCommands.getBatContent(versionName) else LaunchCommands.getShContent(
                                versionName
                            ))?.let { FileUtils.writeFile(scriptFilePs, it, false) }
                        } catch (e: CommandTooLongException) {
                            println(CMCL.getString("MESSAGE_EXPORT_COMMAND_EXCEEDS_LENGTH_LIMIT"))
                        } catch (e: Exception) {
                            println(CMCL.getString("EXCEPTION_WRITE_FILE") + ": " + e)
                        }
                    }

                    "export-script-ps" -> {
                        val scriptFilePs = File(value)
                        if (scriptFilePs.exists()) {
                            println(CMCL.getString("CONSOLE_FILE_EXISTS", value))
                            return
                        }
                        try {
                            LaunchCommands.getPowerShellContent(versionName)
                                ?.let { FileUtils.writeFile(scriptFilePs, it, false) }
                        } catch (e: Exception) {
                            println(CMCL.getString("EXCEPTION_WRITE_FILE") + ": " + e)
                        }
                    }

                    else -> println(CMCL.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", operateArg.originString))
                }
            }

            else -> {
                println(CMCL.getString("CONSOLE_ONLY_HELP"))
            }
        }
    }

    private fun installFabric(
        jsonFile: File,
        jarFile: File,
        fabricVersion: String?,
        installApi: Boolean,
        fabricApiVersion: String?
    ): Boolean {
        val success: Boolean =
            com.mrshiehx.cmcl.modules.extra.fabric.FabricInstaller.install(jsonFile, jarFile, fabricVersion)
        if (success && installApi) {
            var ver: String? = null
            try {
                ver = VersionUtils.getGameVersion(
                    JSONObject(FileUtils.readFileContent(jsonFile)),
                    jarFile
                ).id?.replace(" Pre-Release ", "-pre")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val url = ModrinthModManager().getDownloadLink(
                "P7dR8mSH",
                "Fabric API",
                ver,
                fabricApiVersion,
                isModpack = false,
                ModFunction.MOD_MR_DEPENDENCY_INSTALLER
            )
            if (!Utils.isEmpty(url)) {
                ModFunction.downloadMod(url)
            }
        }
        return success
    }

    private fun installExtra(
        jsonFile: File,
        jarFile: File,
        extraInstaller: ExtraInstaller,
        extraVersion: String?
    ): Boolean {
        return extraInstaller.install(jsonFile, jarFile, extraVersion)
    }

    fun executeNotFound(notFound: List<Library>) {
        for (library in notFound) {
            print("    ")
            println(library.libraryJSONObject.optString("name")) //legal
        }
        if (InteractionUtils.yesOrNo(CMCL.getString("CONSOLE_LACK_LIBRARIES_WHETHER_DOWNLOAD"))) {
            LibrariesDownloader.downloadLibraries(notFound)
        }
    }

}
