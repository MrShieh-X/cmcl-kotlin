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
package com.mrshiehx.cmcl.modSources.modrinth

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.enums.ModrinthSection
import com.mrshiehx.cmcl.modSources.Manager
import com.mrshiehx.cmcl.modSources.curseforge.CurseForgeManager
import com.mrshiehx.cmcl.utils.Utils
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

abstract class ModrinthManager : Manager<ModrinthSection>() {
    abstract override val section: ModrinthSection
    override val nameAllLowerCase: String
        get() = section.nameAllLowerCase

    override fun search(searchContent: String, limit: Int): JSONObject? {
        val searchResult: JSONArray = try {
            val jsonObject = JSONObject(
                NetworkUtils[ROOT + "search?query=" + URLEncoder.encode(
                    searchContent,
                    StandardCharsets.UTF_8.name()
                ) + "&limit=" + (if (limit < 0) 50 else limit) + "&facets=" + URLEncoder.encode(
                    "[[\"project_type:" + section.projectType + "\"]]",
                    StandardCharsets.UTF_8.name()
                )]
            )
            jsonObject.optJSONArray("hits")
        } catch (e: Exception) {
            println(CMCL.getString("MESSAGE_FAILED_SEARCH", e))
            return null
        }
        val list: List<JSONObject> = JSONUtils.jsonArrayToJSONObjectList(searchResult)
        if (list.isEmpty()) {
            println(CMCL.getString("NO_SEARCH_RESULTS"))
            return null
        }
        for (i in list.indices.reversed()) {
            try {
                val result: JSONObject = list[i]
                var gameVersion: String? = null
                val versions: JSONArray? = result.optJSONArray("versions")
                if (versions != null && versions.length() > 0) {
                    val list2: MutableList<String> = LinkedList()
                    for (o in versions.toList()) {
                        if (o is String) {
                            list2.add(o)
                        }
                    }
                    list2.sortWith { o1, o2 -> -VersionUtils.tryToCompareVersion(o1, o2) }
                    if (list2.size > 0) {
                        gameVersion = list2[0]
                    }
                }
                CurseForgeManager.printOne(
                    i + 1,
                    result.optString("title"),
                    result.optString("description"),
                    Ansi.ansi().fg(Ansi.Color.RED).a(result.optString("author")).toString(),
                    gameVersion,
                    null
                )
            } catch (e: Exception) {
                println(
                    CMCL.getString("CF_FAILED_TO_SHOW_SOMEONE", i + 1, e).replace("\${NAME}", section.nameAllLowerCase)
                )
            }
        }
        val number = InteractionUtils.inputInt(
            Utils.getString("CF_SELECT_TARGET", 1, list.size).replace("\${NAME}", section.nameAllLowerCase),
            1,
            list.size
        )
        return if (number != null) {
            list[number - 1]
        } else null
    }

    fun printInformation(bySearch: JSONObject?, byID: JSONObject?, originalModName: String?, modId: String?) {
        var modName = originalModName
        val information: MutableMap<String, String?> = LinkedHashMap()
        //System.out.println(bySearch);
        var id = modId
        var description: String? = null
        var author: String? = null
        var dateModified: String? = null
        var dateCreated: String? = null
        var iconUrl: String? = null
        var gameVersions: JSONArray? = null
        var downloads = -1
        val categories1: MutableCollection<String> = HashSet()
        val donations: MutableList<Pair<String, String>> = LinkedList()
        if (bySearch != null) {
            id = bySearch.optString("project_id", modId)
            description = bySearch.optString("description")
            author = bySearch.optString("author")
            //latestGameVersion = bySearch.optString("latest_version");
            dateModified = bySearch.optString("date_modified")
            dateCreated = bySearch.optString("date_created")
            iconUrl = bySearch.optString("icon_url")
            gameVersions = bySearch.optJSONArray("versions")
            val a: String = bySearch.optString("title")
            if (!Utils.isEmpty(a)) modName = a
            downloads = bySearch.optInt("downloads", -1)
            val categories: JSONArray? = bySearch.optJSONArray("categories")
            if (categories != null && categories.length() > 0) {
                for (o in categories) if (o is String) categories1.add(o)
            }
        }
        if (byID != null) {
            if (Utils.isEmpty(description)) description = byID.optString("description")
            if (Utils.isEmpty(dateModified)) dateModified = byID.optString("updated")
            if (Utils.isEmpty(dateCreated)) dateCreated = byID.optString("published")
            if (Utils.isEmpty(id)) id = byID.optString("id")
            if (downloads < 0) downloads = byID.optInt("downloads", -1)
            if (Utils.isEmpty(modName)) modName = byID.optString("title")
            if (Utils.isEmpty(iconUrl)) iconUrl = byID.optString("icon_url")
            if (gameVersions == null) gameVersions = byID.optJSONArray("game_versions")
            val a: String = byID.optString("title")
            if (!Utils.isEmpty(a)) modName = a
            val categories: JSONArray? = byID.optJSONArray("categories")
            if (categories != null && categories.length() > 0) {
                for (o in categories) if (o is String) categories1.add(o)
            }
            val donationUrls: JSONArray? = byID.optJSONArray("donation_urls")
            if (donationUrls != null && donationUrls.length() > 0) {
                for (o in donationUrls) {
                    if (o is JSONObject) {
                        val platform: String = o.optString("platform")
                        val url: String = o.optString("url")
                        if (!Utils.isEmpty(platform) && !Utils.isEmpty(url)) {
                            donations.add(Pair(platform, url))
                        }
                    }
                }
            }
        }
        if (!Utils.isEmpty(modName)) information[section.informationNameTip] = modName
        if (!Utils.isEmpty(id)) information[section.informationIdTip] = id
        if (!Utils.isEmpty(description)) information[CMCL.getString("CF_INFORMATION_SUMMARY")] = description
        if (!Utils.isEmpty(iconUrl)) information[CMCL.getString("CF_INFORMATION_ICON")] = iconUrl
        if (!Utils.isEmpty(author)) information[CMCL.getString("CF_INFORMATION_AUTHOR")] = author
        var latestGameVersion: String? = null
        if (gameVersions != null) {
            val list: List<String> =
                gameVersions.toList().map { java.lang.String.valueOf(it) }.sortedWith(VersionUtils.VERSION_COMPARATOR)
            if (list.isNotEmpty()) latestGameVersion = list[list.size - 1]
        }
        if (Utils.isEmpty(latestGameVersion) && bySearch != null)
            latestGameVersion = bySearch.optString("latest_version")
        if (!Utils.isEmpty(latestGameVersion))
            information[CMCL.getString("CF_INFORMATION_LATEST_GAME_VERSION")] = latestGameVersion
        if (categories1.isNotEmpty()) {
            information[CMCL.getString("CF_INFORMATION_CATEGORIES")] = categories1.toTypedArray().contentToString()
        }
        if (!Utils.isEmpty(dateModified) && dateModified.length >= 19) {
            val dateString = CurseForgeManager.parseDate(dateModified)
            information[CMCL.getString("CF_INFORMATION_DATE_MODIFIED")] = dateString
        }
        if (!Utils.isEmpty(dateCreated) && dateCreated.length >= 19) {
            val dateString = CurseForgeManager.parseDate(dateCreated)
            information[CMCL.getString("CF_INFORMATION_DATE_CREATED")] = dateString
        }
        if (downloads >= 0) information[CMCL.getString("CF_INFORMATION_DOWNLOAD_COUNT")] = downloads.toString()
        if (donations.size > 0) {
            val donation = StringBuilder()
            for ((name, url) in donations) {
                donation.append('\n').append("      ").append(name).append('\n')
                donation.append(CMCL.getString("CF_INFORMATION_DONATION_URL")).append(url)
            }
            information[CMCL.getString("CF_INFORMATION_DONATION")] = donation.toString()
        }
        if (byID != null) {
            val issueTrackerUrl: String = byID.optString("issues_url")
            if (!Utils.isEmpty(issueTrackerUrl))
                information[CMCL.getString("CF_INFORMATION_ISSUE_TRACKER_URL")] = issueTrackerUrl
            val sourceUrl: String = byID.optString("source_url")
            if (!Utils.isEmpty(sourceUrl)) information[CMCL.getString("CF_INFORMATION_SOURCE_URL")] = sourceUrl
            val bodyUrl: String = byID.optString("body_url")
            if (!Utils.isEmpty(bodyUrl)) information[CMCL.getString("CF_INFORMATION_WEBSITE_URL")] = bodyUrl
            val wikiUrl: String = byID.optString("wiki_url")
            if (!Utils.isEmpty(wikiUrl)) information[CMCL.getString("CF_INFORMATION_WIKI_URL")] = wikiUrl
            val discordUrl: String = byID.optString("discord_url")
            if (!Utils.isEmpty(discordUrl)) information[CMCL.getString("CF_INFORMATION_DISCORD_URL")] = discordUrl
        }
        if (information.isEmpty()) {
            println(CMCL.getString("CF_INFORMATION_NOTHING", section.nameAllLowerCase))
        } else {
            println("$modName:") //legal
            for ((key, value) in information) {
                print(key) //legal
                println(value) //legal
            }
        }
    }

    @Throws(IncorrectCategoryAddon::class, IOException::class)
    fun getByID(id: String): JSONObject {
        val mod = JSONObject(NetworkUtils[ROOT + "project/" + id])
        val a: ModrinthSection? = ModrinthSection.xvalueOf(mod.optString("project_type"))
        if (a !== section) throw IncorrectCategoryAddon(a)
        return mod
    }

    override fun getDownloadLink(
        modId: String,
        originalModName: String?,
        mcVersion: String?,
        addonVersion: String?,
        isModpack: Boolean,
        dependencyInstaller: DependencyInstaller
    ): String? {
        var modName: String? = originalModName
        val modAllVersionsJsonArrayFather =
            try {
                JSONArray(NetworkUtils[ROOT + "project/" + modId + "/version"])
            } catch (e: Exception) {
                println(CMCL.getString("MOD_FAILED_TO_GET_ALL_FILES", e).replace("\${NAME}", nameAllLowerCase))
                return null
            }
        val modClassificationMap: MutableMap<String, ArrayList<Pair<JSONObject, JSONObject>>> =
            HashMap<String, ArrayList<Pair<JSONObject, JSONObject>>>()
        //开始装载并排序
        for (i in 0 until modAllVersionsJsonArrayFather.length()) {
            val modVersion: JSONObject = modAllVersionsJsonArrayFather.optJSONObject(i) ?: continue
            val gameVersion: JSONArray = modVersion.optJSONArray("game_versions") ?: JSONArray()
            for (j in 0 until gameVersion.length()) {
                val ver: String = gameVersion.optString(j)
                val oneMcVerOfModSupport: ArrayList<Pair<JSONObject, JSONObject>> =
                    if (modClassificationMap.containsKey(ver)) modClassificationMap[ver]!! else ArrayList<Pair<JSONObject, JSONObject>>()
                //格式化时间 2019-07-22T01:56:42.27Z -> 2019-07-22T01:56:42
                modVersion.put("date_published", modVersion.optString("date_published").substring(0, 19))
                val files: JSONArray? = modVersion.optJSONArray("files")
                if (files != null) {
                    for (`object` in files) {
                        if (`object` is JSONObject) {
                            if (files.length() == 1) {
                                oneMcVerOfModSupport.add(Pair(`object`, modVersion))
                            } else {
                                val url: String = `object`.optString("url")
                                val filename: String = `object`.optString("filename")
                                if (!Utils.isEmpty(url) &&
                                    !Utils.isEmpty(filename) &&  //!filename.endsWith("-dev.jar")&&
                                    !filename.endsWith("-sources-dev.jar") &&
                                    !filename.endsWith("-sources.jar")
                                ) {
                                    oneMcVerOfModSupport.add(Pair(`object`, modVersion))
                                }
                            }
                        }
                    }
                }
                addonFilesTimeSort(oneMcVerOfModSupport)
                modClassificationMap[ver] = oneMcVerOfModSupport
            }
        }
        val modSupportMinecraftVersion: String?
        if (!Utils.isEmpty(mcVersion) && modClassificationMap[mcVersion] != null) {
            modSupportMinecraftVersion = mcVersion
        } else {
            //Mod 支持的所有 MC 版本
            val modSupportedMcVer = ArrayList(modClassificationMap.keys)

            //排序算法
            modSupportedMcVer.sortWith(VersionUtils.VERSION_COMPARATOR)
            if (Utils.isEmpty(modName)) {
                try {
                    modName = getByID(modId).optString("title")
                } catch (ignored: Exception) {
                }
            }
            println(CMCL.getString("CF_SUPPORTED_GAME_VERSION", modName))
            PrintingUtils.printListItems(modSupportedMcVer, false, 6, 3, true)
            modSupportMinecraftVersion = InteractionUtils.inputStringInFilter(
                CMCL.getString("CF_INPUT_GAME_VERSION"), CMCL.getString("CONSOLE_INPUT_STRING_NOT_FOUND")
            ) { modSupportedMcVer.contains(it) }
            if (modSupportMinecraftVersion == null) return null
        }
        val versions: ArrayList<Pair<JSONObject, JSONObject>> =
            modClassificationMap[modSupportMinecraftVersion] ?: return null
        if (versions.size == 0) {
            println(CMCL.getString("CF_NO_VERSION_FOR_GAME_VERSION", nameAllLowerCase))
            return null
        }
        var targetFile: Pair<JSONObject, JSONObject>? = null
        if (!Utils.isEmpty(addonVersion)) {
            val matches: List<Pair<JSONObject, JSONObject>> = versions.filter { (_, value) ->
                val versionNumber: String = value.optString("version_number")
                versionNumber.contains(addonVersion)
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
                        .a(i + 1) + Ansi.ansi().fg(Ansi.Color.WHITE)
                        .a(("]" + versions[i].first.optString("filename")) + "\n")
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

        if (!isModpack) {
            //System.out.println(targetFile.getValue());
            val jsonArray: JSONArray? = targetFile.second.optJSONArray("dependencies")
            if (jsonArray != null && jsonArray.length() > 0) {
                val list: MutableMap<String, String?> = HashMap()
                for (`object` in jsonArray) {
                    if (`object` is JSONObject) {
                        val jsonObject: JSONObject = `object`
                        val dmodid: String = jsonObject.optString("project_id")
                        var name: String? = null
                        try {
                            val head: JSONObject = getByID(dmodid)
                            name = head.optString("title")
                        } catch (ignore: Exception) {
                        }
                        if (!Utils.isEmpty(dmodid)) list[dmodid] = name
                    }
                }
                if (list.isNotEmpty()) {
                    println()
                    println(CMCL.getString("CF_DEPENDENCIES_TIP").replace("\${NAME}", nameAllLowerCase))
                    var i = 0
                    for ((id, name) in list) {
                        val stringBuilder = StringBuilder()
                        stringBuilder.append(CMCL.getString("CF_DEPENDENCY_INFORMATION_ID_STRING", id)).append('\n')
                        if (!Utils.isEmpty(name)) {
                            stringBuilder.append(CMCL.getString("CF_DEPENDENCY_INFORMATION_NAME", name)).append('\n')
                        }
                        println(stringBuilder) //legal
                        i++
                    }
                    println()
                    for ((key, value) in list) {
                        dependencyInstaller.install(modSupportMinecraftVersion, value, key)
                    }
                }
            }
        }
        return targetFile.first.optString("url")
    }


    class IncorrectCategoryAddon(var section: ModrinthSection?) : Exception()

    companion object {
        private const val ROOT = "https://api.modrinth.com/v2/"
        private val TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

        //按时间排序每个 JsonObject
        private fun addonFilesTimeSort(list: ArrayList<Pair<JSONObject, JSONObject>>) {
            list.sortWith { (key1, _), (key2, _) ->
                //2021-06-14T15:14:23.68Z
                try {
                    val dt1 = TIME_FORMAT.parse(key1.optString("date_published"))
                    val dt2 = TIME_FORMAT.parse(key2.optString("date_published"))
                    return@sortWith dt2.time.compareTo(dt1.time)
                } catch (ignore: Exception) {
                }
                0
            }
        }
    }
}
