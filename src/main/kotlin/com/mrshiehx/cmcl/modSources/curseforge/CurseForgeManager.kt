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
package com.mrshiehx.cmcl.modSources.curseforge

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.enums.CurseForgeSection
import com.mrshiehx.cmcl.modSources.Manager
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.Utils.getString
import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.console.PrintingUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

abstract class CurseForgeManager : Manager<CurseForgeSection>() {
    abstract override val section: CurseForgeSection
    abstract override val nameAllLowerCase: String
    protected abstract val nameFirstUpperCase: String
    override fun getDownloadLink(
        modId: String,
        originalModName: String?,
        mcVersion: String?,
        addonVersion: String?,
        isModpack: Boolean,
        dependencyInstaller: DependencyInstaller
    ): String? {
        var modName = originalModName
        val modAllVersionsJsonArrayFather = try {
            val jsonObject = JSONObject(NetworkUtils.curseForgeGet("$GET_ADDON_INFORMATION$modId/files?pageSize=10000"))
            jsonObject.optJSONArray("data") ?: JSONArray()
        } catch (e: Exception) {
            println(CMCL.getString("MOD_FAILED_TO_GET_ALL_FILES", e).replace("\${NAME}", nameAllLowerCase))
            return null
        }

        //用来排序 https://addons-ecs.forgesvc.net/api/v2/addon/297344/files 的 Map，因为 Get 下来的 Json 是乱序的
        //String 是支持的 MC 版本，因为一个模组可以支持多个版本，一个文件一个 JsonObject ，所以采用 ArrayList<JSONObject> ，这是个一对多的 HashMap
        //一个 MC 版本对应多个 JsonObject
        val modClassificationMap: MutableMap<String, ArrayList<JSONObject>> = HashMap()
        //开始装载并排序
        for (i in 0 until modAllVersionsJsonArrayFather.length()) {
            val modVersion = modAllVersionsJsonArrayFather.optJSONObject(i) ?: continue
            val gameVersion = modVersion.optJSONArray("gameVersions") ?: JSONArray()
            for (j in 0 until gameVersion.length()) {
                val ver = gameVersion.optString(j)
                val oneMcVerOfModSupport: ArrayList<JSONObject> =
                    if (modClassificationMap.containsKey(ver)) modClassificationMap[ver]!! else ArrayList()
                //格式化时间 2019-07-22T01:56:42.27Z -> 2019-07-22T01:56:42
                modVersion.put("fileDate", modVersion.optString("fileDate").substring(0, 19))
                oneMcVerOfModSupport.add(modVersion)
                addonFilesTimeSort(oneMcVerOfModSupport)
                modClassificationMap[ver] = oneMcVerOfModSupport
            }
        }
        modClassificationMap.remove("Fabric")
        modClassificationMap.remove("Forge")
        modClassificationMap.remove("Rift")
        val modSupportMinecraftVersion: String?
        if (!isEmpty(mcVersion) && modClassificationMap[mcVersion] != null) {
            modSupportMinecraftVersion = mcVersion
        } else {
            //Mod 支持的所有 MC 版本
            val modSupportedMcVer = ArrayList<String>()

            //循环遍历 Map ，并将该 Mod 所有支持的 MC 版本提取出来并装载到 modSupportedMcVer 进行排序，因为这玩意儿在 Json 里面是乱序的
            //比如：1.15.2  1.18.1  1.16.5  1.16.4  1.12  1.14.4
            for ((key) in modClassificationMap) {
                val version = key.replace("-Snapshot", ".-1")
                if (!modSupportedMcVer.contains(version)) {
                    modSupportedMcVer.add(version)
                }
            }

            //排序算法
            modSupportedMcVer.sortWith sort@{ o1: String, o2: String ->
                val o1s = o1.splitByRegex("\\.")
                val o2s = o2.splitByRegex("\\.")
                if (o1s.isEmpty() || o2s.isEmpty()) return@sort 0
                val o1i = IntArray(o1s.size)
                val o2i = IntArray(o2s.size)
                try {
                    for (i in o1s.indices) {
                        val o1String = o1s[i]
                        o1i[i] = o1String.toInt()
                    }
                    for (i in o2s.indices) {
                        val o2String = o2s[i]
                        o2i[i] = o2String.toInt()
                    }
                } catch (e: Exception) {
                    return@sort 0
                }
                if (o1i[1] > o2i[1]) {
                    return@sort -1
                } else if (o1i[1] < o2i[1]) {
                    return@sort 1
                } else {
                    if (o1i.size >= 3 && o2i.size >= 3) {
                        return@sort o2i[2].compareTo(o1i[2])
                    } else if (o1i.size >= 3) {
                        return@sort 0.compareTo(o1i[2])
                    } else if (o2i.size >= 3) {
                        return@sort o2i[2].compareTo(0)
                    } else {
                        return@sort 0
                    }
                }
            }
            if (Utils.isEmpty(modName)) {
                try {
                    modName =
                        JSONObject(NetworkUtils.curseForgeGet(GET_ADDON_INFORMATION + modId)).optJSONObject("data")
                            ?.optString("name")
                } catch (ignore: Exception) {
                }
            }
            println(CMCL.getString("CF_SUPPORTED_GAME_VERSION", modName))
            for (i in modSupportedMcVer.indices) {
                val version = modSupportedMcVer[i]
                if (version.endsWith(".-1")) {
                    modSupportedMcVer[i] = version.replace(".-1", "-Snapshot")
                }
            }
            PrintingUtils.printListItems(modSupportedMcVer, true, 6, 3, true)
            modSupportMinecraftVersion = InteractionUtils.inputStringInFilter(
                CMCL.getString("CF_INPUT_GAME_VERSION"),
                CMCL.getString("CONSOLE_INPUT_STRING_NOT_FOUND")
            ) { o: String -> modSupportedMcVer.contains(o) }
            if (modSupportMinecraftVersion == null) return null
        }
        val versions = modClassificationMap[modSupportMinecraftVersion]
            ?: return null
        if (versions.size == 0) {
            println(CMCL.getString("CF_NO_VERSION_FOR_GAME_VERSION", nameAllLowerCase))
            return null
        }
        var targetFile: JSONObject? = null
        if (!isEmpty(addonVersion)) {
            val matches = versions.filter { mod: JSONObject ->
                val fileName = mod.optString("fileName")
                fileName.contains(addonVersion)
            }
            if (matches.size == 1) {
                targetFile = matches[0]
            }
        }
        if (targetFile == null) {
            AnsiConsole.systemInstall()
            for (i in versions.indices.reversed()) {
                print(
                    Ansi.ansi().fg(Ansi.Color.WHITE).a("[").toString() + "" + Ansi.ansi().fg(Ansi.Color.CYAN)
                        .a(i + 1) + Ansi.ansi().fg(Ansi.Color.WHITE).a("]" + versions[i].optString("fileName") + "\n")
                )
            }
            AnsiConsole.systemUninstall()
            val modVersionOfSingleMcVersion = InteractionUtils.inputInt(
                CMCL.getString("CF_INPUT_VERSION", 1, versions.size).replace("\${NAME}", nameAllLowerCase),
                1,
                versions.size,
                true,
                -1
            )
            if (modVersionOfSingleMcVersion == null || modVersionOfSingleMcVersion == -1) return null
            targetFile = versions[modVersionOfSingleMcVersion - 1]
        }
        val jsonArray = targetFile.optJSONArray("dependencies")
        if (jsonArray != null && jsonArray.length() > 0) {
            val list: MutableMap<Int, String?> = HashMap()
            for (`object` in jsonArray) {
                if (`object` is JSONObject) {
                    if (`object`.optInt("relationType") == 3 && `object`.has("modId")) {
                        val addonId = `object`.optInt("modId")
                        var name: String? = null
                        try {
                            val head =
                                JSONObject(NetworkUtils.curseForgeGet(GET_ADDON_INFORMATION + addonId)).optJSONObject("data")
                            name = head?.optString("name")
                        } catch (ignore: Exception) {
                        }
                        list[addonId] = name
                    }
                }
            }
            if (list.isNotEmpty()) {
                println()
                println(CMCL.getString("CF_DEPENDENCIES_TIP").replace("\${NAME}", nameAllLowerCase))
                var i = 0
                for ((id, name) in list) {
                    val stringBuilder = StringBuilder()
                    stringBuilder.append(CMCL.getString("CF_DEPENDENCY_INFORMATION_ID", id)).append('\n')
                    if (!Utils.isEmpty(name)) {
                        stringBuilder.append(CMCL.getString("CF_DEPENDENCY_INFORMATION_NAME", name)).append('\n')
                    }
                    println(stringBuilder) //legal
                    i++
                }
                for ((key, value) in list) {
                    dependencyInstaller.install(modSupportMinecraftVersion, Utils.valueOf(value), key.toString())
                }
            }
        }
        return targetFile.optString("downloadUrl").ifBlank {
            "https://edge.forgecdn.net/files/${targetFile.optInt("id") / 1000}/${targetFile.optInt("id") % 1000}/${
                targetFile.optString("fileName")
            }"
        }
    }

    override fun search(searchContent: String, limit: Int): JSONObject? {
        val searchResult: JSONArray = try {
            val jsonObject = JSONObject(
                NetworkUtils.curseForgeGet(
                    SEARCH_ADDONS + "&pageSize=" + limit + "&classId=" + section.sectionId + "&searchFilter=" +
                            URLEncoder.encode(searchContent, StandardCharsets.UTF_8.name())
                )
            )
            jsonObject.optJSONArray("data")
        } catch (e: Exception) {
            println(CMCL.getString("MESSAGE_FAILED_SEARCH", e))
            return null
        }
        var list = JSONUtils.jsonArrayToJSONObjectList(searchResult)
        if (list.isEmpty()) {
            println(CMCL.getString("NO_SEARCH_RESULTS"))
            return null
        }
        list = list.reversed()
        for (i in list.indices.reversed()) {
            try {
                var gameVersion: String? = null
                var projectFileName: String? = null
                val result = list[i]
                val gameVersionLatestFiles = result.optJSONArray("latestFilesIndexes")
                if (gameVersionLatestFiles != null && gameVersionLatestFiles.length() > 0) {
                    val first = gameVersionLatestFiles.optJSONObject(0)
                    if (first != null) {
                        gameVersion = first.optString("gameVersion")
                        projectFileName = first.optString("filename")
                    }
                }
                printOne(
                    i + 1,
                    result.optString("name"),
                    result.optString("summary"),
                    getAuthor(result.optJSONArray("authors") ?: JSONArray()),
                    gameVersion,
                    projectFileName
                )
            } catch (e: Exception) {
                println(CMCL.getString("CF_FAILED_TO_SHOW_SOMEONE", i + 1, e).replace("\${NAME}", nameAllLowerCase))
            }
        }
        val number = InteractionUtils.inputInt(
            getString("CF_SELECT_TARGET", 1, list.size).replace(
                "\${NAME}",
                nameAllLowerCase
            ), 1, list.size
        )
        return if (number != null) {
            list[number - 1]
        } else null
    }

    @Throws(NotMinecraftAddon::class, IncorrectCategoryAddon::class, IOException::class)
    fun getByID(modId: String): JSONObject {
        val mod = JSONObject(NetworkUtils.curseForgeGet(GET_ADDON_INFORMATION + modId)).optJSONObject("data")
        val gameId = mod.optInt("gameId")
        if (gameId != 432) {
            throw NotMinecraftAddon(gameId)
        }
        /*JSONObject categorySection = mod.optJSONObject("categorySection");
        if (categorySection == null) {
            throw new IncorrectCategoryAddon(-1);
        }*/
        val a = mod.optInt("classId")
        if (a != section.sectionId) throw IncorrectCategoryAddon(a)
        return mod
    }

    fun printInformation(mod: JSONObject, modName: String) {
        val information: MutableMap<String, String> = LinkedHashMap()
        //System.out.println(mod);
        if (!Utils.isEmpty(modName)) information[section.informationNameTip] = modName

        val id = mod.optString("id")
        if (!Utils.isEmpty(id)) information[section.informationIdTip] = id

        val summary = mod.optString("summary")
        if (!Utils.isEmpty(summary)) information[CMCL.getString("CF_INFORMATION_SUMMARY")] = summary

        val logo = mod.optJSONObject("logo")
        if (logo != null) {
            val url = logo.optString("url")
            if (!Utils.isEmpty(url)) information[CMCL.getString("CF_INFORMATION_ICON")] = url
        }

        val authorsJSONArray = mod.optJSONArray("authors")
        if (authorsJSONArray != null && authorsJSONArray.length() > 0) {
            val author = StringBuilder()
            val authors = JSONUtils.jsonArrayToJSONObjectList(authorsJSONArray)
            for (jsonObject in authors) {
                val name = jsonObject.optString("name")
                if (!Utils.isEmpty(name)) {
                    author.append("\n      ").append(name).append('\n')
                    val url = jsonObject.optString("url")
                    if (!Utils.isEmpty(url)) {
                        author.append(CMCL.getString("CF_INFORMATION_AUTHOR_URL")).append(url)
                    }
                }
            }
            information[CMCL.getString("CF_INFORMATION_AUTHORS")] = author.toString()
        }
        val gameVersionLatestFilesList =
            JSONUtils.jsonArrayToJSONObjectList(mod.optJSONArray("latestFilesIndexes") ?: JSONArray())
        if (gameVersionLatestFilesList.isNotEmpty()) {
            val list = gameVersionLatestFilesList.map { a: JSONObject -> a.optString("gameVersion") }
                .sortedWith(VersionUtils.VERSION_COMPARATOR)
            information[CMCL.getString("CF_INFORMATION_LATEST_GAME_VERSION")] = list[list.size - 1]
        }
        val downloadCount = mod.optInt("downloadCount", -1)
        if (downloadCount >= 0) {
            information[CMCL.getString("CF_INFORMATION_DOWNLOAD_COUNT")] = downloadCount.toString()
        }
        val dateModified = mod.optString("dateModified")
        if (!isEmpty(dateModified) && dateModified.length >= 19) {
            val dateString = parseDate(dateModified)
            information[CMCL.getString("CF_INFORMATION_DATE_MODIFIED")] = dateString
        }
        val dateCreated = mod.optString("dateCreated")
        if (!isEmpty(dateCreated) && dateCreated.length >= 19) {
            val dateString = parseDate(dateCreated)
            information[CMCL.getString("CF_INFORMATION_DATE_CREATED")] = dateString
        }
        val dateReleased = mod.optString("dateReleased")
        if (!isEmpty(dateReleased) && dateReleased.length >= 19) {
            val dateString = parseDate(dateReleased)
            information[CMCL.getString("CF_INFORMATION_DATE_RELEASED")] = dateString
        }
        val links = mod.optJSONObject("links")
        if (links != null) {
            val issueTrackerUrl = links.optString("issuesUrl")
            if (!Utils.isEmpty(issueTrackerUrl)) information[CMCL.getString("CF_INFORMATION_ISSUE_TRACKER_URL")] =
                issueTrackerUrl

            val sourceUrl = links.optString("sourceUrl")
            if (!Utils.isEmpty(sourceUrl)) information[CMCL.getString("CF_INFORMATION_SOURCE_URL")] = sourceUrl

            val websiteUrl = links.optString("websiteUrl")
            if (!Utils.isEmpty(websiteUrl)) information[CMCL.getString("CF_INFORMATION_WEBSITE_URL")] = websiteUrl

            val wikiUrl = links.optString("wikiUrl")
            if (!Utils.isEmpty(wikiUrl)) information[CMCL.getString("CF_INFORMATION_WIKI_URL")] = wikiUrl
        }
        if (information.isEmpty()) {
            println(CMCL.getString("CF_INFORMATION_NOTHING", nameAllLowerCase))
        } else {
            println("$modName:") //legal
            for ((key, value) in information) {
                print(key) //legal
                println(value) //legal
            }
        }
    }

    class NotMinecraftAddon(var gameId: Int) : Exception()

    class IncorrectCategoryAddon(var gameCategoryId: Int) : Exception()

    companion object {
        private const val SEARCH_ADDONS = "https://api.curseforge.com/v1/mods/search?gameId=432"
        private const val GET_ADDON_INFORMATION = "https://api.curseforge.com/v1/mods/"
        private val TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        fun printOne(
            order: Int,
            name: String,
            summary: String?,
            originalAuthor: String?,
            latestGameVersion: String?,
            latestFileName: String?
        ) {
            var author = originalAuthor
            AnsiConsole.systemInstall()
            author = if (!Utils.isEmpty(author)) {
                Ansi.ansi().fg(Ansi.Color.WHITE).a("/ ").toString() + "" + author + " "
            } else {
                ""
            }
            val gameVersionAndFileName: String
            val stringBuilder = StringBuilder()
            val notEmptyBoth = !Utils.isEmpty(latestGameVersion) && !Utils.isEmpty(latestFileName)
            if (!Utils.isEmpty(latestGameVersion) || !Utils.isEmpty(latestFileName)) {
                stringBuilder.append("(")
            }
            if (!Utils.isEmpty(latestGameVersion)) {
                stringBuilder.append(latestGameVersion)
            }
            if (notEmptyBoth) {
                stringBuilder.append(", ")
            }
            if (!Utils.isEmpty(latestFileName)) {
                stringBuilder.append(latestFileName)
            }
            if (!Utils.isEmpty(latestGameVersion) || !Utils.isEmpty(latestFileName)) {
                stringBuilder.append(")")
            }
            gameVersionAndFileName = stringBuilder.toString()
            println(
                Ansi.ansi().fg(Ansi.Color.CYAN).a(order).toString() + " " +
                        Ansi.ansi().fg(Ansi.Color.GREEN).a(
                            name
                        ) +
                        Ansi.ansi().fg(Ansi.Color.WHITE).a(" ") + author +
                        if (!isEmpty(gameVersionAndFileName)) Ansi.ansi().fg(Ansi.Color.WHITE)
                            .a(gameVersionAndFileName) else ""
            )
            AnsiConsole.systemUninstall()
            if (!isEmpty(summary)) println("    $summary")
        }

        private fun getAuthor(authors: JSONArray): String? {
            var first: String? = null
            var count = 0
            for (i in 0 until authors.length()) {
                val jsonObject = authors.optJSONObject(i) ?: continue
                val name = jsonObject.optString("name")
                if (Utils.isEmpty(name)) continue
                if (i == 0) {
                    first = name
                } else {
                    count++
                }
            }
            if (first == null) return null
            return if (count == 0) Ansi.ansi().fg(Ansi.Color.RED).a(first).toString() else Ansi.ansi()
                .fg(Ansi.Color.RED).a(first).toString() + " " + Ansi.ansi().fg(Ansi.Color.WHITE)
                .a(CMCL.getString("CF_AUTHOR_MORE", count))
        }

        //按时间排序每个 JsonObject
        private fun addonFilesTimeSort(list: MutableList<JSONObject>) {
            list.sortWith { o1: JSONObject, o2: JSONObject ->
                //2021-06-14T15:14:23.68Z
                try {
                    val dt1 = TIME_FORMAT.parse(o1.optString("fileDate"))
                    val dt2 = TIME_FORMAT.parse(o2.optString("fileDate"))
                    return@sortWith dt2.time.compareTo(dt1.time)
                } catch (ignore: Exception) {
                }
                0
            }
        }

        fun parseDate(sourceDate: String): String =
            try {
                val date = TIME_FORMAT.parse(sourceDate.substring(0, 19) + "+0000")
                val format = SimpleDateFormat(CMCL.getString("TIME_FORMAT"), CMCL.locale)
                format.format(date) + " (" + TimeZone.getDefault().displayName + ")"
            } catch (e: Exception) {
                CMCL.getString("EXCEPTION_UNABLE_PARSE")
            }
    }
}
