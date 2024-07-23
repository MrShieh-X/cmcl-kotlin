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
package com.mrshiehx.cmcl.utils.cmcl.version

import com.mrshiehx.cmcl.api.download.DefaultApiProvider
import com.mrshiehx.cmcl.api.download.DownloadSource
import com.mrshiehx.cmcl.bean.SplitLibraryName
import com.mrshiehx.cmcl.utils.JavaUtils.splitByRegex
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import org.json.JSONArray
import org.json.JSONObject

object VersionLibraryUtils {
    fun splitLibraryName(name: String): SplitLibraryName? = SplitLibraryName.valueOf(name)

    /**
     * @return KEY is downloadURL and VALUE is storage path
     */
    fun getLibraryDownloadURLAndStoragePath(library: JSONObject): Pair<String?, String>? {
        val downloads: JSONObject? = library.optJSONObject("downloads")
        return if (downloads != null) {
            val artifactJo: JSONObject? = downloads.optJSONObject("artifact")
            if (artifactJo != null) {
                var path: String = artifactJo.optString("path")
                val url: String = artifactJo.optString("url")
                if (Utils.isEmpty(path) && !Utils.isEmpty(library.optString("name"))) {
                    val name: String = library.optString("name")
                    val nameSplit: SplitLibraryName? = splitLibraryName(name)
                    if (nameSplit != null) {
                        val fileName: String = nameSplit.fileName
                        path = Utils.getPathFromLibraryName(nameSplit) + "/" + fileName
                    }
                }
                if (!Utils.isEmpty(path) || !Utils.isEmpty(url)) {
                    var url2: String? = null
                    if (!Utils.isEmpty(url)) {
                        url2 =
                            replaceUrl(url) //.replace("https://libraries.minecraft.net/", DownloadSource.getProvider().libraries()).replace("https://maven.minecraftforge.net/", DownloadSource.getProvider().forgeMaven());
                    }
                    Pair(url2, path)
                } else {
                    null
                }
            } else {
                null
            }
        } else {
            val name: String = library.optString("name")
            var url: String = library.optString("url", DownloadSource.getProvider().libraries())
            /*switch (url) {
                case "https://maven.fabricmc.net/":
                case "https://maven.fabricmc.net":
                    url = DownloadSource.getProvider().fabricMaven();
                    break;
                case "http://repo.maven.apache.org/maven2/":
                case "http://repo.maven.apache.org/maven2":
                    url = "https://repo.maven.apache.org/maven2/";
                    break;
                case "https://maven.minecraftforge.net/":
                case "https://maven.minecraftforge.net":
                case "https://files.minecraftforge.net/maven/":
                case "https://files.minecraftforge.net/maven":
                    if (!(DownloadSource.getProvider() instanceof DefaultApiProvider))
                        url = DownloadSource.getProvider().forgeMaven();
                    break;
            }*/
            url = replaceUrl(url)
            if (Utils.isEmpty(name)) return null
            val nameSplit: SplitLibraryName = splitLibraryName(name) ?: return null
            val fileName: String = nameSplit.fileName
            val path: String = Utils.getPathFromLibraryName(nameSplit) + "/" + fileName
            Pair(
                NetworkUtils.addSlashIfMissing(url) + if (name.startsWith("net.minecraftforge:forge:")) path.substring(
                    0,
                    path.length - 4
                ) + "-universal.jar" else path, path
            )
        }
    }

    fun mergeLibraries(source: List<JSONObject>, target: List<JSONObject>): JSONArray {
        val jsonArray = JSONArray()
        jsonArray.putAll(source)
        for (jsonObject in target) {
            val targetName: String = jsonObject.optString("name")
            var indexOf = -1
            for (j in source.indices) {
                val jsonObject1: JSONObject = source[j]
                val sourceName: String = jsonObject1.optString("name")
                if (sourceName == targetName) {
                    indexOf = j
                    break
                } else {
                    val targetNameSplit = targetName.splitByRegex(":")
                    val sourceNameSplit = sourceName.splitByRegex(":")
                    if (targetNameSplit.size == sourceNameSplit.size && sourceNameSplit.size >= 3) {
                        if (targetNameSplit[0] == sourceNameSplit[0] && targetNameSplit[1] == sourceNameSplit[1]) {
                            indexOf = j
                            break
                        }
                    }
                }
            }
            //if (withoutTargetServerreqAndClientreq) {
            jsonObject.remove("clientreq")
            jsonObject.remove("serverreq")
            //}
            if (indexOf < 0) {
                jsonArray.put(jsonObject)
            } else {
                jsonArray.put(indexOf, jsonObject)
            }
        }
        return jsonArray
    }

    fun replaceUrl(originalUrl: String): String {
        var url = originalUrl
        if (url.isBlank()) return url
        var a: String
        if (url.contains("https://libraries.minecraft.net/".also { a = it })) {
            url = url.replace(a, DownloadSource.getProvider().libraries())
        }
        if (url.contains("https://maven.fabricmc.net/".also { a = it })) {
            url = url.replace(a, DownloadSource.getProvider().fabricMaven())
        }
        if (url.contains("http://repo.maven.apache.org/maven2/".also { a = it })) {
            url = url.replace(a, "https://repo.maven.apache.org/maven2/")
        }
        if (url.contains("https://maven.minecraftforge.net/".also { a = it })) {
            if (DownloadSource.getProvider() !is DefaultApiProvider) {
                url = url.replace(a, DownloadSource.getProvider().forgeMaven())
            }
        }
        if (url.contains("https://files.minecraftforge.net/maven/".also { a = it })) {
            if (DownloadSource.getProvider() !is DefaultApiProvider) {
                url = url.replace(a, DownloadSource.getProvider().forgeMaven())
            }
        }
        if (url.contains("http://repo.liteloader.com/".also { a = it })) {
            url = url.replace(a, "https://repo.liteloader.com/")
        }
        return url
    }
}
