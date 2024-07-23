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
import com.mrshiehx.cmcl.bean.RunningMinecraft
import com.mrshiehx.cmcl.bean.VersionConfig
import com.mrshiehx.cmcl.enums.GameCrashError
import com.mrshiehx.cmcl.exceptions.*
import com.mrshiehx.cmcl.functions.AccountFunction
import com.mrshiehx.cmcl.functions.VersionFunction
import com.mrshiehx.cmcl.modules.MinecraftLauncher
import com.mrshiehx.cmcl.modules.account.authentication.AccountRefresher
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.authlib.AuthlibInjectorInformation
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.nide8auth.Nide8AuthInformation
import com.mrshiehx.cmcl.utils.FileUtils.readFileContent
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.AccountUtils
import com.mrshiehx.cmcl.utils.cmcl.ArgumentsUtils
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import com.mrshiehx.cmcl.utils.system.SystemUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object VersionStarter {
    fun getVersionInfo(versionFolder: File?): VersionConfig {
        val versionInfoFile = File(versionFolder, "cmclversion.json")
        val versionInfoFileHMCL = File(versionFolder, "hmclversion.cfg")
        val versionInfoFilePCL2 = File(versionFolder, "PCL/Setup.ini")
        var cmclJO: JSONObject? = null
        var hmclJO: JSONObject? = null
        var pcl2File: String? = null
        if (versionInfoFile.exists()) {
            try {
                cmclJO = JSONUtils.parseJSONObject(readFileContent(versionInfoFile))
            } catch (ignored: Throwable) {
            }
        }
        if (versionInfoFileHMCL.exists()) {
            try {
                hmclJO = JSONUtils.parseJSONObject(readFileContent(versionInfoFileHMCL))
            } catch (ignored: Throwable) {
            }
        }
        if (versionInfoFilePCL2.exists()) {
            try {
                pcl2File = readFileContent(versionInfoFilePCL2)
            } catch (ignored: Throwable) {
            }
        }
        var cmcl: VersionConfig? = null
        var hmcl: VersionConfig? = null
        var pcl2: VersionConfig? = null
        if (cmclJO != null) {
            cmcl = VersionConfig.valueOf(cmclJO)
        }
        if (hmclJO != null) {
            hmcl = VersionConfig.valueOfHMCL(hmclJO)
        }
        if (pcl2File != null) {
            pcl2 = VersionConfig.valueOfPCL2(pcl2File)
        }
        var Final = VersionConfig.EMPTY
        if (cmcl != null && hmcl != null && pcl2 != null) {
            Final = pcl2.mergeTo(hmcl.mergeTo(cmcl))
        } else if (cmcl != null && hmcl != null) {
            Final = hmcl.mergeTo(cmcl)
        } else if (cmcl != null && pcl2 != null) {
            Final = pcl2.mergeTo(cmcl)
        } else if (hmcl != null && pcl2 != null) {
            Final = pcl2.mergeTo(hmcl)
        } else if (cmcl != null) {
            Final = cmcl
        } else if (hmcl != null) {
            Final = hmcl
        } else if (pcl2 != null) {
            Final = pcl2
        }
        return Final
    }

    fun execute(version: String?) {
        var version = version
        val config: JSONObject = Utils.getConfig()
        if (Utils.isEmpty(version)) {
            version = config.optString("selectedVersion")
            if (Utils.isEmpty(version)) {
                println(CMCL.getString("CONSOLE_NO_SELECTED_VERSION"))
                return
            }
        }
        if (!config.has("exitWithMinecraft")) {
            config.put("exitWithMinecraft", InteractionUtils.yesOrNo(CMCL.getString("CONSOLE_ASK_EXIT_WITH_MC")))
        }
        if (!config.has("printStartupInfo")) {
            config.put("printStartupInfo", InteractionUtils.yesOrNo(CMCL.getString("CONSOLE_ASK_PRINT_STARTUP_INFO")))
        }
        if (!config.has("checkAccountBeforeStart")) {
            config.put("checkAccountBeforeStart", InteractionUtils.yesOrNo(CMCL.getString("CONSOLE_ASK_CHECK_ACCOUNT")))
        }
        Utils.saveConfig(config)
        start(version, config)
    }

    fun start(version: String, config: JSONObject) {
        /*String javaPath = config.optString("javaPath", Utils.getDefaultJavaPath());
        if (isEmpty(javaPath) || !new File(javaPath).exists()) {
            System.out.println(getString("CONSOLE_INCORRECT_JAVA"));
        } else {*/
        val versionsFolder = File(CMCL.gameDir, "versions")
        val versionFolder = File(versionsFolder, version)
        val versionJarFile = File(versionFolder, "$version.jar")
        val versionJsonFile = File(versionFolder, "$version.json")
        try {
            val account = AccountUtils.getSelectedAccountIfNotLoginNow(config) ?: return
            val versionConfig = getVersionInfo(versionFolder)
            val workingDirectory =
                if (versionConfig.isolate == true) versionFolder else (if (!isEmpty(versionConfig.gameDir)) File(
                    versionConfig.gameDir
                ) else if (MinecraftLauncher.isModpack(versionFolder)) versionFolder else CMCL.gameDir)
            var javaPath: String?
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
                windowSizeWidth = viWindowSizeWidth.toInt()
                if (windowSizeWidth <= 0) {
                    windowSizeWidth = config.optInt("windowSizeWidth", 854)
                    println(CMCL.getString("WARNING_VCFG_WINDOW_SIZE_WIDTH_INCORRECT"))
                }
            }

            if (viWindowSizeHeight == null)
                windowSizeHeight = config.optInt("windowSizeHeight", 480)
            else {
                windowSizeHeight = viWindowSizeHeight.toInt()
                if (windowSizeHeight <= 0) {
                    windowSizeHeight = config.optInt("windowSizeHeight", 480)
                    println(CMCL.getString("WARNING_VCFG_WINDOW_SIZE_HEIGHT_INCORRECT"))
                }
            }
            val isFullscreen = versionConfig.isFullscreen ?: config.optBoolean("isFullscreen")
            val jvmArgs: List<String> =
                versionConfig.jvmArgs.ifEmpty { ArgumentsUtils.parseJVMArgs(config.optJSONArray("jvmArgs")) }
            val gameArgs: Map<String, String?> =
                versionConfig.gameArgs.ifEmpty { ArgumentsUtils.parseGameArgs(config.optJSONObject("gameArgs")) }
            val assetsDir = if (!isEmpty(versionConfig.assetsDir)) File(versionConfig.assetsDir) else CMCL.assetsDir
            val resourcePackDir =
                if (!isEmpty(versionConfig.resourcesDir)) File(versionConfig.resourcesDir) else CMCL.resourcePacksDir
            val exitWithMinecraft = versionConfig.exitWithMinecraft ?: config.optBoolean("exitWithMinecraft")
            val printStartupInfo = versionConfig.printStartupInfo ?: config.optBoolean("printStartupInfo")
            val checkAccountBeforeStart = versionConfig.checkAccountBeforeStart
                ?: config.optBoolean("checkAccountBeforeStart")
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
                return
            }
            if (printStartupInfo) {
                val sb = StringBuilder()
                sb.append(
                    CMCL.getString("MESSAGE_STARTUP_INFO_MAIN")
                        .replace("\${VERSION_NAME}", version)
                        .replace("\${REAL_VERSION_NAME}", getVersion(versionJsonFile, versionJarFile))
                        .replace("\${PLAYER_NAME}", account.optString("playerName", "XPlayer"))
                        .replace("\${ACCOUNT_TYPE}", AccountFunction.getAccountTypeWithInformation(account))
                        .replace("\${JAVA_PATH}", javaPath)
                        .replace("\${EXIT_WITH_MC}", exitWithMinecraft.toString())
                        .replace("\${FULLSCREEN}", isFullscreen.toString())
                        .replace("\${MAX_MEMORY}", maxMemory.toString() + "MB")
                        .replace("\${WIDTH}", windowSizeWidth.toString())
                        .replace("\${HEIGHT}", windowSizeHeight.toString())
                        .replace("\${CHECK_ACCOUNT_BEFORE_START}", checkAccountBeforeStart.toString())
                        .replace("\${GAME_DIR}", workingDirectory.absolutePath)
                ).append('\n')
                if (assetsDir != File(workingDirectory, "assets")) {
                    sb.append(
                        CMCL.getString("MESSAGE_STARTUP_INFO_ASSETS_DIR")
                            .replace("\${ASSETS_DIR}", assetsDir.absolutePath)
                    ).append('\n')
                }
                if (resourcePackDir != File(workingDirectory, "resourcepacks")) {
                    sb.append(
                        CMCL.getString("MESSAGE_STARTUP_INFO_RESOURCE_PACKS_DIR")
                            .replace("\${RESOURCE_PACKS_DIR}", resourcePackDir.absolutePath)
                    ).append('\n')
                }
                if (!Utils.isEmpty(quickPlayLogFilePath)) {
                    sb.append(
                        CMCL.getString("MESSAGE_STARTUP_INFO_QUICK_PLAY_LOG_FILE_PATH")
                            .replace("\${QUICK_PLAY_LOG_FILE_PATH}", quickPlayLogFilePath)
                    ).append('\n')
                }
                if (!Utils.isEmpty(quickPlaySaveName)) {
                    sb.append(
                        CMCL.getString("MESSAGE_STARTUP_INFO_QUICK_PLAY_SAVE_NAME")
                            .replace("\${QUICK_PLAY_SAVE_NAME}", quickPlaySaveName)
                    ).append('\n')
                }
                if (!Utils.isEmpty(quickPlayServerAddress)) {
                    sb.append(
                        CMCL.getString("MESSAGE_STARTUP_INFO_QUICK_PLAY_SERVER_ADDRESS")
                            .replace("\${QUICK_PLAY_SERVER_ADDRESS}", quickPlayServerAddress)
                    ).append('\n')
                }
                if (!Utils.isEmpty(quickPlayRealmsID)) {
                    sb.append(
                        CMCL.getString("MESSAGE_STARTUP_INFO_QUICK_PLAY_REALMS_ID")
                            .replace("\${QUICK_PLAY_REALMS_ID}", quickPlayRealmsID)
                    ).append('\n')
                }
                if (jvmArgs.isNotEmpty() || gameArgs.isNotEmpty()) {
                    sb.append(
                        CMCL.getString("MESSAGE_STARTUP_INFO_ARGS")
                            .replace("\${JVM_ARGS}", JSONArray(jvmArgs).toString(2))
                            .replace("\${GAME_ARGS}", JSONObject(gameArgs).toString(2))
                    )
                }
                println(sb)
            }
            if (checkAccountBeforeStart) {
                if (!checkAccount(account, config)) return
            }
            var accessToken: String = Utils.randomUUIDNoSymbol()
            var uuid: String = AccountUtils.getUUIDByName(account.optString("playerName", "XPlayer"))
            if (account.optInt("loginMethod") > 0) {
                accessToken = account.optString("accessToken", accessToken)
                uuid = account.optString("uuid", uuid)
            } //刷新完账号才获取有关信息
            CMCL.runningMc = RunningMinecraft(
                MinecraftLauncher.launchMinecraft(
                    versionFolder,
                    versionJarFile,
                    versionJsonFile,
                    workingDirectory,
                    assetsDir,
                    resourcePackDir,
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
                    jvmArgs,
                    gameArgs,
                    AuthlibInjectorInformation.valuesOf(account, accessToken, uuid, true),
                    if (account.optInt("loginMethod") == 3) Nide8AuthInformation.valueOf(account) else null,
                    quickPlayLogFilePath,
                    quickPlaySaveName,
                    quickPlayServerAddress,
                    quickPlayRealmsID
                ),
                exitWithMinecraft
            )
            val crashError = arrayOf<GameCrashError?>(null)
            Thread {
                val dis = BufferedReader(InputStreamReader(CMCL.runningMc!!.process.inputStream))
                var line: String? = null
                try {
                    while (dis.readLine()?.also { line = it } != null) {
                        println(line) //legal
                        if (line!!.contains("cannot be cast to class java.net.URLClassLoader")) crashError[0] =
                            GameCrashError.URLClassLoader //旧版本Minecraft的Java版本过高问题，报Exception in thread "main" java.lang.ClassCastException: class jdk.internal.loader.ClassLoaders$AppClassLoader cannot be cast to class java.net.URLClassLoader，因为在Java9对相关代码进行了修改，所以要用Java8及更旧
                        else if (line!!.contains("Failed to load a library. Possible solutions:")) crashError[0] =
                            GameCrashError.LWJGLFailedLoad
                        else if (line!!.contains("java.lang.OutOfMemoryError:") || line!!.contains("Too small maximum heap")) crashError[0] =
                            GameCrashError.MemoryTooSmall
                        else if (line!!.contains("Unrecognized option: ")) crashError[0] =
                            GameCrashError.JvmUnrecognizedOption
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
            /*new Thread(new Runnable() {
                    @Override
                    public void run() {*/
            try {
                CMCL.runningMc!!.process.waitFor()
                println(CMCL.getString("MESSAGE_FINISHED_GAME"))
                if (crashError[0] != null) println(
                    CMCL.getString(
                        "MESSAGE_GAME_CRASH_CAUSE_TIPS",
                        crashError[0]!!.cause
                    )
                )
            } catch (interruptedException: InterruptedException) {
                interruptedException.printStackTrace()
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

    fun checkAccount(account: JSONObject, config: JSONObject): Boolean = try {
        if (AccountRefresher.execute(account, config.optJSONArray("accounts") ?: JSONArray())) {
            Utils.saveConfig(config)
        }
        true
    } catch (e: ExceptionWithDescription) {
        e.print()
        println(CMCL.getString("MESSAGE_TELL_USER_CHECK_ACCOUNT_CAN_BE_OFF"))
        false
    }


    private fun getVersion(versionJsonFile: File, versionJarFile: File): String = try {
        Utils.valueOf(VersionUtils.getGameVersion(JSONObject(readFileContent(versionJsonFile)), versionJarFile).id)
    } catch (e: Exception) {
        "null"
    }

}
