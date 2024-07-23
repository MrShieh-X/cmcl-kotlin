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
package com.mrshiehx.cmcl.modules.version.downloaders

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.api.download.DownloadSource
import com.mrshiehx.cmcl.modules.MinecraftLauncher
import com.mrshiehx.cmcl.utils.FileUtils.deleteDirectory
import com.mrshiehx.cmcl.utils.FileUtils.unZip
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.cmcl.version.VersionUtils
import com.mrshiehx.cmcl.utils.console.PercentageTextProgress
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import com.mrshiehx.cmcl.utils.system.OperatingSystem
import com.mrshiehx.cmcl.utils.system.SystemUtils
import org.json.JSONObject
import java.io.File

object NativesDownloader {
    fun download(versionDir: File, librariesJa: List<JSONObject>?) {
        val tempNatives = File(CMCL.CMCLWorkingDirectory, "temp_natives")
        tempNatives.mkdirs()
        try {
            if (librariesJa != null) {
                val nativesNames: MutableList<String> = ArrayList()
                for (jsonObject in librariesJa) {
                    var meet = true
                    val rules = jsonObject.optJSONArray("rules")
                    if (rules != null) {
                        meet = MinecraftLauncher.isMeetConditions(rules, emptyMap())
                    }
                    val downloadsJo1 = jsonObject.optJSONObject("downloads")
                    if (meet && downloadsJo1 != null) {
                        val classifiersJo = downloadsJo1.optJSONObject("classifiers")
                        if (classifiersJo != null) {
                            val nativesNamesJO = jsonObject.optJSONObject("natives")
                            if (nativesNamesJO != null) {
                                val nativesJo = classifiersJo.optJSONObject(
                                    nativesNamesJO.optString(OperatingSystem.CURRENT_OS.checkedName)
                                        .replace("\${arch}", SystemUtils.archInt)
                                )
                                downloadSingleNative(tempNatives, nativesJo, nativesNames)
                            }
                        }
                    }
                }
                println(CMCL.getString("MESSAGE_INSTALL_DOWNLOADED_LIBRARIES"))
            } else {
                println(CMCL.getString("MESSAGE_INSTALL_LIBRARIES_LIST_EMPTY"))
                return
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
            println(CMCL.getString("MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARIES", e1))
        }
        val nativesDir = File(versionDir, VersionUtils.nativesDirName)
        unzip(tempNatives, nativesDir)
    }

    fun unzip(tempNatives: File, nativesDir: File) {
        val natives = tempNatives.listFiles { _, name1: String -> name1.endsWith(".jar") }
        if (natives == null || natives.isEmpty()) {
            println(CMCL.getString("MESSAGE_INSTALL_NATIVES_EMPTY_JAR"))
            return
        }
        println(CMCL.getString("MESSAGE_INSTALL_DECOMPRESSING_NATIVE_LIBRARIES"))
        nativesDir.mkdirs()
        for (file in natives) {
            try {
                //File dir = new File(tempNatives, file.getName().substring(0, file.getName().lastIndexOf(".")));
                //dir.mkdirs();
                print(CMCL.getString("MESSAGE_UNZIPPING_FILE", file.getName()))
                unZip(file, nativesDir, PercentageTextProgress()) { string: String ->
                    val s: String? = Utils.getExtension(string)
                    Utils.isEmpty(s) || !Utils.isEmpty(s) && s != "sha1" && s != "git"
                }
            } catch (e1: Exception) {
                e1.printStackTrace()
                println(CMCL.getString("MESSAGE_FAILED_TO_DECOMPRESS_FILE", file.absolutePath, e1))
            }
        }
        println(CMCL.getString("MESSAGE_INSTALL_DECOMPRESSED_NATIVE_LIBRARIES"))

        /*List<File> libFiles = new ArrayList<>();
        String houzhui = ".so";

        String osName = System.getProperty("os.name");

        if (osName.toLowerCase().contains("windows")) {
            houzhui = ".dll";
        } else if (osName.toLowerCase().contains("mac")) {
            houzhui = ".dylib";
        }

        File[] var4 = tempNatives.listFiles((dir, name12) -> dir.exists() && dir.isDirectory());

        if (var4 == null || var4.length == 0) {
            System.out.println(getString("MESSAGE_INSTALL_NATIVES_EMPTY_NATIVE_FILE"));
            return;

        }

        for (File file : var4) {
            if (file != null && file.isDirectory()) {
                String finalHouzhui = houzhui;
                File[] files = file.listFiles((dir, name12) -> name12.toLowerCase().endsWith(finalHouzhui) || name12.toLowerCase().endsWith(".jnilib"));
                libFiles.addAll(Arrays.asList(files));
            }
        }

        for (File file : libFiles) {
            File to = new File(nativesDir, file.getName());
            try {

                System.out.println(getString("MESSAGE_COPYING_FILE", file.getName(), to.getPath()));
                if (to.exists()) to.delete();
                Utils.copyFile(file, to);
            } catch (IOException e1) {
                e1.printStackTrace();
                System.out.println(getString("MESSAGE_FAILED_TO_COPY_FILE", file.getAbsolutePath(), to.getAbsolutePath(), e1));
            }
        }

        System.out.println(getString("MESSAGE_INSTALL_COPIED_NATIVE_LIBRARIES"));*/deleteDirectory(tempNatives)
    }

    fun downloadSingleNative(tempNatives: File, nativesJo: JSONObject, nativesNames: MutableList<String>) {
        val name12 = VersionUtils.getNativeLibraryName(nativesJo.optString("path"))
        if (!nativesNames.contains(name12)) {
            var url1 = nativesJo.optString("url")
            if (!Utils.isEmpty(url1)) {
                url1 = url1.replace("https://libraries.minecraft.net/", DownloadSource.getProvider().libraries())
                val nativeFile = File(tempNatives, url1.substring(url1.lastIndexOf("/") + 1))
                try {
                    //if(!nativeFile.exists()) {
                    //nativeFile.createNewFile();
                    print(CMCL.getString("MESSAGE_DOWNLOADING_FILE", url1.substring(url1.lastIndexOf("/") + 1)))
                    DownloadUtils.downloadFile(url1, nativeFile, PercentageTextProgress())
                    nativesNames.add(name12)
                    //}
                } catch (e1: Exception) {
                    //Utils.downloadFileFailed(url1, nativeFile, e1);
                    println(CMCL.getString("MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON", url1, e1))
                }
            }
        }

    }
}
