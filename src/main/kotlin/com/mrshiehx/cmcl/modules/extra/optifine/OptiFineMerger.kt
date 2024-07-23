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
package com.mrshiehx.cmcl.modules.extra.optifine

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.api.download.DownloadSource
import com.mrshiehx.cmcl.bean.SplitLibraryName
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.modules.extra.ExtraMerger
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.console.PercentageTextProgress
import com.mrshiehx.cmcl.utils.console.PrintingUtils
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.system.JavaUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.jar.JarFile

object OptiFineMerger : ExtraMerger {
    private const val EXTRA_NAME = "OptiFine"

    /**
     * 将 OptiFine 的JSON合并到原版JSON
     *
     * @return key: 如果无法安装 OptiFine，是否继续安装；value：如果成功合并，则为需要安装的依赖库集合，否则为空
     */
    override fun merge(
        minecraftVersion: String,
        headJSONObject: JSONObject,
        jarFile: File,
        askContinue: Boolean,
        extraVersion: String?
    ): Pair<Boolean, List<JSONObject>?> {
        val versions = try {
            JSONArray(
                NetworkUtils[NetworkUtils.addSlashIfMissing(
                    DownloadSource.getProvider().thirdPartyOptiFine()
                ) + minecraftVersion]
            )
        } catch (e: Exception) {
            if (Constants.isDebug()) e.printStackTrace()
            println(CMCL.getString("INSTALL_MODLOADER_FAILED_TO_GET_INSTALLABLE_VERSION", EXTRA_NAME))
            return Pair(
                askContinue && InteractionUtils.yesOrNo(
                    CMCL.getString(
                        "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                        EXTRA_NAME
                    )
                ), null
            )
        }
        if (versions.length() == 0) {
            println(CMCL.getString("INSTALL_MODLOADER_NO_INSTALLABLE_VERSION", EXTRA_NAME))
            return Pair(
                askContinue && InteractionUtils.yesOrNo(
                    CMCL.getString(
                        "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                        EXTRA_NAME
                    )
                ), null
            )
        }
        val versionsMap: MutableMap<String, JSONObject> = HashMap<String, JSONObject>()
        for (o in versions) {
            if (o is JSONObject) {
                val version: JSONObject = o
                val type: String = version.optString("type")
                val patch: String = version.optString("patch")
                versionsMap[type + (if (Utils.isEmpty(patch)) "" else "_$patch")] = version
            }
        }
        if (versionsMap.isEmpty()) {
            println(CMCL.getString("INSTALL_MODLOADER_NO_INSTALLABLE_VERSION", EXTRA_NAME))
            return Pair(
                askContinue && InteractionUtils.yesOrNo(
                    CMCL.getString(
                        "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                        EXTRA_NAME
                    )
                ), null
            )
        }
        val optifineVersionString: String
        val optifineVersion: JSONObject
        if (Utils.isEmpty(extraVersion)) {
            val optifineVersionNames: List<String> = ArrayList(versionsMap.keys)
            PrintingUtils.printListItems(optifineVersionNames, true, 4, 3, true)
            val inputOFVersion = ExtraMerger.selectExtraVersion(
                CMCL.getString(
                    "INSTALL_MODLOADER_SELECT",
                    EXTRA_NAME,
                    optifineVersionNames[0]
                ), versionsMap, optifineVersionNames[0], EXTRA_NAME
            )
                ?: return Pair(false, null)
            optifineVersionString = inputOFVersion
            optifineVersion = versionsMap[inputOFVersion] ?: return Pair(false, null)
        } else {
            optifineVersionString = extraVersion
            optifineVersion = versionsMap[extraVersion] ?: run {
                println(
                    CMCL.getString("INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION", extraVersion)
                        .replace("\${NAME}", "OptiFine")
                )
                return Pair(
                    askContinue && InteractionUtils.yesOrNo(
                        CMCL.getString(
                            "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                            EXTRA_NAME
                        )
                    ), null
                )
            }
        }
        return try {
            installInternal(headJSONObject, optifineVersion, jarFile, optifineVersionString)
        } catch (e: ExceptionWithDescription) {
            println(e.message)
            Pair(
                askContinue && InteractionUtils.yesOrNo(
                    CMCL.getString(
                        "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                        EXTRA_NAME
                    )
                ), null
            )
        }
    }


    @Throws(ExceptionWithDescription::class)
    fun installInternal(
        minecraftVersion: String,
        optiFineVersionString: String,
        headJSONObject: JSONObject,
        jarFile: File
    ): Pair<Boolean, List<JSONObject>> {
        val versions = try {
            JSONArray(
                NetworkUtils[NetworkUtils.addSlashIfMissing(
                    DownloadSource.getProvider().thirdPartyOptiFine()
                ) + minecraftVersion]
            )
        } catch (e: Exception) {
            if (Constants.isDebug()) e.printStackTrace()
            throw ExceptionWithDescription(
                CMCL.getString(
                    "INSTALL_MODLOADER_FAILED_TO_GET_INSTALLABLE_VERSION",
                    EXTRA_NAME
                )
            )
        }
        if (versions.length() == 0) {
            throw ExceptionWithDescription(CMCL.getString("INSTALL_MODLOADER_NO_INSTALLABLE_VERSION", EXTRA_NAME))
        }
        val versionsMap: MutableMap<String?, JSONObject> = HashMap<String?, JSONObject>()
        for (version in versions) {
            if (version is JSONObject) {
                val type: String = version.optString("type")
                val patch: String = version.optString("patch")
                versionsMap[type + (if (Utils.isEmpty(patch)) "" else "_$patch")] = version
            }
        }
        val optifineVersion: JSONObject = versionsMap[optiFineVersionString]
            ?: throw ExceptionWithDescription(
                CMCL.getString(
                    "INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION",
                    optiFineVersionString
                ).replace("\${NAME}", EXTRA_NAME)
            )
        return installInternal(headJSONObject, optifineVersion, jarFile, optiFineVersionString)
    }

    @Throws(ExceptionWithDescription::class)
    private fun installInternal(
        headJSONObject: JSONObject,
        optiFineVersionJSONObject: JSONObject,
        jarFile: File,
        optiFineVersion: String
    ): Pair<Boolean, List<JSONObject>> {
        val patch: String = optiFineVersionJSONObject.optString("patch")
        val type: String = optiFineVersionJSONObject.optString("type")
        val mcVersion: String = optiFineVersionJSONObject.optString("mcversion")
        val url = DownloadSource.getProvider().thirdPartyOptiFine() + mcVersion + "/" + type + "/" + patch
        val installer: File = SplitLibraryName(
            "optifine",
            "OptiFine",
            mcVersion + "_" + type + "_" + patch,
            "installer"
        ).physicalFile /*new File(".cmcl","OptiFine_"+mcversion+"_"+optiFineVersion+".jar")*/
        print(CMCL.getString("INSTALL_MODLOADER_DOWNLOADING_FILE"))
        try {
            DownloadUtils.downloadFile(url, installer, PercentageTextProgress())
        } catch (e: Exception) {
            throw ExceptionWithDescription(CMCL.getString("INSTALL_MODLOADER_FAILED_DOWNLOAD", EXTRA_NAME) + ": " + e)
        }
        val librariesArray: JSONArray = headJSONObject.optJSONArray("libraries")
            ?: JSONArray().also { headJSONObject.put("libraries", it) }
        val optifineFileName =
            SplitLibraryName("optifine", "OptiFine", mcVersion + "_" + type + "_" + patch) /*lib add to json*/
        librariesArray.put(JSONObject().put("name", optifineFileName.toString()))
        val optifineFile: File = optifineFileName.physicalFile
        var containsLW = false
        try {
            JarFile(installer).use { installerJar ->
                if (installerJar.getEntry("optifine/Patcher.class") != null) {
                    val command: MutableList<String> = ArrayList(7)
                    command.add(JavaUtils.defaultJavaPath)
                    command.add("-cp")
                    command.add(installer.absolutePath)
                    command.add("optifine.Patcher")
                    command.add(jarFile.absolutePath)
                    command.add(installer.absolutePath)
                    command.add(optifineFile.absolutePath)
                    val processBuilder = ProcessBuilder(command)
                    processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
                    try {
                        val waitFor = processBuilder.start().waitFor()
                        if (waitFor != 0) {
                            throw ExceptionWithDescription(
                                CMCL.getString(
                                    "INSTALL_MODLOADER_FAILED_WITH_REASON",
                                    EXTRA_NAME,
                                    CMCL.getString("EXCEPTION_EXECUTE_COMMAND")
                                )
                            )
                        }
                    } catch (e: Exception) {
                        throw ExceptionWithDescription(
                            CMCL.getString(
                                "INSTALL_MODLOADER_FAILED_WITH_REASON",
                                EXTRA_NAME,
                                e
                            )
                        )
                    }
                } else {
                    FileUtils.copyFile(installer, optifineFile)
                }
                val lw2_0 = installerJar.getEntry("launchwrapper-2.0.jar")
                if (lw2_0 != null) {
                    val s = SplitLibraryName("optifine", "launchwrapper", "2.0") /*lib add to json*/
                    librariesArray.put(JSONObject().put("name", s.toString()))
                    FileUtils.inputStream2File(installerJar.getInputStream(lw2_0), s.physicalFile)
                    containsLW = true
                }
                val lwOfTxt = installerJar.getEntry("launchwrapper-of.txt")
                if (lwOfTxt != null) {
                    val launchWrapperVersion: String =
                        Utils.inputStream2String(installerJar.getInputStream(lwOfTxt)).trim()
                    val launchWrapperJar = installerJar.getEntry("launchwrapper-of-$launchWrapperVersion.jar")
                    if (launchWrapperJar != null) {
                        val s =
                            SplitLibraryName("optifine", "launchwrapper-of", launchWrapperVersion) /*lib add to json*/
                        librariesArray.put(JSONObject().put("name", s.toString()))
                        FileUtils.inputStream2File(installerJar.getInputStream(launchWrapperJar), s.physicalFile)
                        containsLW = true
                    }
                }
                val buildofText = installerJar.getEntry("buildof.txt")
                if (buildofText != null) {
                    val buildof: String = Utils.inputStream2String(installerJar.getInputStream(buildofText)).trim()
                    if ("cpw.mods.bootstraplauncher.BootstrapLauncher" == headJSONObject.optString("mainClass")) {
                        try {
                            val s = buildof.splitByRegex("-")
                            if (s.size >= 2) {
                                if (s[0].toInt() < 20210924 || s[0].toInt() == 20210924 && s[1].toInt() < 190833) {
                                    throw ExceptionWithDescription(CMCL.getString("INSTALL_OPTIFINE_INCOMPATIBLE_WITH_FORGE_17"))
                                }
                            }
                        } catch (ignored: Throwable) {
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw ExceptionWithDescription(CMCL.getString("INSTALL_MODLOADER_FAILED_WITH_REASON", EXTRA_NAME, e))
        }
        val returns: MutableList<JSONObject> = LinkedList<JSONObject>()
        if (!containsLW) {
            val s = SplitLibraryName("net.minecraft", "launchwrapper", "1.12")
            val library: JSONObject = JSONObject().put("name", s.toString())
            librariesArray.put(library)
            if (s.physicalFile.length() == 0L) returns.add(library)
        }
        val minecraftArguments: String = headJSONObject.optString("minecraftArguments")
        if (!Utils.isEmpty(minecraftArguments)) {
            headJSONObject.put("minecraftArguments", "--tweakClass optifine.OptiFineTweaker $minecraftArguments")
        } else {
            val arguments: JSONObject = headJSONObject.optJSONObject("arguments")
                ?: JSONObject().also { headJSONObject.put("arguments", it) }
            val game: JSONArray = arguments.optJSONArray("game") ?: JSONArray().also { arguments.put("game", it) }
            game.put("--tweakClass").put("optifine.OptiFineTweaker")
        }
        val mainClass: String = headJSONObject.optString("mainClass")
        if ("cpw.mods.bootstraplauncher.BootstrapLauncher" != mainClass && "cpw.mods.modlauncher.Launcher" != mainClass) headJSONObject.put(
            "mainClass",
            "net.minecraft.launchwrapper.Launch"
        )
        val optifine = JSONObject()
        optifine.put("version", optiFineVersion)
        optifine.put("jarUrl", url)
        headJSONObject.put("optifine", optifine)
        return Pair(true, returns)
    }

}
