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
package com.mrshiehx.cmcl.modules

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.bean.Library
import com.mrshiehx.cmcl.bean.SplitLibraryName
import com.mrshiehx.cmcl.bean.arguments.Argument
import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.bean.arguments.ValueArgument
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.exceptions.EmptyNativesException
import com.mrshiehx.cmcl.exceptions.InvalidJavaException
import com.mrshiehx.cmcl.exceptions.LaunchException
import com.mrshiehx.cmcl.exceptions.LibraryDefectException
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.authlib.AuthlibInjectorAuthentication
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.authlib.AuthlibInjectorInformation
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.nide8auth.Nide8AuthAuthentication
import com.mrshiehx.cmcl.modules.account.authentication.yggdrasil.nide8auth.Nide8AuthInformation
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.cmcl.AccountUtils
import com.mrshiehx.cmcl.utils.cmcl.version.VersionLibraryUtils
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils
import com.mrshiehx.cmcl.utils.console.CommandUtils
import com.mrshiehx.cmcl.utils.system.JavaUtils
import com.mrshiehx.cmcl.utils.system.OperatingSystem
import com.mrshiehx.cmcl.utils.system.SystemUtils
import com.sun.management.OperatingSystemMXBean
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.management.ManagementFactory
import java.util.*
import java.util.regex.Pattern


object MinecraftLauncher {
    /**
     * Get Minecraft Launch Command Arguments
     *
     * @param minecraftJarFile           jar file
     * @param minecraftVersionJsonFile   json file
     * @param gameDir                    game directory
     * @param assetsDir                  assets directory
     * @param resourcePacksDir           resource packs directory
     * @param playerName                 player name
     * @param javaPath                   java path
     * @param maxMemory                  max memory
     * @param miniMemory                 mini memory
     * @param width                      window width
     * @param height                     window height
     * @param fullscreen                 is window fullscreen
     * @param accessToken                access token of official account
     * @param uuid                       uuid of official account
     * @param isDemo                     is Minecraft demo
     * @param customScreenSize           does player custom the size of screen
     * @param properties                 user properties
     * @param startLaunch                after judgment, prepare to start, nullable
     * @param jvmArgs                    append jvm arguments (nullable)
     * @param gameArgs                   append game arguments (nullable)
     * @param authlibInjectorInformation authlib-injector account (nullable)
     * @param nide8AuthInformation       nide8auth account (nullable)
     * @return Launch Command Arguments
     * @throws LaunchException        launch exception
     * @throws IOException            io or file exception
     * @throws JSONException          exception to parsing json
     * @throws LibraryDefectException exception to if some libraries are not found
     * @author MrShiehX
     */
    @Throws(
        LibraryDefectException::class,
        EmptyNativesException::class,
        LaunchException::class,
        IOException::class,
        JSONException::class
    )
    fun getMinecraftLaunchCommandArguments(
        minecraftJarFile: File,
        minecraftVersionJsonFile: File,
        gameDir: File?,
        assetsDir: File?,
        resourcePacksDir: File?,
        playerName: String,
        javaPath: String,
        maxMemory: Long,
        miniMemory: Int,
        width: Int,
        height: Int,
        fullscreen: Boolean,
        accessToken: String = Utils.randomUUIDNoSymbol(),
        uuid: String?,
        isDemo: Boolean,
        customScreenSize: Boolean,
        properties: JSONObject?,
        startLaunch: (() -> Unit)?,
        jvmArgs: List<String>?,
        gameArgs: Map<String, String?>?,
        authlibInjectorInformation: AuthlibInjectorInformation?,
        nide8AuthInformation: Nide8AuthInformation?,
        quickPlayLogFilePath: String?,
        quickPlaySaveName: String?,
        quickPlayServerAddress: String?,
        quickPlayRealmsID: String?
    ): List<String> {
        var gameDir = gameDir
        var assetsDir = assetsDir
        var resourcePacksDir = resourcePacksDir
        var javaPath = javaPath
        var accessToken = accessToken
        var uuid = uuid
        var authlibInjectorInformation = authlibInjectorInformation
        var nide8AuthInformation = nide8AuthInformation
        javaPath = try {
            getRealJavaPath(javaPath)
        } catch (e: InvalidJavaException) {
            throw LaunchException(e.message ?: "")
        }
        if (null == gameDir) {
            gameDir = File(".minecraft")
        }
        if (null == assetsDir) {
            assetsDir = File(gameDir, "assets")
        }
        if (null == resourcePacksDir) {
            resourcePacksDir = File(gameDir, "resourcepacks")
        }
        if (!gameDir.exists()) {
            gameDir.mkdirs()
        }
        if (maxMemory <= 0) {
            throw LaunchException(getString("EXCEPTION_MAX_MEMORY_MUST_BE_GREATER_THAN_ZERO"))
        }
        if (width <= 0 || height <= 0) {
            throw LaunchException(getString("EXCEPTION_WINDOW_SIZE_MUST_BE_GREATER_THAN_ZERO"))
        }
        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }
        if (!resourcePacksDir.exists()) {
            resourcePacksDir.mkdirs()
        }
        val physicalTotal =
            (ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean).totalPhysicalMemorySize / 1048576
        if (maxMemory > physicalTotal) {
            throw LaunchException(getString("EXCEPTION_MAX_MEMORY_TOO_BIG"))
        }
        val contentOfJsonFile: String =
            if (!minecraftVersionJsonFile.exists()) throw LaunchException(getString("EXCEPTION_VERSION_JSON_NOT_FOUND"))
            else FileUtils.readFileContent(minecraftVersionJsonFile)

        var authlibFile: File? = null
        if (authlibInjectorInformation != null) {
            if (authlibInjectorInformation.isEmpty) authlibInjectorInformation = null
        }
        if (authlibInjectorInformation != null) {
            try {
                authlibFile = AuthlibInjectorAuthentication.authlibInjectorFile
            } catch (e: Exception) {
                if (Constants.isDebug()) e.printStackTrace()
                Utils.printflnErr(getString("UNAVAILABLE_AUTHLIB_ACCOUNT_REASON"), e)
                Utils.printflnErr(
                    if (authlibInjectorInformation.forOfflineSkin) getString("UNAVAILABLE_CUSTOM_SKIN") else getString(
                        "UNAVAILABLE_AUTHLIB_ACCOUNT"
                    )
                )
                authlibInjectorInformation = null
            }
        }
        if (authlibInjectorInformation != null) {
            if (!Utils.isEmpty(authlibInjectorInformation.uuid)) uuid = authlibInjectorInformation.uuid
            if (!Utils.isEmpty(authlibInjectorInformation.token)) accessToken = authlibInjectorInformation.token
        }
        var nide8authFile: File? = null
        if (nide8AuthInformation != null) {
            try {
                nide8authFile = Nide8AuthAuthentication.nide8AuthFile
            } catch (e: Exception) {
                if (Constants.isDebug()) e.printStackTrace()
                Utils.printflnErr(getString("UNAVAILABLE_NIDE8AUTH_ACCOUNT_REASON"), e)
                Utils.printflnErr(getString("UNAVAILABLE_NIDE8AUTH_ACCOUNT"))
                nide8AuthInformation = null
            }
        }
        startLaunch?.invoke()
        val headJsonObject = JSONObject(contentOfJsonFile)
        if (!Utils.isEmpty(headJsonObject.optString("inheritsFrom"))) {
            throw LaunchException(getString("EXCEPTION_INCOMPLETE_VERSION"))
        } else {
            if (!minecraftJarFile.exists()) {
                throw LaunchException(getString("EXCEPTION_VERSION_JAR_NOT_FOUND"))
            }
        }
        val javaVersionJO: JSONObject? = headJsonObject.optJSONObject("javaVersion")
        val javaVersionInt = JavaUtils.getJavaVersion(javaPath)
        if (javaVersionInt != null && javaVersionJO != null) {
            val majorVersion: Int = javaVersionJO.optInt("majorVersion", -1)
            if (majorVersion != -1 && javaVersionInt < majorVersion) {
                throw LaunchException(
                    String.format(
                        getString("EXCEPTION_JAVA_VERSION_TOO_LOW"),
                        majorVersion,
                        javaVersionInt
                    )
                )
            }
        }
        if (nide8AuthInformation != null) {
            if (javaVersionInt != null && javaVersionInt < 8) {
                throw LaunchException(getString("EXCEPTION_NIDE8AUTH_JAVA_VERSION_TOO_LOW"))
            } else if (javaVersionInt == 8) {
                val originalJavaVersion = JavaUtils.getOriginalJavaVersion(javaPath)
                if (!Utils.isEmpty(originalJavaVersion) && originalJavaVersion.contains("_")) {
                    try {
                        val subVer = originalJavaVersion.splitByRegex("_")[1].toInt()
                        if (subVer < 101) {
                            throw LaunchException(getString("EXCEPTION_NIDE8AUTH_JAVA_VERSION_TOO_LOW"))
                        }
                    } catch (ignore: NumberFormatException) {
                    }
                }
            }
        }


        //File loggingFile = new File(minecraftVersionJsonFile.getParentFile(), "log4j2.xml");
        val arguments: MutableList<String> = LinkedList()
        val minecraftArguments: MutableList<String> = LinkedList()
        val jvmArguments: MutableList<String> = LinkedList()
        //String id = headJsonObject.optString("id", "1.0");
        val assetIndexObject: JSONObject? = headJsonObject.optJSONObject("assetIndex")
        val assetsIndex: String = assetIndexObject?.optString("id") ?: ""

        //String jvmArgumentsBuilder=null;

        /*18wXXa;arguments
            16wXXa;minecraftArguments
            17w44a;arguments
            17w42a;minecraftArguments
            3D Shareware v1.34;arguments
            1.RV-Pre1; minecraftArguments
            1.13 arguments
            1.12 minecraftArguments
            */
        val featuresProvided: MutableMap<String, Boolean> = HashMap()
        featuresProvided["is_demo_user"] = isDemo
        featuresProvided["has_custom_resolution"] = customScreenSize
        featuresProvided["has_quick_plays_support"] = !Utils.isEmpty(quickPlayLogFilePath)
        featuresProvided["is_quick_play_singleplayer"] = !Utils.isEmpty(quickPlaySaveName)
        featuresProvided["is_quick_play_multiplayer"] = !Utils.isEmpty(quickPlayServerAddress)
        featuresProvided["is_quick_play_realms"] = !Utils.isEmpty(quickPlayRealmsID)
        if (headJsonObject.optJSONObject("arguments") != null) {
            getGameArguments(headJsonObject, featuresProvided, minecraftArguments)
            getJavaVirtualMachineArguments(headJsonObject, featuresProvided, jvmArguments)
        }
        val args: String = headJsonObject.optString("minecraftArguments")
        if (!Utils.isEmpty(args)) {
            val argus = CommandUtils.splitCommand(CommandUtils.clearRedundantSpaces(args))
            minecraftArguments.addAll(argus)
        }

        //v1.3 去除冗余
        val a = Arguments.valueOf(minecraftArguments, false).removeDuplicate()
        val arguments1: MutableList<Argument> = LinkedList(a.getArguments())
        arguments1.sortWith sort@{ o1, o2 ->
            if ("tweakClass" == o1.key && "tweakClass" != o2.key) {
                return@sort -1
            } else if ("tweakClass" != o1.key && "tweakClass" == o2.key) {
                return@sort 1
            } else if ("tweakClass" != o1.key /*&& !"tweakClass".equals(o2.key)*/) {
                return@sort 0
            } else {
                if (o1 is ValueArgument && o2 is ValueArgument) {
                    val o1s = o1.value
                    val o2s = o2.value
                    val f1 = "net.minecraftforge.legacy._1_5_2.LibraryFixerTweaker"
                    val f2 = "cpw.mods.fml.common.launcher.FMLTweaker"
                    val f3 = "net.minecraftforge.fml.common.launcher.FMLTweaker"
                    if (o1s == f1 || o1s == f2 || o1s == f3) {
                        return@sort -1
                    } else if (o2s == f1 || o2s == f2 || o2s == f3) {
                        return@sort 1
                    } else return@sort 0
                } else {
                    return@sort 0
                }
            }
        }
        minecraftArguments.clear()
        for (argument in arguments1) {
            minecraftArguments.add("--" + argument.key)
            if (argument is ValueArgument) {
                minecraftArguments.add(argument.value)
            }
        }
        var hasThree = false
        var hasOFT = -1
        var hasOFTorOFFT = false
        val size = minecraftArguments.size
        for (i in 0 until size) {
            val argument = minecraftArguments[i]
            if (("-tweakClass" == argument || "--tweakClass" == argument || "/tweakClass" == argument) && i + 1 < size) {
                val tweakClass = minecraftArguments[i + 1]
                val aa = "optifine.OptiFineTweaker" == tweakClass
                if (aa) {
                    hasOFT = i + 1
                }
                if ((aa || "OptiFineForgeTweaker.OptiFineTweaker" == tweakClass) and !hasOFTorOFFT) {
                    hasOFTorOFFT = true
                }
                if ("com.mumfrey.liteloader.launch.LiteLoaderTweaker" == tweakClass || "net.minecraftforge.fml.common.launcher.FMLTweaker" == tweakClass || "cpw.mods.fml.common.launcher.FMLTweaker" == tweakClass || "net.minecraftforge.legacy._1_5_2.LibraryFixerTweaker" == tweakClass) {
                    hasThree = true
                }
                if (hasOFT >= 0 && hasThree && hasOFTorOFFT) break
            }
        }
        if (hasOFT >= 0 && hasThree) {
            minecraftArguments[hasOFT] = "optifine.OptiFineForgeTweaker"
        }
        val mainClass: String = headJsonObject.optString("mainClass", "net.minecraft.client.main.Main")
        val `var` =
            hasThree && hasOFTorOFFT && ("net.minecraft.launchwrapper.Launch" == mainClass || "cpw.mods.modlauncher.Launcher" == mainClass)
        val libraries: JSONArray = headJsonObject.optJSONArray("libraries") ?: JSONArray()
        val librariesFile: File =  /*new File(gameDir, "libraries")*/CMCL.librariesDir /*v2.2.1 Issue#36*/
        val pair: Triple<List<Library>, List<Library>, Boolean> = getLibraries(libraries, `var`)
        val librariesPaths: List<Library> = pair.first
        val notFound: List<Library> = pair.second
        if (notFound.isNotEmpty()) {
            throw LibraryDefectException(notFound)
        }
        val nativesFolder = VersionUtils.getNativesDir(minecraftVersionJsonFile.getParentFile())
        val librariesString = StringBuilder()
        for (library in librariesPaths) {
            librariesString.append(library.localFile.absolutePath).append(File.pathSeparator)
        }
        librariesString.append(minecraftJarFile.absolutePath)
        var assetsPath = assetsDir.absolutePath
        if (assetsIndex == "legacy") {
            assetsPath = File(assetsDir, "virtual/legacy").absolutePath
        }
        val lastGameDirPath = gameDir.absolutePath
        for (i in minecraftArguments.indices) {
            var source: String
            var s = minecraftArguments[i]
            if (s.contains("\${main_class}".also { source = it })) {
                minecraftArguments[i] =
                    s.replace(source, headJsonObject.optString("mainClass", "net.minecraft.client.main.Main"))
                        .also { s = it }
            }
            if (s.contains("\${auth_player_name}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, playerName).also { s = it }
            }
            if (s.contains("\${version_name}".also { source = it })) {
                minecraftArguments[i] =
                    s.replace(source,  /*minecraftJarFile.getParentFile().getName()*/headJsonObject.optString("id"))
                        .also { s = it }
            }
            val n: String = if (authlibInjectorInformation != null && !authlibInjectorInformation.forOfflineSkin) {
                String.format("CMCL-Kotlin %s(%s)", Constants.CMCL_VERSION_NAME, authlibInjectorInformation.serverName)
            } else if (nide8AuthInformation != null) {
                String.format("CMCL-Kotlin %s(%s)", Constants.CMCL_VERSION_NAME, nide8AuthInformation.serverName)
            } else {
                "CMCL-Kotlin " + Constants.CMCL_VERSION_NAME
            }
            if (s.contains("\${version_type}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, n).also { s = it }
            }
            if (s.contains("\${profile_name}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, n).also { s = it }
            }
            if (s.contains("\${auth_access_token}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, accessToken).also { s = it }
            }
            if (s.contains("\${auth_session}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, accessToken).also { s = it }
            }
            if (s.contains("\${game_directory}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, lastGameDirPath).also { s = it }
            }
            if (s.contains("\${assets_root}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, assetsDir.absolutePath).also { s = it }
            }
            if (s.contains("\${assets_index_name}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, assetsIndex).also { s = it }
            }
            if (s.contains("\${auth_uuid}".also { source = it })) {
                minecraftArguments[i] =
                    s.replace(source, (if (Utils.isEmpty(uuid)) AccountUtils.getUUIDByName(playerName) else uuid))
                        .also { s = it }
            }
            if (s.contains("\${user_type}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, "msa").also { s = it }
            }
            if (s.contains("\${game_assets}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, assetsPath).also { s = it }
            }
            if (s.contains("\${user_properties}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, properties?.toString() ?: "{}").also { s = it }
            }
            if (s.contains("\${resolution_width}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, width.toString()).also { s = it }
            }
            if (s.contains("\${resolution_height}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, height.toString()).also { s = it }
            }
            if (s.contains("\${primary_jar}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, minecraftJarFile.absolutePath).also { s = it }
            }
            if (s.contains("\${classpath_separator}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, File.pathSeparator).also { s = it }
            }
            if (s.contains("\${primary_jar_name}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, minecraftJarFile.getName()).also { s = it }
            }
            if (s.contains("\${library_directory}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, librariesFile.absolutePath).also { s = it }
            }
            if (s.contains("\${libraries_directory}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, librariesFile.absolutePath).also { s = it }
            }
            if (s.contains("\${language}".also { source = it })) {
                minecraftArguments[i] = s.replace(source, CMCL.locale.toString()).also { s = it }
            }
            if (s.contains("\${quickPlayPath}".also { source = it }) && !Utils.isEmpty(quickPlayLogFilePath)) {
                minecraftArguments[i] = s.replace(source, quickPlayLogFilePath).also { s = it }
            }
            if (s.contains("\${quickPlaySingleplayer}".also { source = it }) && !Utils.isEmpty(quickPlaySaveName)) {
                minecraftArguments[i] = s.replace(source, quickPlaySaveName).also { s = it }
            }
            if (s.contains("\${quickPlayMultiplayer}".also { source = it }) && !Utils.isEmpty(quickPlayServerAddress)) {
                minecraftArguments[i] = s.replace(source, quickPlayServerAddress).also { s = it }
            }
            if (s.contains("\${quickPlayRealms}".also { source = it }) && !Utils.isEmpty(quickPlayRealmsID)) {
                minecraftArguments[i] = s.replace(source, quickPlayRealmsID).also { s = it }
            }
        }
        if (resourcePacksDir.exists() && resourcePacksDir != File(gameDir, "resourcepacks")) {
            minecraftArguments.add("--resourcePackDir")
            minecraftArguments.add(resourcePacksDir.absolutePath)
        }
        if (fullscreen) minecraftArguments.add("--fullscreen")
        val nativesFiles = nativesFolder.listFiles()
        if (pair.third && (!nativesFolder.exists() || nativesFiles == null || nativesFiles.isEmpty())) {
            throw EmptyNativesException(libraries)
        }

        //String javaArgument;
        if (jvmArguments.size > 0) {
            jvmArguments.add(0, "-Xmn" + miniMemory + "m")
            jvmArguments.add(1, "-Xmx" + maxMemory + "m")
            jvmArguments.add(2, "-Dfile.encoding=UTF-8")
            for (i in jvmArguments.indices) {/*boolean replace = true;
                String replaceTo = null;*/
                var source: String
                var s = jvmArguments[i]

                /*else if (s.contains(source = "${xxxxxxxxxxxxxxxxxxxx}")) {
                    jvmArguments.set(i, s=s.replace(source, valueeeeeeeeee));
                }*/if (s.contains("\${natives_directory}".also { source = it })) {
                    jvmArguments[i] = s.replace(source, nativesFolder.absolutePath).also { s = it }
                }
                if (s.contains("\${launcher_name}".also { source = it })) {
                    val launcherName: String =
                        if (authlibInjectorInformation != null && !authlibInjectorInformation.forOfflineSkin) {
                            String.format("CMCL(%s)", authlibInjectorInformation.serverName)
                        } else if (nide8AuthInformation != null) {
                            String.format("CMCL(%s)", nide8AuthInformation.serverName)
                        } else {
                            "CMCL"
                        }
                    jvmArguments[i] = s.replace(source, launcherName).also { s = it }
                }
                if (s.contains("\${launcher_version}".also { source = it })) {
                    jvmArguments[i] = s.replace(source, Constants.CMCL_VERSION_NAME).also { s = it }
                }
                if (s.contains("\${classpath}".also { source = it })) {
                    jvmArguments[i] = s.replace(source, librariesString.toString()).also { s = it }
                }
                if (s.contains("\${file_separator}".also { source = it })) {
                    jvmArguments[i] = s.replace(source, File.separator).also { s = it }
                }
                if (s.contains("\${classpath_separator}".also { source = it })) {
                    jvmArguments[i] = s.replace(source, File.pathSeparator).also { s = it }
                }
                if (s.contains("\${library_directory}".also { source = it })) {
                    jvmArguments[i] = s.replace(source, librariesFile.absolutePath).also { s = it }
                }
                if (s.contains("\${libraries_directory}".also { source = it })) {
                    jvmArguments[i] = s.replace(source, librariesFile.absolutePath).also { s = it }
                }
                if (s.contains("\${primary_jar_name}".also { source = it })) {
                    jvmArguments[i] = s.replace(source, minecraftJarFile.getName()).also { s = it }
                }
                if (s.contains("\${version_name}".also { source = it })) {
                    jvmArguments[i] = s.replace(source, headJsonObject.optString("id")).also { s = it }
                }
            }
        } else {
            jvmArguments.add("-Xmn" + miniMemory + "m")
            jvmArguments.add("-Xmx" + maxMemory + "m")
            jvmArguments.add("-Dfile.encoding=UTF-8")
            jvmArguments.add("-Djava.library.path=" + nativesFolder.absolutePath)
            jvmArguments.add("-Dminecraft.launcher.brand=CMCL")
            jvmArguments.add("-Dminecraft.launcher.version=" + Constants.CMCL_VERSION_NAME)
            jvmArguments.add("-cp")
            jvmArguments.add(librariesString.toString())
        }
        var jvmArgusIndex = 3
        if (authlibInjectorInformation != null) {
            jvmArguments.add(jvmArgusIndex++, "-Dauthlibinjector.side=client")
            jvmArguments.add(jvmArgusIndex++, "-Dauthlibinjector.noShowServerName")
            jvmArguments.add(
                jvmArgusIndex++,
                "-javaagent:" + authlibFile!!.absolutePath + "=" + authlibInjectorInformation.serverAddress
            )
            if (!Utils.isEmpty(authlibInjectorInformation.metadataEncoded)) jvmArguments.add(
                jvmArgusIndex++,
                "-Dauthlibinjector.yggdrasil.prefetched=" + authlibInjectorInformation.metadataEncoded
            )
        }
        if (nide8AuthInformation != null) {
            jvmArguments.add(
                jvmArgusIndex++,
                "-javaagent:" + nide8authFile!!.absolutePath + "=" + nide8AuthInformation.serverId
            )
            jvmArguments.add(jvmArgusIndex++, "-Dnide8auth.client=true")
        }
        val config: JSONObject = Utils.getConfig()
        val proxyEnabled: Boolean = config.optBoolean("proxyEnabled")
        val proxyHost: String = config.optString("proxyHost")
        val proxyPort: String = config.optString("proxyPort")
        if (proxyEnabled && !Utils.isEmpty(proxyHost) && !Utils.isEmpty(proxyPort) && config.optString("proxyUsername")
                .isEmpty() && config.optString("proxyPassword").isEmpty()
        ) {
            jvmArguments.add(jvmArgusIndex++, "-Dhttp.proxyHost=$proxyHost")
            jvmArguments.add(jvmArgusIndex++, "-Dhttp.proxyPort=$proxyPort")
            jvmArguments.add(jvmArgusIndex++, "-Dhttps.proxyHost=$proxyHost")
            jvmArguments.add(jvmArgusIndex++, "-Dhttps.proxyPort=$proxyPort")
        }


        /*JSONObject logging = headJsonObject.optJSONObject("logging");
        if (logging != null) {
            JSONObject client = logging.optJSONObject("client");
            if (client != null) {
                String argument = client.optString("argument");
                if (!loggingFile.exists() || loggingFile.length() == 0) {
                    JSONObject file = client.optJSONObject("file");
                    if (file != null) {
                        String url = file.optString("url");
                        if (!Utils.isEmpty(url)) {
                            try {
                                System.out.print(String.format(getString("MESSAGE_DOWNLOADING_FILE"), loggingFile.getName()));
                                ConsoleMinecraftLauncher.downloadFile(url,loggingFile,new XProgressBar());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (loggingFile.exists() && loggingFile.length() > 0) {
                    jvmArguments.add(jvmArgusIndex++, argument.replace("${path}", loggingFile.getAbsolutePath()));
                }
            }
        }*/

        /*自定义JVM参数*/
        val addableJvmArgs: MutableList<String> = LinkedList()
        if (!jvmArgs.isNullOrEmpty()) {
            for (arg in jvmArgs) {
                val indexOf = arg.indexOf('=')
                var ab: String? = null
                if (indexOf >= 0) {
                    ab = arg.substring(0, indexOf) + "="
                }
                var contains = false
                var replace = -1
                if (!Utils.isEmpty(ab)) {
                    for (i in jvmArguments.indices) {
                        val rarg = jvmArguments[i]
                        if (rarg.startsWith(ab)) {
                            contains = true
                            if (indexOf + 1 < arg.length) {
                                replace = i
                            }
                            break
                        }
                    }
                } else {
                    for (rarg in jvmArguments) {
                        if (rarg == arg) {
                            contains = true
                            break
                        }
                    }
                }
                if (!contains) {
                    addableJvmArgs.add(arg)
                } else {
                    if (replace >= 0) {
                        jvmArguments[replace] = arg
                    }
                }
            }
        }


        /*自定义游戏参数*/
        val addableGameArgs: MutableList<String> = LinkedList()
        if (!gameArgs.isNullOrEmpty()) {
            for ((key, value) in gameArgs) {
                var contains = false
                if (Utils.isEmpty(value)) {
                    for (arg in minecraftArguments) {
                        if (arg == "--$key" || arg == "-$key") {
                            contains = true
                            break
                        }
                    }
                    if (!contains) {
                        addableGameArgs.add("--$key")
                    }
                } else {
                    for (i in minecraftArguments.indices) {
                        val arg = minecraftArguments[i]
                        if (arg == "--$key" || arg == "-$key") {
                            contains = true
                            if (i + 1 >= minecraftArguments.size) {
                                minecraftArguments.add(value)
                            } else {
                                val next = minecraftArguments[i + 1]
                                if (!next.startsWith("--") && !next.startsWith("-")) {
                                    minecraftArguments[i + 1] = value
                                } else {
                                    minecraftArguments.add(i + 1, value)
                                }
                            }
                            break
                        }
                    }
                    if (!contains) {
                        addableGameArgs.add("--$key")
                        addableGameArgs.add(value)
                    }
                }
            }
        }
        if (!Utils.isEmpty(quickPlayServerAddress) && !minecraftArguments.contains("--quickPlayMultiplayer")) {
            val address = quickPlayServerAddress.splitByRegex(":")
            minecraftArguments.add("--server")
            minecraftArguments.add(address[0])
            minecraftArguments.add("--port")
            minecraftArguments.add(if (address.size > 1) address[1] else "25565")
        }
        arguments.add(javaPath)
        arguments.addAll(addableJvmArgs)
        arguments.addAll(jvmArguments)
        arguments.add(mainClass)
        arguments.addAll(minecraftArguments)
        arguments.addAll(addableGameArgs)
        return arguments
    }

    /**
     * Get Minecraft Launch Command, see [MinecraftLauncher.getMinecraftLaunchCommandArguments]
     *
     * @author MrShiehX
     */
    @Throws(
        LibraryDefectException::class,
        EmptyNativesException::class,
        LaunchException::class,
        IOException::class,
        JSONException::class
    )
    fun getMinecraftLaunchCommand(
        minecraftJarFile: File,
        minecraftVersionJsonFile: File,
        gameDir: File?,
        assetsDir: File?,
        resourcePacksDir: File?,
        playerName: String,
        javaPath: String,
        maxMemory: Long,
        miniMemory: Int,
        width: Int,
        height: Int,
        fullscreen: Boolean,
        accessToken: String = Utils.randomUUIDNoSymbol(),
        uuid: String?,
        isDemo: Boolean,
        customScreenSize: Boolean,
        properties: JSONObject?,
        jvmArgs: List<String>?,
        gameArgs: Map<String, String?>?,
        authlibInjectorInformation: AuthlibInjectorInformation?,
        nide8AuthInformation: Nide8AuthInformation?,
        quickPlayLogFilePath: String?,
        quickPlaySaveName: String?,
        quickPlayServerAddress: String?,
        quickPlayRealmsID: String?
    ): String {
        val args = getMinecraftLaunchCommandArguments(
            minecraftJarFile,
            minecraftVersionJsonFile,
            gameDir,
            assetsDir,
            resourcePacksDir,
            playerName,
            javaPath,
            maxMemory,
            miniMemory,
            width,
            height,
            fullscreen,
            accessToken,
            uuid,
            isDemo,
            customScreenSize,
            properties,
            null,
            jvmArgs,
            gameArgs,
            authlibInjectorInformation,
            nide8AuthInformation,
            quickPlayLogFilePath,
            quickPlaySaveName,
            quickPlayServerAddress,
            quickPlayRealmsID
        )
        val stringBuilder = StringBuilder()
        for (i in args.indices) {
            var str = args[i]
            if (str.contains(" ")) {
                str = "\"" + str + "\""
                if (str.contains("\\")) {
                    str = str.replace("\\", "\\\\")
                }
            }
            stringBuilder.append(str)
            if (i + 1 != args.size) {
                stringBuilder.append(" ")
            }
        }
        return stringBuilder.toString()
    }

    fun isMeetConditions(rules: JSONArray, featuresProvided: Map<String, Boolean>): Boolean {
        if (rules.length() == 0) return true
        var action = "disallow"
        for (i in 0 until rules.length()) {
            val rule: JSONObject = rules.optJSONObject(i) ?: continue
            var thisAction: String?
            run meetsRule@{
                val os: JSONObject? = rule.optJSONObject("os")
                val features: JSONObject? = rule.optJSONObject("features")
                var osMatches: Boolean
                run osMatches@{
                    if (os != null) {
                        val name: String = os.optString("name")
                        val version: String = os.optString("version")
                        val arch: String = os.optString("arch")
                        if (!Utils.isEmpty(name) && name != OperatingSystem.CURRENT_OS.checkedName) {
                            osMatches = false
                            return@osMatches
                        }
                        if (!Utils.isEmpty(version) && !Pattern.compile(version)
                                .matcher(System.getProperty("os.version")).find()
                        ) {
                            osMatches = false
                            return@osMatches
                        }
                        if (!Utils.isEmpty(arch)) {
                            osMatches = Pattern.compile(arch).matcher(System.getProperty("os.arch")).matches()
                            return@osMatches
                        }
                        osMatches = true
                    } else osMatches = false
                }
                if (os != null && !osMatches) {
                    thisAction = null
                    return@meetsRule
                }
                if (features != null) for ((key, value) in features.toMap().entries) if (featuresProvided[key] != value) {
                    thisAction = null
                    return@meetsRule
                }
                thisAction = rule.optString("action")
            }
            if (!Utils.isEmpty(thisAction)) action = thisAction!!
        }
        return action == "allow"
    }

    /**
     * Launch Minecraft, see [MinecraftLauncher.getMinecraftLaunchCommandArguments]
     *
     * @author MrShiehX
     */
    @Throws(
        LibraryDefectException::class,
        EmptyNativesException::class,
        LaunchException::class,
        IOException::class,
        JSONException::class
    )
    fun launchMinecraft(
        versionDir: File,
        minecraftJarFile: File,
        minecraftVersionJsonFile: File,
        gameDir: File?,
        assetsDir: File?,
        resourcePacksDir: File?,
        playerName: String,
        javaPath: String,
        maxMemory: Long,
        miniMemory: Int,
        width: Int,
        height: Int,
        fullscreen: Boolean,
        accessToken: String = Utils.randomUUIDNoSymbol(),
        uuid: String?,
        isDemo: Boolean,
        customScreenSize: Boolean,
        properties: JSONObject?,
        jvmArgs: List<String>?,
        gameArgs: Map<String, String?>?,
        authlibInjectorInformation: AuthlibInjectorInformation?,
        nide8AuthInformation: Nide8AuthInformation?,
        quickPlayLogFilePath: String?,
        quickPlaySaveName: String?,
        quickPlayServerAddress: String?,
        quickPlayRealmsID: String?
    ): Process {
        val args = getMinecraftLaunchCommandArguments(
            minecraftJarFile,
            minecraftVersionJsonFile,
            gameDir,
            assetsDir,
            resourcePacksDir,
            playerName,
            javaPath,
            maxMemory,
            miniMemory,
            width,
            height,
            fullscreen,
            accessToken,
            uuid,
            isDemo,
            customScreenSize,
            properties,
            { println(getString("MESSAGE_STARTING_GAME")) },
            jvmArgs,
            gameArgs,
            authlibInjectorInformation,
            nide8AuthInformation,
            quickPlayLogFilePath,
            quickPlaySaveName,
            quickPlayServerAddress,
            quickPlayRealmsID
        )
        val processBuilder = ProcessBuilder(args)
        processBuilder.directory(versionDir)
        processBuilder.redirectErrorStream(true)
        return processBuilder.start()
    }

    private fun getJavaVirtualMachineArguments(
        headJsonObject: JSONObject,
        featuresProvided: Map<String, Boolean>,
        args: MutableList<String>
    ) {
        getArguments(headJsonObject, "jvm", featuresProvided, args)
    }

    private fun getGameArguments(
        headJsonObject: JSONObject,
        featuresProvided: Map<String, Boolean>,
        args: MutableList<String>
    ) {
        getArguments(headJsonObject, "game", featuresProvided, args)
    }

    private fun getArguments(
        headJsonObject: JSONObject,
        name: String,
        featuresProvided: Map<String, Boolean>,
        args: MutableList<String>
    ) {
        //StringBuilder arguments = new StringBuilder();
        val argumentsArray: JSONObject = headJsonObject.optJSONObject("arguments")
        val array: JSONArray = argumentsArray.optJSONArray(name) ?: return
        for (i in 0 until array.length()) {
            val obj: Any = array.opt(i)
            if (obj is String) {
                args.add(obj)
            } else if (obj is JSONObject) {
                val jsonObject: JSONObject? = array.optJSONObject(i)
                if (jsonObject != null && jsonObject.has("value")) {
                    val value: Any? = jsonObject.opt("value")
                    if (value != null) {
                        var meetConditions = true
                        val rules: JSONArray? = jsonObject.optJSONArray("rules")
                        if (rules != null) {
                            meetConditions = isMeetConditions(rules, featuresProvided)
                        }
                        if (meetConditions) {
                            if (value is JSONArray) {
                                val value2: JSONArray = value
                                for (k in 0 until value2.length()) {
                                    if (value2.opt(k) is String) {
                                        args.add(Utils.valueOf(value2.opt(k)))
                                    }
                                }
                            } else {
                                args.add(Utils.valueOf(value))
                            }
                        }
                    }
                }
            }
        }
        //return arguments.substring(0, arguments.length()-1);
    }

    fun getLibraries(libraries: JSONArray): Triple<List<Library>, List<Library>, Boolean> =
        getLibraries(libraries, false)


    /**
     * 获得依赖库
     *
     * @param libraries 依赖库JSONArray
     * @return 1st 为存在的依赖库集合，2nd 为不存在的依赖库集合, 3rd 为是否拥有natives
     */
    fun getLibraries(
        libraries: JSONArray,
        replaceOptiFineToOptiFineInstaller: Boolean
    ): Triple<List<Library>, List<Library>, Boolean> {
        val librariesPaths: MutableMap<String, Library> = LinkedHashMap()
        val notFound: MutableMap<String, Library> = HashMap()
        var needNatives = false
        //List<String> names = new ArrayList<>();
        for (i in 0 until libraries.length()) {
            val library: JSONObject = libraries.optJSONObject(i)
            var meet = true
            val rules: JSONArray? = library.optJSONArray("rules")
            if (rules != null) {
                meet = isMeetConditions(rules, emptyMap())
            }
            if (!meet) continue
            val downloads: JSONObject? = library.optJSONObject("downloads")
            if (downloads != null) {
                if (downloads.optJSONObject("classifiers") != null) {
                    needNatives = true
                    if (downloads.optJSONObject("artifact") == null) {
                        continue
                    }
                }
            }
            val name: String = library.optString("name")
            var nameSplit = VersionLibraryUtils.splitLibraryName(name) ?: continue
            if (replaceOptiFineToOptiFineInstaller && "optifine" == nameSplit.first && "OptiFine" == nameSplit.second) {
                nameSplit = SplitLibraryName(
                    nameSplit.first,
                    nameSplit.second,
                    nameSplit.version,
                    "installer",
                    nameSplit.extension
                )
            }
            val key =
                nameSplit.first + ":" + nameSplit.second + if (!Utils.isEmpty(nameSplit.classifier)) ":" + nameSplit.classifier else ""
            val exist = librariesPaths[key]
            val libraryFile = nameSplit.physicalFile
            val lb = Library(library, libraryFile)
            if (exist == null) {
                if (libraryFile.exists() && libraryFile.length() > 0) {
                    librariesPaths[key] = lb
                } else {
                    notFound[key] =
                        lb/*if (!notFound.containsKey(key) */ /*&& !library.optString("name").isEmpty()*/ /**/ /*&&((library.has("downloads") && library.optJSONObject("downloads").has("artifact"))||library.has("url"))*/ /*) {
                            notFound.put(key, lb);
                        }*/
                }
            } else {
                val existName = exist.libraryJSONObject.optString("name")
                val existNameSplit = VersionLibraryUtils.splitLibraryName(existName) ?: continue
                if (existNameSplit.first == nameSplit.first && existNameSplit.second == nameSplit.second) {
                    val compare = VersionUtils.tryToCompareVersion(existNameSplit.version, nameSplit.version)
                    if (compare == -1 || compare == 0 && lb.libraryJSONObject.length() > exist.libraryJSONObject.length()) {
                        if (libraryFile.exists() && libraryFile.length() > 0) {
                            librariesPaths[key] = lb
                            notFound.remove(key)
                        } else {
                            notFound[key] = lb
                            librariesPaths.remove(key)
                        }
                    }
                }
            }

        }
        return Triple(LinkedList(librariesPaths.values), LinkedList(notFound.values), needNatives)
    }

    fun isModpack(versionDir: File? /*, JSONObject versionJSON*/): Boolean {
        if (File(versionDir, "modpack.cfg").exists()) //兼容 HMCL
            return true
        if (File(versionDir, "modpack.json").exists()) //CMCL
            return true
        if (File(versionDir, "manifest.json").exists() || File(
                versionDir,
                "mcbbs.packmeta"
            ).exists() || File(versionDir, "modrinth.index.json").exists() || File(versionDir, "mmc-pack.json").exists()
        ) //兼容 BakaXL
            return true
        val setupIni = File(versionDir, "PCL/Setup.ini")
        try {
            if (setupIni.isFile() && Pattern.compile("VersionArgumentIndie:\\s*1")
                    .matcher(FileUtils.readFileContent(setupIni)).find()//兼容 PCL2，严格来说此处不严谨，因为是判断是否设置版本隔离，不过也没有问题
            ) return true
        } catch (ignored: IOException) {
        }
        return false
    }

    @Throws(InvalidJavaException::class)
    fun getRealJavaPath(originalJavaPath: String): String {
        var javaPath = originalJavaPath
        var javaPathFile = File(javaPath)
        if (!javaPathFile.exists()) {
            throw InvalidJavaException(getString("EXCEPTION_JAVA_NOT_FOUND"))
        } else {
            if (javaPathFile.isDirectory()) {
                javaPathFile = File(
                    javaPathFile,
                    if (javaPathFile.getName()
                            .equals("bin", ignoreCase = true)
                    ) (if (SystemUtils.isWindows) "java.exe" else "java")
                    else if (SystemUtils.isWindows) "bin\\java.exe" else "bin/java"
                )
                if (!javaPathFile.exists()) {
                    javaPathFile = File(javaPath, if (SystemUtils.isWindows) "java.exe" else "java")
                    javaPath = if (!javaPathFile.exists()) {
                        throw InvalidJavaException(getString("CONSOLE_INCORRECT_JAVA"))
                    } else {
                        javaPathFile.path
                    }
                } else {
                    javaPath = javaPathFile.path
                }
            }
        }
        return javaPath
    }
}
