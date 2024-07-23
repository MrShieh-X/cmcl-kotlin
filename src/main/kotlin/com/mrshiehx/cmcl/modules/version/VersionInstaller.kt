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
package com.mrshiehx.cmcl.modules.version

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.api.download.DownloadSource
import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.modules.MinecraftLauncher
import com.mrshiehx.cmcl.modules.version.downloaders.AssetsDownloader
import com.mrshiehx.cmcl.modules.version.downloaders.NativesDownloader
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.cmcl.version.VersionLibraryUtils
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils
import com.mrshiehx.cmcl.utils.console.PercentageTextProgress
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import com.mrshiehx.cmcl.utils.json.XJSONObject
import com.mrshiehx.cmcl.utils.system.OperatingSystem
import com.mrshiehx.cmcl.utils.system.SystemUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

object VersionInstaller {
    @Throws(ExceptionWithDescription::class)
    fun start(
        versionName: String,
        storage: String,
        versions: JSONArray?,
        installAssets: Boolean,
        installNatives: Boolean,
        installLibraries: Boolean,
        installForgeOrFabricOrQuilt: InstallForgeOrFabricOrQuilt?,
        threadCount: Int,
        fabricMerger: Merger?,
        forgeMerger: Merger?,
        quiltMerger: Merger?,
        liteLoaderMerger: Merger?,
        optiFineMerger: Merger?,
        onFinished: (() -> Unit)?,
        versionJSONMerger: VersionJSONMerger?,
        librariesToBeMerged: JSONArray?
    ) {
        /*if (!checkNetwork(DownloadSource.getProvider().versionJSON())) {
            throw new DescriptionException(getString("MESSAGE_FAILED_TO_CONNECT_TO_URL", DownloadSource.getProvider().versionJSON()));
        }*/
        val cmcl: File = CMCL.CMCLWorkingDirectory
        //File versionsFile = new File(cmcl, "versions.json");
        cmcl.mkdirs()
        if (versions == null || versions.length() == 0) {
            throw ExceptionWithDescription(CMCL.getString("MESSAGE_VERSIONS_LIST_IS_EMPTY"))
        }
        var url: String? =
            versions.filterIsInstance<JSONObject>().firstOrNull { it.optString("id") == versionName }?.optString("url")
        if (Utils.isEmpty(url)) {
            throw ExceptionWithDescription(CMCL.getString("EXCEPTION_VERSION_NOT_FOUND", versionName))
        }
        url = url.replace("https://launchermeta.mojang.com/", DownloadSource.getProvider().versionJSON())
            .replace("https://piston-meta.mojang.com/", DownloadSource.getProvider().versionJSON())
        val versionDir: File = File(CMCL.versionsDir, storage)
        versionDir.mkdirs()
        val jsonFile = File(versionDir, "$storage.json")
        val jarFile = File(versionDir, "$storage.jar")
        if (jsonFile.exists()) jsonFile.delete()
        val headVersionFile: XJSONObject
        try {
            val versionJSONBytes = DownloadUtils.downloadBytes(url)
            //downloadFile(url, jsonFile);
            headVersionFile = XJSONObject(String(versionJSONBytes))
            headVersionFile.put("gameVersion", headVersionFile.optString("id"))
            headVersionFile.put("id", storage)
        } catch (e: IOException) {
            throw ExceptionWithDescription(CMCL.getString("MESSAGE_FAILED_TO_CONTROL_VERSION_JSON_FILE", e))
        }
        if (installForgeOrFabricOrQuilt == InstallForgeOrFabricOrQuilt.FABRIC && fabricMerger != null) {
            if (!fabricMerger.merge(versionName, headVersionFile, jarFile, true).first) {
                return
            }
        } else if (installForgeOrFabricOrQuilt == InstallForgeOrFabricOrQuilt.QUILT && quiltMerger != null) {
            if (!quiltMerger.merge(versionName, headVersionFile, jarFile, true).first) {
                return
            }
        }
        versionJSONMerger?.merge(headVersionFile)
        val downloadsJo = headVersionFile.optJSONObject("downloads")
            ?: throw ExceptionWithDescription(CMCL.getString("MESSAGE_INSTALL_NOT_FOUND_JAR_FILE_DOWNLOAD_INFO"))
        val clientJo = downloadsJo.optJSONObject("client")
            ?: throw ExceptionWithDescription(CMCL.getString("MESSAGE_INSTALL_NOT_FOUND_JAR_FILE_DOWNLOAD_INFO"))
        val urlClient: String = clientJo.optString("url")
            .replace("https://launcher.mojang.com/", DownloadSource.getProvider().versionClient())
        if (Utils.isEmpty(urlClient)) {
            throw ExceptionWithDescription(CMCL.getString("MESSAGE_INSTALL_JAR_FILE_DOWNLOAD_URL_EMPTY"))
        }
        try {
            print(CMCL.getString("MESSAGE_INSTALL_DOWNLOADING_JAR_FILE"))
            jarFile.createNewFile()
            try {
                DownloadUtils.downloadFile(urlClient, jarFile, PercentageTextProgress())
            } catch (e: Exception) {
                FileUtils.deleteDirectory(versionDir)
                throw ExceptionWithDescription(CMCL.getString("MESSAGE_FAILED_DOWNLOAD_FILE", urlClient))
            }
            println(CMCL.getString("MESSAGE_INSTALL_DOWNLOADED_JAR_FILE"))

            /*安装顺序不可乱*/if (installForgeOrFabricOrQuilt == InstallForgeOrFabricOrQuilt.FORGE && forgeMerger != null) {
                println(CMCL.getString("MESSAGE_START_INSTALLING_FORGE"))
                if (!forgeMerger.merge(versionName, headVersionFile, jarFile, true).first) {
                    return
                }
                println(CMCL.getString("MESSAGE_INSTALLED_FORGE"))
            }
            if (liteLoaderMerger != null) {
                println(CMCL.getString("MESSAGE_START_INSTALLING_LITELOADER"))
                if (!liteLoaderMerger.merge(versionName, headVersionFile, jarFile, true).first) {
                    return
                }
                println(CMCL.getString("MESSAGE_INSTALLED_LITELOADER"))
            }
            if (optiFineMerger != null) {
                println(CMCL.getString("MESSAGE_START_INSTALLING_OPTIFINE"))
                if (!optiFineMerger.merge(versionName, headVersionFile, jarFile, true).first) {
                    return
                }
                println(CMCL.getString("MESSAGE_INSTALLED_OPTIFINE"))
            }
            jsonFile.createNewFile()
            if (librariesToBeMerged != null) {
                val toBeMerged: List<JSONObject> = JSONUtils.jsonArrayToJSONObjectList(librariesToBeMerged)
                if (toBeMerged.isNotEmpty()) {
                    headVersionFile.put(
                        "libraries",
                        VersionLibraryUtils.mergeLibraries(
                            JSONUtils.jsonArrayToJSONObjectList(headVersionFile.optJSONArray("libraries")),
                            toBeMerged
                        )
                    )
                }
            }
            try {
                FileUtils.writeFile(jsonFile, headVersionFile.toString(2), false)
            } catch (e: Exception) {
                FileUtils.deleteDirectory(versionDir)
                throw e
            }
            val librariesDir: File = File(CMCL.gameDir, "libraries")
            val nativesDir = File(versionDir, VersionUtils.nativesDirName)
            val tempNatives = File(cmcl, "temp_natives")
            //tempNatives.mkdirs();
            //librariesDir.mkdirs();
            //nativesDir.mkdirs();
            CMCL.createLauncherProfiles()
            if (installLibraries || installNatives) {
                downloadLibrariesAndNatives(
                    installNatives,
                    installLibraries,
                    tempNatives,
                    librariesDir,
                    nativesDir,
                    headVersionFile
                )
            }
            if (installAssets) {
                println(CMCL.getString("MESSAGE_INSTALL_DOWNLOADING_ASSETS"))
                AssetsDownloader.start(headVersionFile, threadCount) {
                    println(CMCL.getString("MESSAGE_INSTALL_DOWNLOADED_ASSETS"))
                    onFinished?.invoke()
                }
            } else {
                onFinished?.invoke()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ExceptionWithDescription(CMCL.getString("MESSAGE_FAILED_TO_INSTALL_NEW_VERSION", ex))
        }
    }

    private fun downloadLibrariesAndNatives(
        installNatives: Boolean,
        installLibraries: Boolean,
        tempNatives: File,
        librariesDir: File,
        nativesDir: File,
        headVersionFile: JSONObject
    ) {
        println(CMCL.getString("MESSAGE_INSTALL_DOWNLOADING_LIBRARIES"))
        try {
            val librariesJa: JSONArray? = headVersionFile.optJSONArray("libraries")
            if (librariesJa == null || librariesJa.length() == 0) {
                println(CMCL.getString("MESSAGE_INSTALL_LIBRARIES_LIST_EMPTY"))
                return
            }
            val nativesNames: MutableList<String> = ArrayList()
            for (i in 0 until librariesJa.length()) {
                val jsonObject: JSONObject? = librariesJa.optJSONObject(i)
                if (jsonObject != null) {
                    var meet = true
                    val rules: JSONArray? = jsonObject.optJSONArray("rules")
                    if (rules != null) {
                        meet = MinecraftLauncher.isMeetConditions(rules, emptyMap())
                    }
                    //System.out.println(meet);
                    if (!meet) continue
                    if (installLibraries) {
                        val pair = VersionLibraryUtils.getLibraryDownloadURLAndStoragePath(jsonObject)
                        if (pair != null) {
                            val path: String = pair.second
                            val url1: String? = pair.first
                            if (!Utils.isEmpty(url1)) {
                                val file = File(librariesDir, path)
                                try {
                                    file.getParentFile().mkdirs()
                                    if (!file.exists()) {
                                        file.createNewFile()
                                    }
                                    if (file.length() == 0L) {
                                        print(
                                            CMCL.getString(
                                                "MESSAGE_DOWNLOADING_FILE",
                                                url1.substring(url1.lastIndexOf("/") + 1)
                                            )
                                        )
                                        DownloadUtils.downloadFile(url1, file, PercentageTextProgress())
                                    }
                                } catch (e1: Exception) {
                                    //e1.printStackTrace();
                                    Utils.downloadFileFailed(url1, file, e1)
                                    //System.out.println(getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARY", url1, e1));
                                }
                            } else {
                                if (!jsonObject.optString("name").startsWith("net.minecraftforge:forge:")) println(
                                    CMCL.getString(
                                        "MESSAGE_NOT_FOUND_LIBRARY_DOWNLOAD_URL",
                                        jsonObject.optString("name")
                                    )
                                )
                            }
                        }
                    }
                    if (!installNatives) continue
                    val downloadsJo1: JSONObject = jsonObject.optJSONObject("downloads") ?: continue
                    val classifiersJo: JSONObject = downloadsJo1.optJSONObject("classifiers") ?: continue
                    val nativesNamesJO: JSONObject = jsonObject.optJSONObject("natives") ?: continue

                    //String osName = System.getProperty("os.name");
                    val nativesJo: JSONObject = classifiersJo.optJSONObject(
                        nativesNamesJO.optString(
                            OperatingSystem.CURRENT_OS.checkedName.replace(
                                "\${arch}",
                                SystemUtils.archInt
                            )
                        )
                    )
                        ?: continue
                    NativesDownloader.downloadSingleNative(tempNatives, nativesJo, nativesNames)
                }
            }
            println(CMCL.getString("MESSAGE_INSTALL_DOWNLOADED_LIBRARIES"))
        } catch (e1: Exception) {
            println(CMCL.getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARIES", e1))
        }
        if (installNatives) {
            NativesDownloader.unzip(tempNatives, nativesDir)
        }
    }

    private fun checkNetwork(urla: String): Boolean {
        try {
            val url = URL(urla)
            try {
                val co = url.openConnection()
                co.setConnectTimeout(12000)
                co.connect()
                return true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return false
    }

    enum class InstallForgeOrFabricOrQuilt {
        FORGE,
        FABRIC,
        QUILT
    }

    fun interface Merger {
        fun merge(
            minecraftVersion: String,
            headJSONObject: JSONObject,
            minecraftJarFile: File,
            askContinue: Boolean
        ): Pair<Boolean, List<JSONObject>?>
    }

    fun interface VersionJSONMerger {
        fun merge(headJSONObject: JSONObject)
    }
}
