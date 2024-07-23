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
import com.mrshiehx.cmcl.bean.XDate
import com.mrshiehx.cmcl.bean.arguments.ArgumentRequirement
import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.bean.arguments.TextArgument
import com.mrshiehx.cmcl.bean.arguments.ValueArgument
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.functions.mod.ModFunction
import com.mrshiehx.cmcl.modSources.modrinth.ModrinthModManager
import com.mrshiehx.cmcl.modules.extra.fabric.FabricMerger
import com.mrshiehx.cmcl.modules.extra.forge.ForgeMerger
import com.mrshiehx.cmcl.modules.extra.liteloader.LiteloaderMerger
import com.mrshiehx.cmcl.modules.extra.optifine.OptiFineMerger
import com.mrshiehx.cmcl.modules.extra.quilt.QuiltMerger
import com.mrshiehx.cmcl.modules.version.VersionInstaller
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.console.PrintingUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

object InstallFunction : Function {
    override val usageName = "install"
    override fun execute(arguments: Arguments) {
        val firstArg = arguments.optArgument(1)
        if (firstArg is TextArgument) {
            if (!Function.checkArgs(
                    arguments, 2, 1,
                    ArgumentRequirement.ofSingle("s"),
                    ArgumentRequirement.ofSingle("select"),
                    ArgumentRequirement.ofSingle("api"),
                    ArgumentRequirement.ofSingle("no-assets"),
                    ArgumentRequirement.ofSingle("no-libraries"),
                    ArgumentRequirement.ofSingle("no-natives"),
                    ArgumentRequirement.ofSingle("fabric"),
                    ArgumentRequirement.ofSingle("forge"),
                    ArgumentRequirement.ofSingle("liteloader"),
                    ArgumentRequirement.ofSingle("optifine"),
                    ArgumentRequirement.ofSingle("quilt"),
                    ArgumentRequirement.ofValue("n"),
                    ArgumentRequirement.ofValue("name"),
                    ArgumentRequirement.ofValue("t"),
                    ArgumentRequirement.ofValue("thread"),
                    ArgumentRequirement.ofValue("fabric"),
                    ArgumentRequirement.ofValue("forge"),
                    ArgumentRequirement.ofValue("liteloader"),
                    ArgumentRequirement.ofValue("optifine"),
                    ArgumentRequirement.ofValue("quilt"),
                    ArgumentRequirement.ofValue("api")
                )
            ) return
            val version = firstArg.originString
            val storage = arguments.opt("n", arguments.opt("name", version)) ?: version
            if (File(CMCL.versionsDir, "$storage/$storage.json").exists()) {
                Utils.printfln(CMCL.getString("MESSAGE_INSTALL_INPUT_NAME_EXISTS"), storage)
                return
            }
            try {
                val versionsFile: File = Utils.downloadVersionsFile()
                val versions: JSONArray? = JSONObject(FileUtils.readFileContent(versionsFile)).optJSONArray("versions")
                val threadCount =
                    arguments.optInt("t", arguments.optInt("thread", Constants.DEFAULT_DOWNLOAD_THREAD_COUNT))
                val installFabric = arguments.contains("fabric")
                val installForge = arguments.contains("forge")
                val installQuilt = arguments.contains("quilt")
                val installLiteLoader = arguments.contains("liteloader")
                val installOptiFine = arguments.contains("optifine")
                if (!installFabric && arguments.contains("api")) {
                    println(CMCL.getString("INSTALL_FABRIC_API_WITHOUT_FABRIC"))
                    return
                }
                var installForgeOrFabricOrQuilt: VersionInstaller.InstallForgeOrFabricOrQuilt? = null
                if (installFabric && installForge) {
                    println(CMCL.getString("INSTALL_COEXIST", "Fabric", "Forge"))
                    return
                }
                if (installFabric && installLiteLoader) {
                    println(CMCL.getString("INSTALL_COEXIST", "Fabric", "LiteLoader"))
                    return
                }
                if (installFabric && installOptiFine) {
                    println(CMCL.getString("INSTALL_COEXIST", "Fabric", "OptiFine"))
                    return
                }
                if (installFabric && installQuilt) {
                    println(CMCL.getString("INSTALL_COEXIST", "Fabric", "Quilt"))
                    return
                }
                if (installQuilt && installForge) {
                    println(CMCL.getString("INSTALL_COEXIST", "Quilt", "Forge"))
                    return
                }
                if (installQuilt && installLiteLoader) {
                    println(CMCL.getString("INSTALL_COEXIST", "Quilt", "LiteLoader"))
                    return
                }
                if (installQuilt && installOptiFine) {
                    println(CMCL.getString("INSTALL_COEXIST", "Quilt", "OptiFine"))
                    return
                }
                if (installFabric) {
                    installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.FABRIC
                } else if (installForge) {
                    installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.FORGE
                } else if (installQuilt) {
                    installForgeOrFabricOrQuilt = VersionInstaller.InstallForgeOrFabricOrQuilt.QUILT
                }
                VersionInstaller.start(version,
                    storage,
                    versions,
                    installAssets = !arguments.contains("no-assets"),
                    installNatives = !arguments.contains("no-natives"),
                    installLibraries = !arguments.contains("no-libraries"),
                    installForgeOrFabricOrQuilt,
                    if (threadCount != null && threadCount > 0) threadCount else Constants.DEFAULT_DOWNLOAD_THREAD_COUNT,
                    { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                        val a: Pair<Boolean, List<JSONObject>?> = FabricMerger.merge(
                            minecraftVersion,
                            headJSONObject,
                            minecraftJarFile,
                            askContinue,
                            arguments.opt("fabric")
                        )
                        if (askContinue && !a.first) {
                            FileUtils.deleteDirectory(minecraftJarFile.getParentFile())
                        }
                        a
                    },
                    { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                        val a: Pair<Boolean, List<JSONObject>?> = ForgeMerger.merge(
                            minecraftVersion,
                            headJSONObject,
                            minecraftJarFile,
                            askContinue,
                            arguments.opt("forge")
                        )
                        if (askContinue && !a.first) {
                            FileUtils.deleteDirectory(minecraftJarFile.getParentFile())
                        }
                        a
                    },
                    { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                        val a: Pair<Boolean, List<JSONObject>?> = QuiltMerger.merge(
                            minecraftVersion,
                            headJSONObject,
                            minecraftJarFile,
                            askContinue,
                            arguments.opt("quilt")
                        )
                        if (askContinue && !a.first) {
                            FileUtils.deleteDirectory(minecraftJarFile.getParentFile())
                        }
                        a
                    },
                    if (installLiteLoader) VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                        val a: Pair<Boolean, List<JSONObject>?> = LiteloaderMerger.merge(
                            minecraftVersion,
                            headJSONObject,
                            minecraftJarFile,
                            askContinue,
                            arguments.opt("liteloader")
                        )
                        if (askContinue && !a.first) {
                            FileUtils.deleteDirectory(minecraftJarFile.getParentFile())
                        }
                        a
                    } else null,
                    if (installOptiFine) VersionInstaller.Merger { minecraftVersion: String, headJSONObject: JSONObject, minecraftJarFile: File, askContinue: Boolean ->
                        val a: Pair<Boolean, List<JSONObject>?> = OptiFineMerger.merge(
                            minecraftVersion,
                            headJSONObject,
                            minecraftJarFile,
                            askContinue,
                            arguments.opt("optifine")
                        )
                        if (askContinue && !a.first) {
                            FileUtils.deleteDirectory(minecraftJarFile.getParentFile())
                        }
                        a
                    } else null,
                    {
                        println(CMCL.getString("MESSAGE_INSTALLED_NEW_VERSION"))
                        if (arguments.contains("s") || arguments.contains("select")) {
                            Utils.saveConfig(Utils.getConfig().put("selectedVersion", storage))
                        }
                        if (installFabric && arguments.contains("api")) {
                            val url = ModrinthModManager().getDownloadLink(
                                "P7dR8mSH",
                                "Fabric API",
                                version.replace(" Pre-Release ", "-pre"),
                                arguments.opt("api"),
                                isModpack = false,
                                ModFunction.MOD_MR_DEPENDENCY_INSTALLER
                            )
                            if (!Utils.isEmpty(url)) {
                                ModFunction.downloadMod(url)
                            }
                        }
                    }, null, null
                )
            } catch (e: Exception) {
                println(e.toString())
            }
        } else if (firstArg is ValueArgument) {
            if (!Function.checkArgs(
                    arguments, 2, 1,
                    ArgumentRequirement.ofValue("show"),
                    ArgumentRequirement.ofValue("t"),
                    ArgumentRequirement.ofValue("time")
                )
            ) return
            if (firstArg.key != "show") {
                println(CMCL.getString("CONSOLE_UNKNOWN_USAGE", firstArg.key))
                return
            }
            val typeString = firstArg.value
            val typeInt: Int
            val columnsNum: Int
            val separatingSpaceLength: Int
            when (typeString.lowercase(Locale.getDefault())) {
                "a", "all" -> {
                    typeInt = 0
                    columnsNum = 3
                    separatingSpaceLength = 1
                }

                "r", "release" -> {
                    typeInt = 1
                    columnsNum = 8
                    separatingSpaceLength = 2
                }

                "s", "snapshot" -> {
                    typeInt = 2
                    columnsNum = 3
                    separatingSpaceLength = 1
                }

                "oa", "oldalpha" -> {
                    typeInt = 3
                    columnsNum = 5
                    separatingSpaceLength = 2
                }

                "ob", "oldbeta" -> {
                    typeInt = 4
                    columnsNum = 7
                    separatingSpaceLength = 2
                }

                else -> {
                    println(CMCL.getString("INSTALL_SHOW_UNKNOWN_TYPE", typeString))
                    return
                }
            }
            try {
                var start: XDate? = null
                var end: XDate? = null
                val time = arguments.opt("t", arguments.opt("time"))
                if (!Utils.isEmpty(time)) {
                    try {
                        val startAndEnd = time.splitByRegex("/")
                        val starts = startAndEnd[0].splitByRegex("-")
                        val ends = startAndEnd[1].splitByRegex("-")
                        start = XDate(starts[0].toInt(), starts[1].toInt(), starts[2].toInt())
                        end = XDate(ends[0].toInt(), ends[1].toInt(), ends[2].toInt())
                        if (XDate.compareDate(start, end) == 0) {
                            println(CMCL.getString("CONSOLE_INSTALL_SHOW_INCORRECT_TIME", time))
                            return
                        }
                    } catch (ignore: Throwable) {
                        println(CMCL.getString("CONSOLE_INSTALL_SHOW_INCORRECT_TIME", time))
                        return
                    }
                }
                val versions: MutableList<String> = LinkedList()
                val versionsFile: File = Utils.downloadVersionsFile()
                val versionsJSONArray: JSONArray =
                    JSONObject(FileUtils.readFileContent(versionsFile)).optJSONArray("versions") ?: JSONArray()
                for (i in 0 until versionsJSONArray.length()) {
                    val jsonObject: JSONObject = versionsJSONArray.optJSONObject(i) ?: continue
                    val id: String = jsonObject.optString("id")
                    val timeHere: String = jsonObject.optString("releaseTime")
                    val timeAllow: Boolean
                    if (!Utils.isEmpty(timeHere) && start != null /*&&end!=null*/) {
                        var thiz: XDate? = null
                        try {
                            val times = timeHere.substring(0, 4 + 1 + 2 + 1 + 2).splitByRegex("-")
                            if (times.size > 1) {
                                thiz = XDate(times[0].toInt(), times[1].toInt(), times[2].toInt())
                            }
                        } catch (ignore: Throwable) {
                        }
                        timeAllow = if (thiz != null) {
                            /* 0 first > second
                             * 1 first < second
                             * 2 first = second
                             **/
                            val compareOfStart = XDate.compareDate(start, thiz)
                            val compareOfEnd = XDate.compareDate(end!!, thiz)
                            if (compareOfStart == 1 || compareOfStart == 2) {
                                compareOfEnd == 0 || compareOfEnd == 2
                            } else {
                                false
                            }
                        } else {
                            false
                        }
                    } else timeAllow = true
                    if (typeInt == 0) {
                        if (timeAllow) versions.add(id)
                    } else {
                        val type: String = jsonObject.optString("type")
                        when (typeInt) {
                            1 -> if ("release" == type) if (timeAllow) versions.add(id)
                            2 -> if ("snapshot" == type) if (timeAllow) versions.add(id)
                            3 -> if ("old_alpha" == type) if (timeAllow) versions.add(id)
                            4 -> if ("old_beta" == type) if (timeAllow) versions.add(id)
                        }
                    }
                }
                PrintingUtils.printListItems(versions, true, columnsNum, separatingSpaceLength, true)
            } catch (exception: Exception) {
                exception.printStackTrace()
                Utils.printfln(CMCL.getString("CONSOLE_FAILED_LIST_VERSIONS"), exception)
            }
        } else {
            println(Utils.getString("CONSOLE_UNKNOWN_COMMAND_OR_MEANING", firstArg!!.originString))
        }
    }

}
