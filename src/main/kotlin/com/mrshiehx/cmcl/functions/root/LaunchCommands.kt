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
package com.mrshiehx.cmcl.functions.root

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.exceptions.*
import com.mrshiehx.cmcl.functions.VersionFunction
import com.mrshiehx.cmcl.modules.MinecraftLauncher
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.authlib.AuthlibInjectorInformation
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.nide8auth.Nide8AuthInformation
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.AccountUtils
import com.mrshiehx.cmcl.utils.cmcl.ArgumentsUtils
import com.mrshiehx.cmcl.utils.console.CommandUtils
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.system.SystemUtils
import org.json.JSONObject
import java.io.File
import java.io.IOException

object LaunchCommands {
    @Throws(CommandTooLongException::class)
    fun getBatContent(version: String): String? {
        try {
            val args = getArguments(version) ?: return null
            val versionsFolder = File(CMCL.gameDir, "versions")
            val versionFolder = File(versionsFolder, version)
            val stringBuilder = StringBuilder()
            val command = CommandUtils.argsToCommand(args)
            if (command.length > 8190) {
                throw CommandTooLongException()
            }
            val path = versionFolder.absolutePath
            stringBuilder.append("@echo off").append('\n')
            stringBuilder.append("cd /D ").append(if (path.contains(" ")) "\"" + path + "\"" else path).append('\n')
            stringBuilder.append(command).append('\n')
            stringBuilder.append("pause").append('\n')
            return stringBuilder.toString()
        } catch (e: CommandTooLongException) {
            throw e
        } catch (ex: EmptyNativesException) {
            println(CMCL.getString("EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND"))
        } catch (ex: LibraryDefectException) {
            VersionFunction.executeNotFound(ex.list)
        } catch (ex: LaunchException) {
            println(CMCL.getString("CONSOLE_FAILED_START") + ": " + ex.message)
        } catch (ex: Exception) {
            ex.printStackTrace()
            println(CMCL.getString("CONSOLE_FAILED_START") + ": " + ex)
        }
        return null
    }

    fun getShContent(version: String): String? {
        try {
            val args = getArguments(version) ?: return null
            val versionsFolder = File(CMCL.gameDir, "versions")
            val versionFolder = File(versionsFolder, version)
            val stringBuilder = StringBuilder()
            val command = CommandUtils.argsToCommand(args)
            val path = versionFolder.absolutePath
            stringBuilder.append("#!/usr/bin/env bash").append('\n')
            stringBuilder.append("cd ").append(if (path.contains(" ")) "\"" + path + "\"" else path).append('\n')
            stringBuilder.append(command).append('\n')
            return stringBuilder.toString()
        } catch (ex: EmptyNativesException) {
            println(CMCL.getString("EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND"))
        } catch (ex: LibraryDefectException) {
            VersionFunction.executeNotFound(ex.list)
        } catch (ex: LaunchException) {
            println(CMCL.getString("CONSOLE_FAILED_START") + ": " + ex.message)
        } catch (ex: Exception) {
            ex.printStackTrace()
            println(CMCL.getString("CONSOLE_FAILED_START") + ": " + ex)
        }
        return null
    }

    fun getPowerShellContent(version: String): String? {
        try {
            val args = getArguments(version) ?: return null
            val versionsFolder = File(CMCL.gameDir, "versions")
            val versionFolder = File(versionsFolder, version)
            val stringBuilder = StringBuilder()
            val path = versionFolder.absolutePath
            stringBuilder.append("Set-Location -Path ").append(CommandUtils.powershellString(path)).append('\n')
            stringBuilder.append('&')
            for (raw in args) {
                stringBuilder.append(' ')
                stringBuilder.append(CommandUtils.powershellString(raw))
            }
            stringBuilder.append('\n')
            return stringBuilder.toString()
        } catch (ex: EmptyNativesException) {
            println(CMCL.getString("EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND"))
        } catch (ex: LibraryDefectException) {
            VersionFunction.executeNotFound(ex.list)
        } catch (ex: LaunchException) {
            println(CMCL.getString("CONSOLE_FAILED_START") + ": " + ex.message)
        } catch (ex: Exception) {
            ex.printStackTrace()
            println(CMCL.getString("CONSOLE_FAILED_START") + ": " + ex)
        }
        return null
    }

    fun print(version: String) {
        try {
            val args = getArguments(version) ?: return
            val versionsFolder = File(CMCL.gameDir, "versions")
            val versionFolder = File(versionsFolder, version)
            val command = CommandUtils.argsToCommand(args)
            val path = versionFolder.absolutePath
            println(CMCL.getString("CONSOLE_START_COMMAND"))
            println("===================================================================================================================")
            println("cd " + (if (SystemUtils.isWindows) "/D " else "") + if (path.contains(" ")) "\"" + path + "\"" else path)
            println(command) //legal
            println("===================================================================================================================")
            if (SystemUtils.isWindows && command.length > 8190 /*不同情况下不一样*/) {
                println(CMCL.getString("MESSAGE_PRINT_COMMAND_EXCEEDS_LENGTH_LIMIT"))
            }
        } catch (ex: EmptyNativesException) {
            println(CMCL.getString("EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND"))
        } catch (ex: LibraryDefectException) {
            VersionFunction.executeNotFound(ex.list)
        } catch (ex: LaunchException) {
            println(CMCL.getString("CONSOLE_FAILED_START") + ": " + ex.message)
        } catch (ex: Exception) {
            ex.printStackTrace()
            println(CMCL.getString("CONSOLE_FAILED_START") + ": " + ex)
        }
    }

    @Throws(LaunchException::class, IOException::class)
    fun getArguments(version: String): List<String>? {
        var version = version
        val config: JSONObject = Utils.getConfig()
        if (Utils.isEmpty(version)) {
            version = config.optString("selectedVersion")
            if (Utils.isEmpty(version)) {
                println(CMCL.getString("MESSAGE_TO_SELECT_VERSION"))
                return null
            }
        }
        if (!config.has("checkAccountBeforeStart")) {
            config.put("checkAccountBeforeStart", InteractionUtils.yesOrNo(CMCL.getString("CONSOLE_ASK_CHECK_ACCOUNT")))
        }
        Utils.saveConfig(config)
        val versionsFolder = File(CMCL.gameDir, "versions")
        val versionFolder = File(versionsFolder, version)
        val versionJarFile = File(versionFolder, "$version.jar")
        val versionJsonFile = File(versionFolder, "$version.json")
        val account = AccountUtils.getSelectedAccountIfNotLoginNow(config) ?: return null
        if (!isEmpty(account.optString("offlineSkin")) || !isEmpty(account.optString("providedSkin"))) {
            println(CMCL.getString("PRINT_COMMAND_NOT_SUPPORT_OFFLINE_CUSTOM_SKIN"))
        }
        val versionConfig = VersionStarter.getVersionInfo(versionFolder)
        val workingDirectory =
            if (versionConfig.isolate == true) versionFolder
            else
                (if (!isEmpty(versionConfig.gameDir)) File(versionConfig.gameDir)
                else
                    if (MinecraftLauncher.isModpack(versionFolder)) versionFolder
                    else CMCL.gameDir)
        var javaPath: String
        var maxMemory: Int
        var windowSizeWidth: Int
        var windowSizeHeight: Int
        val viJavaPath = versionConfig.javaPath
        val viMaxMemory: Int? = versionConfig.maxMemory
        val viWindowSizeWidth: Int? = versionConfig.windowSizeWidth
        val viWindowSizeHeight: Int? = versionConfig.windowSizeHeight

        if (Utils.isEmpty(viJavaPath))
            javaPath = CMCL.javaPath
        else {
            try {
                javaPath = MinecraftLauncher.getRealJavaPath(viJavaPath)
            } catch (e: InvalidJavaException) {
                javaPath = CMCL.javaPath
                println(CMCL.getString("WARNING_VCFG_JAVA_INCORRECT"))
            }
        }

        if (viMaxMemory == null)
            maxMemory = config.optInt("maxMemory", SystemUtils.defaultMemory.toInt())
        else {
            maxMemory = viMaxMemory
            if (maxMemory <= 0) {
                maxMemory = config.optInt("maxMemory", SystemUtils.defaultMemory.toInt())
                println(CMCL.getString("WARNING_VCFG_MAX_MEMORY_INCORRECT"))
            }
        }

        if (viWindowSizeWidth == null)
            windowSizeWidth = config.optInt("windowSizeWidth", 854)
        else {
            windowSizeWidth = viWindowSizeWidth
            if (windowSizeWidth <= 0) {
                windowSizeWidth = config.optInt("windowSizeWidth", 854)
                println(CMCL.getString("WARNING_VCFG_WINDOW_SIZE_WIDTH_INCORRECT"))
            }
        }

        if (viWindowSizeHeight == null)
            windowSizeHeight = config.optInt("windowSizeHeight", 480)
        else {
            windowSizeHeight = viWindowSizeHeight
            if (windowSizeHeight <= 0) {
                windowSizeHeight = config.optInt("windowSizeHeight", 480)
                println(CMCL.getString("WARNING_VCFG_WINDOW_SIZE_HEIGHT_INCORRECT"))
            }
        }

        val checkAccountBeforeStart =
            versionConfig.checkAccountBeforeStart ?: config.optBoolean("checkAccountBeforeStart")
        val isFullscreen = versionConfig.isFullscreen ?: config.optBoolean("isFullscreen")
        val jvmArgs = ArgumentsUtils.parseJVMArgs(config.optJSONArray("jvmArgs")).toMutableList()
        jvmArgs.addAll(versionConfig.jvmArgs)
        val gameArgs = ArgumentsUtils.parseGameArgs(config.optJSONObject("gameArgs")).toMutableMap()
        gameArgs.putAll(versionConfig.gameArgs)
        val assetsDir = if (!isEmpty(versionConfig.assetsDir)) File(versionConfig.assetsDir) else CMCL.assetsDir
        val resourcesDir =
            if (!isEmpty(versionConfig.resourcesDir)) File(versionConfig.resourcesDir) else CMCL.resourcePacksDir
        val quickPlayLogFilePath =
            if (!isEmpty(versionConfig.qpLogFile)) versionConfig.qpLogFile else config.optString("qpLogFile")
        val quickPlaySaveName =
            if (!isEmpty(versionConfig.qpSaveName)) versionConfig.qpSaveName else config.optString("qpSaveName")
        val quickPlayServerAddress =
            if (!isEmpty(versionConfig.qpServerAddress)) versionConfig.qpServerAddress else config.optString("qpServerAddress")
        val quickPlayRealmsID =
            if (!isEmpty(versionConfig.qpRealmsID)) versionConfig.qpRealmsID else config.optString("qpRealmsID")
        if (Utils.isEmpty(javaPath) || !File(javaPath).exists()) {
            println(CMCL.getString("CONSOLE_INCORRECT_JAVA"))
            return null
        }
        if (checkAccountBeforeStart) {
            if (!VersionStarter.checkAccount(account, config)) return null
        }
        var accessToken: String = Utils.randomUUIDNoSymbol()
        var uuid: String = AccountUtils.getUUIDByName(account.optString("playerName", "XPlayer"))
        if (account.optInt("loginMethod") > 0) {
            accessToken = account.optString("accessToken", accessToken)
            uuid = account.optString("uuid", uuid)
        } //刷新完账号才获取有关信息
        return MinecraftLauncher.getMinecraftLaunchCommandArguments(
            versionJarFile,
            versionJsonFile,
            workingDirectory,
            assetsDir,
            resourcesDir,
            account.optString("playerName", "XPlayer"),
            javaPath,
            maxMemory.toLong(),
            128,
            windowSizeWidth,
            windowSizeHeight,
            isFullscreen,
            accessToken,
            uuid,
            false,
            !isFullscreen,
            account.optJSONObject("properties"),
            null,
            jvmArgs,
            gameArgs,
            AuthlibInjectorInformation.valuesOf(account, accessToken, uuid, false),
            if (account.optInt("loginMethod") == 3) Nide8AuthInformation.valueOf(account) else null,
            quickPlayLogFilePath,
            quickPlaySaveName,
            quickPlayServerAddress,
            quickPlayRealmsID
        )
    }
}
