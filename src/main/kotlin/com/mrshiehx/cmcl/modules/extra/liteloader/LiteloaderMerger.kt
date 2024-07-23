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
package com.mrshiehx.cmcl.modules.extra.liteloader

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.api.download.DefaultApiProvider
import com.mrshiehx.cmcl.api.download.DownloadSource
import com.mrshiehx.cmcl.bean.SplitLibraryName
import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.modules.extra.ExtraMerger
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.console.PercentageTextProgress
import com.mrshiehx.cmcl.utils.console.PrintingUtils
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object LiteloaderMerger : ExtraMerger {

    private const val MODLOADER_NAME = "LiteLoader"


    @Throws(ExceptionWithDescription::class)
    fun installInternal(
        minecraftVersion: String,
        liteloaderVersionString: String?,
        headJSONObject: JSONObject
    ): Pair<Boolean, List<JSONObject>> {
        val versionsOfLiteLoader = getVersionList(minecraftVersion)
        val liteloaderVersion = versionsOfLiteLoader[liteloaderVersionString]
            ?: throw ExceptionWithDescription(
                CMCL.getString(
                    "INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION",
                    liteloaderVersionString
                ).replace("\${NAME}", MODLOADER_NAME)
            )
        return installInternal(liteloaderVersion, headJSONObject)
    }

    /**
     * 将 LiteLoader 的JSON合并到原版JSON
     *
     * @return key: 如果无法安装 LiteLoader，是否继续安装；value：如果成功合并，则为需要安装的依赖库集合，否则为空
     */
    override fun merge(
        minecraftVersion: String,
        headJSONObject: JSONObject,
        jarFile: File,
        askContinue: Boolean,
        extraVersion: String?
    ): Pair<Boolean, List<JSONObject>?> {
        val versionsOfLiteLoader = try {
            getVersionList(minecraftVersion)
        } catch (e: Exception) {
            if (Constants.isDebug()) e.printStackTrace()
            println(e.message)
            return Pair(
                askContinue && InteractionUtils.yesOrNo(
                    CMCL.getString(
                        "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                        MODLOADER_NAME
                    )
                ), null
            )
        }
        val liteloaderVersion: LiteloaderVersion?
        if (Utils.isEmpty(extraVersion)) {
            if (versionsOfLiteLoader.isEmpty()) {
                println(CMCL.getString("INSTALL_MODLOADER_NO_INSTALLABLE_VERSION", MODLOADER_NAME))
                return Pair(
                    askContinue && InteractionUtils.yesOrNo(
                        CMCL.getString(
                            "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                            MODLOADER_NAME
                        )
                    ), null
                )
            }
            val liteloaderVersions = ArrayList(versionsOfLiteLoader.keys)
            PrintingUtils.printListItems(liteloaderVersions, true, 4, 3)
            val inputLLVersion = ExtraMerger.selectExtraVersion<LiteloaderVersion>(
                CMCL.getString(
                    "INSTALL_MODLOADER_SELECT",
                    MODLOADER_NAME,
                    liteloaderVersions[0]
                ), versionsOfLiteLoader, liteloaderVersions[0], MODLOADER_NAME
            )
                ?: return Pair(false, null)
            liteloaderVersion = versionsOfLiteLoader[inputLLVersion] ?: return Pair(false, null)
        } else {
            liteloaderVersion = versionsOfLiteLoader[extraVersion]
            if (liteloaderVersion == null) {
                println(
                    CMCL.getString("INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION", extraVersion)
                        .replace("\${NAME}", "LiteLoader")
                )
                return Pair(
                    askContinue && InteractionUtils.yesOrNo(
                        CMCL.getString(
                            "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                            MODLOADER_NAME
                        )
                    ), null
                )
            }
        }
        return try {
            installInternal(liteloaderVersion, headJSONObject)
        } catch (e: Exception) {
            println(e.message)
            Pair(
                askContinue && InteractionUtils.yesOrNo(
                    CMCL.getString(
                        "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                        MODLOADER_NAME
                    )
                ), null
            )
        }
    }

    @Throws(ExceptionWithDescription::class)
    private fun getVersionList(minecraftVersion: String): Map<String, LiteloaderVersion> {
        val versions = try {
            JSONObject(NetworkUtils[DownloadSource.getProvider().liteLoaderVersion()]).optJSONObject("versions")
        } catch (e: Exception) {
            //e.printStackTrace();
            throw ExceptionWithDescription(
                CMCL.getString(
                    "INSTALL_MODLOADER_FAILED_TO_GET_INSTALLABLE_VERSION",
                    MODLOADER_NAME
                )
            )
        }
        val version: JSONObject = versions.optJSONObject(minecraftVersion)
            ?: throw ExceptionWithDescription(
                CMCL.getString(
                    "INSTALL_MODLOADER_NO_INSTALLABLE_VERSION",
                    MODLOADER_NAME
                )
            )
        val repository: JSONObject? = version.optJSONObject("repo")

        val repoUrl = repository?.optString("url")
        if (Utils.isEmpty(repoUrl)) {
            throw ExceptionWithDescription(
                CMCL.getString(
                    "INSTALL_MODLOADER_FAILED_WITH_REASON",
                    MODLOADER_NAME,
                    "'url' in 'repo' is empty"
                )
            )
        }
        val versionsOfLiteLoader: MutableMap<String, LiteloaderVersion> = HashMap()
        val artefacts: JSONObject? = version.optJSONObject("artefacts")
        if (artefacts != null) {
            val liteloader: JSONObject = artefacts.optJSONObject("com.mumfrey:liteloader")
            executeBranch(minecraftVersion, repoUrl, liteloader, versionsOfLiteLoader, false)
        }
        val snapshots: JSONObject? = version.optJSONObject("snapshots")
        if (snapshots != null) {
            val liteloader: JSONObject = snapshots.optJSONObject("com.mumfrey:liteloader")
            executeBranch(minecraftVersion, repoUrl, liteloader, versionsOfLiteLoader, true)
        }
        return versionsOfLiteLoader
    }

    @Throws(ExceptionWithDescription::class)
    private fun installInternal(
        liteloaderVersion: LiteloaderVersion,
        headJSONObject: JSONObject
    ): Pair<Boolean, List<JSONObject>> {
        val libraryName = "com.mumfrey:liteloader:" + liteloaderVersion.version
        val libraryFile: File =
            SplitLibraryName( /*libraryName,*/"com.mumfrey", "liteloader", liteloaderVersion.version).physicalFile
        val library: JSONObject =
            JSONObject().put("name", libraryName).put("url", "http://dl.liteloader.com/versions/")
                .put("downloads", JSONObject().put("artifact", JSONObject().put("url", liteloaderVersion.url)))
        if (libraryFile.length() <= 0) {
            try {
                print(CMCL.getString("MESSAGE_DOWNLOADING_FILE", libraryFile.getName()))
                DownloadUtils.downloadFile(liteloaderVersion.url, libraryFile, PercentageTextProgress())
            } catch (e: Exception) {
                throw ExceptionWithDescription(CMCL.getString("INSTALL_MODLOADER_FAILED_DOWNLOAD", MODLOADER_NAME))
                //return new Pair<>(askContinue && ConsoleUtils.yesOrNo(getString("INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE", MODLOADER_NAME)), null);
            }
        }
        val minecraftArguments: String = headJSONObject.optString("minecraftArguments")
        headJSONObject.put(
            "minecraftArguments",
            "--tweakClass " + liteloaderVersion.tweakClass + " " + minecraftArguments
        )
        headJSONObject.put("mainClass", "net.minecraft.launchwrapper.Launch")
        val liteloader = JSONObject()
        liteloader.put("version", liteloaderVersion.version)
        liteloader.put("jarUrl", liteloaderVersion.url)
        headJSONObject.put("liteloader", liteloader)
        val libraries: List<JSONObject> = liteloaderVersion.libraries
        libraries.toMutableList().add(library)
        val headLibraries: JSONArray =
            headJSONObject.optJSONArray("libraries") ?: JSONArray().also { headJSONObject.put("libraries", it) }
        for (library2 in libraries) {
            headLibraries.put(library2)
        }
        return Pair(true, libraries)
    }

    private fun executeBranch(
        gameVersion: String,
        repoUrl: String,
        liteloader: JSONObject,
        versions: MutableMap<String, LiteloaderVersion>,
        snapshotBool: Boolean
    ) {
        for ((branchName, versionO) in liteloader.toMap().entries) {
            if (versionO !is Map<*, *> || "latest" == branchName) continue
            val versionJO = JSONObject(versionO)
            val tweakClass: String = versionJO.optString("tweakClass")
            var version: String = versionJO.optString("version")
            val file: String = versionJO.optString("file")
            var url =
                if (DownloadSource.getProvider() is DefaultApiProvider) "${repoUrl}com/mumfrey/liteloader/${gameVersion}/$file" else DownloadSource.getProvider()
                    .thirdPartyLiteLoaderDownload() + "?version=" + version
            if (snapshotBool) {
                try {
                    val snapshot = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                        ByteArrayInputStream(
                            NetworkUtils["${NetworkUtils.addSlashIfMissing(repoUrl)}com/mumfrey/liteloader/${
                                versionJO.optString(
                                    "version"
                                )
                            }/maven-metadata.xml"].toByteArray()
                        )
                    ).documentElement.getElementsByTagName("snapshot").item(0) as Element
                    version = version.replace(
                        "SNAPSHOT",
                        snapshot.getElementsByTagName("timestamp")
                            .item(0).textContent + "-" + snapshot.getElementsByTagName("buildNumber")
                            .item(0).textContent
                    )
                    url =
                        "${repoUrl}com/mumfrey/liteloader/${versionJO.optString("version")}/liteloader-${version}-release.jar"
                } catch (ignore: Exception) {
                }
            }
            val libraries: List<JSONObject> =
                JSONUtils.jsonArrayToJSONObjectList(versionJO.optJSONArray("libraries"))
            versions[version] = LiteloaderVersion(tweakClass, file, version, libraries, url)
        }
    }


    private class LiteloaderVersion(
        val tweakClass: String,
        val file: String,
        val version: String,
        val libraries: List<JSONObject>,
        val url: String
    )


}
