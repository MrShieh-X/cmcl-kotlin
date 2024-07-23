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
package com.mrshiehx.cmcl

import com.mrshiehx.cmcl.CMCL.runningMc
import com.mrshiehx.cmcl.bean.RunningMinecraft
import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.bean.arguments.SingleArgument
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.constants.Constants.isDebug
import com.mrshiehx.cmcl.constants.languages.LanguageEnum
import com.mrshiehx.cmcl.constants.languages.LanguageEnum.Companion.overriddenValueOf
import com.mrshiehx.cmcl.functions.Functions
import com.mrshiehx.cmcl.functions.root.RootFunction
import com.mrshiehx.cmcl.functions.root.VersionStarter
import com.mrshiehx.cmcl.utils.FileUtils.createFile
import com.mrshiehx.cmcl.utils.FileUtils.readFileContent
import com.mrshiehx.cmcl.utils.FileUtils.writeFile
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getConfig
import com.mrshiehx.cmcl.utils.Utils.isBlank
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.AccountUtils
import com.mrshiehx.cmcl.utils.console.CommandUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.system.JavaUtils
import com.mrshiehx.cmcl.utils.system.OperatingSystem
import com.mrshiehx.cmcl.utils.system.SystemUtils
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    //通过按 Ctrl+C 或关闭命令行窗口等退出CMCL时顺便退出游戏
    Runtime.getRuntime().addShutdownHook(Thread {
        if (runningMc != null && runningMc!!.exitWithMinecraft) {
            if (runningMc!!.process.isAlive) {
                runningMc!!.process.destroy()
            }
        }
    })
    CMCL.main(args.asList(), true)
}

object CMCL {
    var runningMc: RunningMinecraft? = null

    private var configJSONObject by Delegates.notNull<JSONObject>()

    var javaPath: String by Delegates.notNull()

    var gameDir: File by Delegates.notNull()
    var assetsDir: File by Delegates.notNull()
    var resourcePacksDir: File by Delegates.notNull()
    var versionsDir: File by Delegates.notNull()
    var librariesDir: File by Delegates.notNull()
    var launcherProfiles: File by Delegates.notNull()

    var isImmersiveMode = false

    init {
        initConfig()
    }

    fun main(args: List<String>, judgeSimplifiedCommand: Boolean) {
        if (args.isEmpty()) {
            val jsonObject: JSONObject = Utils.getConfig()
            val version = jsonObject.optString("selectedVersion")
            if (version.isNotEmpty()) {
                VersionStarter.start(version, jsonObject)
            } else {
                RootFunction.printHelp()
            }
        } else {
            val first = args[0]
            var function = Functions[first]
            val arguments = Arguments(args, true)
            if (function != null) {
                val second = arguments.optArgument(1)
                if (second == null) {
                    println(getHelpDocumentation(function.usageName))
                    return
                }
                if ("h" == second.key || "help" == second.key) {
                    if (second is SingleArgument) {
                        val name = function.usageName
                        if (!isEmpty(name)) {
                            println(getHelpDocumentation(name))
                        } else {
                            RootFunction.printHelp()
                        }
                    } else {
                        println(getString("CONSOLE_HELP_WRONG_WRITE", second.originString))
                    }
                } else {
                    function.execute(arguments)
                }
            } else {
                function = RootFunction
                if (judgeSimplifiedCommand) {
                    val originCommand: String? = getConfig().optJSONObject("simplifyCommands")?.optString(first)
                    if (!isBlank(originCommand)) {
                        main(CommandUtils.splitCommand(CommandUtils.clearRedundantSpaces(originCommand)), false)
                    } else {
                        function.execute(arguments)
                    }
                } else {
                    function.execute(arguments)
                }
            }
        }
    }

    fun getHelpDocumentation(name: String?): String {
        val helpDocumentation = language.helpMap[name]
        return if (!isEmpty(helpDocumentation)) helpDocumentation else ""
    }

    fun initConfig(): JSONObject {
        val configFile = configFile
        if (configFile.exists()) {
            val configFileContent: String = try {
                readFileContent(configFile)
            } catch (e: Exception) {
                e.printStackTrace()
                println(
                    String.format(
                        overriddenValueOf(Locale.getDefault().language).textMap["EXCEPTION_READ_CONFIG_FILE"]
                            ?: "Failed to read the configuration file, please make sure the configuration file (cmcl.json) is readable and the content is correct: %s",
                        e
                    )
                )
                exitProcess(1)
            }
            configJSONObject = if (isEmpty(configFileContent)) {
                JSONObject()
            } else {
                try {
                    JSONObject(configFileContent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    println(
                        String.format(
                            overriddenValueOf(Locale.getDefault().language).textMap["EXCEPTION_READ_CONFIG_FILE"]
                                ?: "Failed to read the configuration file, please make sure the configuration file (cmcl.json) is readable and the content is correct: %s",
                            e
                        )
                    )
                    exitProcess(1)
                }
            }
            val accounts = configJSONObject.optJSONArray("accounts")
            if (accounts != null && accounts.length() > 0) {
                var alreadyHave = false
                for (account in accounts) {
                    if (account !is JSONObject) continue
                    if (!account.optBoolean("selected")) continue
                    if (alreadyHave) {
                        account.put("selected", false)
                    } else {
                        alreadyHave = true
                    }
                }
                for (i in accounts.length() - 1 downTo 0) {
                    if (!AccountUtils.isValidAccount(accounts.opt(i))) {
                        accounts.remove(i)
                    }
                }
                Utils.saveConfig(configJSONObject)
            }
            javaPath = configJSONObject.optString("javaPath", JavaUtils.defaultJavaPath)
            gameDir =
                File(if (!isBlank(configJSONObject.optString("gameDir"))) configJSONObject.optString("gameDir") else ".minecraft")
            assetsDir =
                if (!isBlank(configJSONObject.optString("assetsDir"))) File(configJSONObject.optString("assetsDir")) else File(
                    gameDir,
                    "assets"
                )
            resourcePacksDir =
                if (!isBlank(configJSONObject.optString("resourcesDir"))) File(configJSONObject.optString("resourcesDir")) else File(
                    gameDir,
                    "resourcepacks"
                )
        } else {
            initDefaultDirs()
            configJSONObject = JSONObject()
            configJSONObject.put("language", Locale.getDefault().language)
            configJSONObject.put("javaPath", JavaUtils.defaultJavaPath.also { javaPath = it })
            configJSONObject.put("maxMemory", SystemUtils.defaultMemory)
            configJSONObject.put("windowSizeWidth", 854)
            configJSONObject.put("windowSizeHeight", 480)
            try {
                createFile(configFile, false)
                val writer = FileWriter(configFile, false)
                writer.write(configJSONObject.toString(Constants.INDENT_FACTOR))
                writer.close()
            } catch (e: IOException) {
                if (isDebug()) e.printStackTrace()
                e.printStackTrace()
            }
        }
        initChangelessDirs()
        initProxyIfEnabled(configJSONObject)
        return configJSONObject
    }

    private fun initChangelessDirs() {
        versionsDir = File(gameDir, "versions")
        librariesDir = File(gameDir, "libraries")
        launcherProfiles = File(gameDir, "launcher_profiles.json")
    }

    private fun initDefaultDirs() {
        gameDir = File(".minecraft")
        assetsDir = File(gameDir, "assets")
        resourcePacksDir = File(gameDir, "resourcepacks")
    }

    fun createLauncherProfiles() {
        if (launcherProfiles.exists()) return
        try {
            launcherProfiles.createNewFile()
            writeFile(
                launcherProfiles,
                "{\"selectedProfile\": \"(Default)\",\"profiles\": {\"(Default)\": {\"name\": \"(Default)\"}},\"clientToken\": \"88888888-8888-8888-8888-888888888888\"}",
                false
            )
        } catch (ignore: Exception) {
        }
    }

    private fun initProxyIfEnabled(configContent: JSONObject) {
        val proxyEnabled = configContent.optBoolean("proxyEnabled")
        val proxyHost = configContent.optString("proxyHost")
        val proxyPort = configContent.optString("proxyPort")
        if (!proxyEnabled || isBlank(proxyHost) || isBlank(proxyPort)) return
        NetworkUtils.setProxy(
            proxyHost,
            proxyPort,
            configContent.optString("proxyUsername"),
            configContent.optString("proxyPassword")
        )
    }

    val config: JSONObject
        get() = configJSONObject

    val configFile: File
        get() {
            if (isDebug()) return Constants.DEFAULT_CONFIG_FILE
            if (OperatingSystem.CURRENT_OS == OperatingSystem.LINUX || OperatingSystem.CURRENT_OS == OperatingSystem.OSX) {
                val inConfigDir = File(System.getProperty("user.home"), ".config/cmcl/cmcl.json")
                if (inConfigDir.exists()) {
                    return inConfigDir
                }
                return if (Constants.DEFAULT_CONFIG_FILE.exists()) {
                    Constants.DEFAULT_CONFIG_FILE
                } else inConfigDir
            }
            if (Constants.DEFAULT_CONFIG_FILE.exists() && Constants.DEFAULT_CONFIG_FILE.canWrite()) return Constants.DEFAULT_CONFIG_FILE
            val executableFilePath: String = Utils.getExecutableFilePath()
            val executableFile = File(if (!Utils.isBlank(executableFilePath)) executableFilePath else ".")
            return File(
                if (executableFile.isDirectory()) executableFile else executableFile.getParentFile()
                    ?: File("."), "cmcl.json"
            )
        }

    fun saveConfig(jsonObject: JSONObject) {
        configJSONObject = jsonObject
        val configFile = configFile
        try {
            if (!configFile.exists()) createFile(configFile, false)
            val writer = FileWriter(configFile, false)
            writer.write(jsonObject.toString(Constants.INDENT_FACTOR))
            writer.close()
        } catch (e: IOException) {
            if (isDebug()) e.printStackTrace()
            println(getString("EXCEPTION_SAVE_CONFIG", e))
        }
    }

    val CMCLWorkingDirectory: File
        get() {
            if (isDebug()) return File(".cmcl")
            if (OperatingSystem.CURRENT_OS == OperatingSystem.LINUX || OperatingSystem.CURRENT_OS == OperatingSystem.OSX) {
                return File(System.getProperty("user.home"), ".cmcl")
            }
            val executableFilePath: String = Utils.getExecutableFilePath()
            val executableFile = File(if (!Utils.isBlank(executableFilePath)) executableFilePath else ".")
            return File(
                if (executableFile.isDirectory()) executableFile else executableFile.getParentFile()
                    ?: File("."), ".cmcl"
            )
        }

    val language: LanguageEnum by lazy {
        val languageString: String = getConfig().optString("language")
        if (isEmpty(languageString)) {
            return@lazy overriddenValueOf(Locale.getDefault().language).also {
                Utils.saveConfig(
                    getConfig().put(
                        "language",
                        it.codes.stream().findAny().orElse("en")
                    )
                )
            }
        } else {
            return@lazy overriddenValueOf(languageString)
        }
    }

    val locale: Locale by lazy {
        language.locale
    }

    fun getString(name: String, vararg objects: Any?): String {
        return String.format(getString(name), *objects)
    }

    fun getString(name: String): String {
        val text = language.textMap[name]
        return if (!Utils.isEmpty(text)) text
        else {
            val inEnglish = LanguageEnum.ENGLISH.textMap[name]
            if (!Utils.isEmpty(inEnglish)) inEnglish else name
        }
    }

    fun listVersions(versionsDir: File): List<String> {
        val versionsStrings: MutableList<String> = ArrayList()
        val files = versionsDir.listFiles { pathname: File ->
            if (!pathname.isDirectory()) return@listFiles false
            val files1 = pathname.listFiles()
            if (files1 == null || files1.isEmpty()) return@listFiles false
            File(
                pathname,
                pathname.getName() + ".json"
            ).exists() /*&& new File(pathname, pathname.getName() + ".jar").exists()*/
        }
        if (files != null) {
            versionsStrings.addAll(files.map { it.name })
        }
        return versionsStrings
    }
}