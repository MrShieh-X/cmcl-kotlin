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
package com.mrshiehx.cmcl.modules.extra.forge

import com.mrshiehx.cmcl.CMCL
import com.mrshiehx.cmcl.api.download.DownloadSource
import com.mrshiehx.cmcl.bean.SplitLibraryName
import com.mrshiehx.cmcl.bean.arguments.Arguments
import com.mrshiehx.cmcl.exceptions.ExceptionWithDescription
import com.mrshiehx.cmcl.modules.extra.ExtraMerger
import com.mrshiehx.cmcl.modules.version.downloaders.LibrariesDownloader
import com.mrshiehx.cmcl.utils.FileUtils
import com.mrshiehx.cmcl.utils.Utils
import com.mrshiehx.cmcl.utils.cmcl.version.VersionLibraryUtils
import com.mrshiehx.cmcl.utils.console.InteractionUtils
import com.mrshiehx.cmcl.utils.console.PrintingUtils
import com.mrshiehx.cmcl.utils.internet.DownloadUtils
import com.mrshiehx.cmcl.utils.internet.NetworkUtils
import com.mrshiehx.cmcl.utils.json.JSONUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.spi.FileSystemProvider
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Attributes
import java.util.jar.JarFile
import java.util.zip.ZipFile

object ForgeMerger : ExtraMerger {
    private const val MODLOADER_NAME = "Forge"
    private val TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

    /**
     * 将 Forge 的JSON合并到原版JSON
     *
     * @return key: 如果无法安装 Forge，是否继续安装；value：如果成功合并，则为需要安装的依赖库集合，否则为空
     */
    override fun merge(
        minecraftVersion: String,
        headJSONObject: JSONObject,
        jarFile: File,
        askContinue: Boolean,
        extraVersion: String?
    ): Pair<Boolean, List<JSONObject>?> {
        val installableForges = try {
            getInstallableForges(minecraftVersion)
        } catch (e: Exception) {
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
        if (installableForges.isEmpty()) {
            println(CMCL.getString("INSTALL_MODLOADER_NO_INSTALLABLE_VERSION_2", MODLOADER_NAME))
            return Pair(
                askContinue && InteractionUtils.yesOrNo(
                    CMCL.getString(
                        "INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE",
                        MODLOADER_NAME
                    )
                ), null
            )
        }
        val forge: JSONObject?
        if (Utils.isEmpty(extraVersion)) {
            val list: MutableList<Map.Entry<String, JSONObject>> = LinkedList(installableForges.entries)
            list.sortWith { (_, value), (_, value1) ->
                //2021-06-14T15:14:23.68Z
                try {
                    val dt1 = TIME_FORMAT.parse(value.optString("modified").substring(0, 19) + "+0000")
                    val dt2 = TIME_FORMAT.parse(value1.optString("modified").substring(0, 19) + "+0000")
                    return@sortWith java.lang.Long.compare(dt2.time, dt1.time)
                } catch (ignore: Exception) {
                    //ignore.printStackTrace();
                }
                0
            }
            PrintingUtils.printListItems(list.map { it.key }, true, 4, 2, true)
            val forgeVersionInput = ExtraMerger.selectExtraVersion(
                CMCL.getString("INSTALL_MODLOADER_SELECT", MODLOADER_NAME, list[0].key),
                installableForges,
                list[0].key,
                MODLOADER_NAME
            )
                ?: return Pair(false, null)
            forge = installableForges[forgeVersionInput] ?: return Pair(false, null)
        } else {
            forge = installableForges[extraVersion]
            if (forge == null) {
                println(
                    CMCL.getString("INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION", extraVersion)
                        .replace("\${NAME}", "Forge")
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
            installInternal(forge, headJSONObject, minecraftVersion, jarFile)
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
    fun getInstallableForges(minecraftVersion: String): Map<String, JSONObject> {
        val jsonArray = try {
            listForgeLoaderVersions(minecraftVersion)
        } catch (e: Exception) {
            //e.printStackTrace();
            throw ExceptionWithDescription(
                CMCL.getString(
                    "INSTALL_MODLOADER_FAILED_TO_GET_INSTALLABLE_VERSION",
                    MODLOADER_NAME
                )
            )
        }
        if (jsonArray.length() == 0) {
            throw ExceptionWithDescription(CMCL.getString("INSTALL_MODLOADER_NO_INSTALLABLE_VERSION", MODLOADER_NAME))
        }
        val category = "installer"
        val format = "jar"
        val forges: MutableMap<String, JSONObject> = LinkedHashMap<String, JSONObject>()
        for (`object` in jsonArray) {
            if (`object` is JSONObject) {
                val files: JSONArray? = `object`.optJSONArray("files")
                if (files != null && files.length() > 0) {
                    inside@ for (jsonObject2 in files) {
                        if (jsonObject2 is JSONObject) {
                            if (category == jsonObject2.optString("category") && format == jsonObject2.optString("format")) {
                                forges[`object`.optString("version")] = `object`
                                break@inside
                            }
                        }
                    }
                }
            }
        }
        return forges
    }

    @Throws(ExceptionWithDescription::class)
    fun installInternal(
        forge: JSONObject,
        headJSONObject: JSONObject,
        minecraftVersion: String,
        minecraftJarFile: File
    ): Pair<Boolean, List<JSONObject>> {
        val category = "installer"
        val format = "jar"
        val forgeVersion = forge.optString("version")
        val branch = forge.optString("branch")
        val mcVersion = forge.optString("mcversion")
        val s = (minecraftVersion + "-" + forgeVersion + if (!Utils.isEmpty(branch)) "-$branch" else "") //1.18.2-40.0.9
        val fileName1 = "forge-$s-$category.$format"
        val fileName2 = "forge-$s-$minecraftVersion-$category.$format"
        val first = DownloadSource.getProvider().forgeMaven() + "net/minecraftforge/forge/" + s + "/" + fileName1
        val second = DownloadSource.getProvider()
            .forgeMaven() + "net/minecraftforge/forge/" + s + "-" + minecraftVersion + "/" + fileName2

        //https://bmclapi2.bangbang93.com/forge/download?mcversion=1.18.2&version=40.0.35&category=installer&format=jar
        val installer: File = File(CMCL.CMCLWorkingDirectory, "forge-$s.jar")
        println(CMCL.getString("INSTALL_MODLOADER_DOWNLOADING_FILE"))
        var finalDownload: String?
        try {
            DownloadUtils.downloadFile(first, installer)
            finalDownload = first
        } catch (ignore: Exception) {
            try {
                DownloadUtils.downloadFile(second, installer)
                finalDownload = second
            } catch (ignored: Exception) {
                val stringBuilder = StringBuilder()
                var start = '?'
                if (!Utils.isEmpty(mcVersion)) {
                    stringBuilder.append(start).append("mcversion=").append(mcVersion)
                    start = '&'
                }
                if (!Utils.isEmpty(forgeVersion)) {
                    stringBuilder.append(start).append("version=").append(forgeVersion)
                    start = '&'
                }
                if (!Utils.isEmpty(branch)) {
                    stringBuilder.append(start).append("branch=").append(branch)
                    start = '&'
                }
                stringBuilder.append(start).append("category=").append(category)
                start = '&'
                stringBuilder.append(start).append("format=").append(format)
                //start='&';
                val third = DownloadSource.getProvider().thirdPartyForge() + stringBuilder
                try {
                    //应该是常规的下载文件函数下载不了这个链接，所以才下载了bytes弄去文件那里；也可能下载bytes的函数下载不了，才这样特别
                    val connection = try {
                        URL(third).openConnection() as HttpURLConnection
                    } catch (e: IOException) {
                        if (Utils.getConfig()
                                .optBoolean("proxyEnabled")
                        ) System.err.println(Utils.getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
                        throw e
                    }
                    connection.setDoInput(true)
                    connection.setUseCaches(false)
                    connection.setDoOutput(true)
                    connection.setRequestMethod("GET")
                    val bytes = try {
                        NetworkUtils.httpURLConnection2Bytes(connection)
                    } catch (e: IOException) {
                        if (Utils.getConfig()
                                .optBoolean("proxyEnabled")
                        ) System.err.println(Utils.getString("EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"))
                        throw e
                    }
                    FileUtils.bytes2File(installer, bytes, false)
                    finalDownload = third
                } catch (e: Exception) {
                    throw ExceptionWithDescription(
                        CMCL.getString(
                            "INSTALL_MODLOADER_FAILED_DOWNLOAD",
                            MODLOADER_NAME
                        ) + ": " + e
                    )
                }
            }
        }
        if (!installer.exists() || installer.length() == 0L) {
            throw ExceptionWithDescription(CMCL.getString("INSTALL_MODLOADER_FAILED_DOWNLOAD", MODLOADER_NAME))
        }
        val installProfile: JSONObject
        val zipFile: ZipFile
        try {
            zipFile = ZipFile(installer)
            installProfile =
                JSONObject(Utils.inputStream2String(zipFile.getInputStream(zipFile.getEntry("install_profile.json"))))
        } catch (e: IOException) {
            throw ExceptionWithDescription(
                CMCL.getString(
                    "INSTALL_MODLOADER_FAILED_WITH_REASON",
                    MODLOADER_NAME,
                    CMCL.getString("EXCEPTION_READ_FILE_WITH_PATH", installer.absolutePath) + ": " + e
                )
            )
        } catch (e: JSONException) {
            throw ExceptionWithDescription(
                CMCL.getString(
                    "INSTALL_MODLOADER_FAILED_WITH_REASON",
                    MODLOADER_NAME,
                    CMCL.getString("EXCEPTION_PARSE_FILE")
                )
            )
        }
        var version: JSONObject? = null
        val librariesNeedToInstall: MutableList<JSONObject>
        if (installProfile.has("spec")) {
            //new
            if (installProfile.optString("minecraft") != minecraftVersion) {
                throw ExceptionWithDescription(
                    CMCL.getString(
                        "INSTALL_MODLOADER_FAILED_MC_VERSION_MISMATCH",
                        MODLOADER_NAME
                    )
                )
            }
            val json: String = installProfile.optString("json")
            var installerFileSystem: FileSystem? = null //不会NULL
            for (fileSystemProvider in FileSystemProvider.installedProviders()) {
                if (fileSystemProvider.scheme.equals("jar", ignoreCase = true)) {
                    try {
                        version = JSONObject(
                            String(
                                Files.readAllBytes(
                                    fileSystemProvider.newFileSystem(
                                        installer.toPath(),
                                        HashMap<String, Any?>()
                                    ).also { installerFileSystem = it }.getPath(json)
                                )
                            )
                        )
                        break
                    } catch (e: IOException) {
                        throw ExceptionWithDescription(
                            CMCL.getString(
                                "INSTALL_MODLOADER_FAILED_WITH_REASON",
                                MODLOADER_NAME,
                                CMCL.getString("EXCEPTION_READ_FILE_WITH_PATH", json) + ": " + e
                            )
                        )
                    } catch (e: JSONException) {
                        throw ExceptionWithDescription(
                            CMCL.getString(
                                "INSTALL_MODLOADER_FAILED_WITH_REASON",
                                MODLOADER_NAME,
                                CMCL.getString("EXCEPTION_PARSE_FILE_WITH_PATH", json)
                            )
                        )
                    }
                }
            }
            if (version == null) {
                throw ExceptionWithDescription(
                    CMCL.getString(
                        "INSTALL_MODLOADER_FAILED_WITH_REASON",
                        MODLOADER_NAME,
                        CMCL.getString("EXCEPTION_READ_FILE_WITH_PATH", json)
                    )
                )
            }
            val installProfileLibraries: JSONArray? = installProfile.optJSONArray("libraries")
            if (installProfileLibraries != null && installProfileLibraries.length() > 0) {
                for (library in installProfileLibraries) {
                    if (library is JSONObject) {
                        val pair = VersionLibraryUtils.getLibraryDownloadURLAndStoragePath(library)
                        if (pair == null) {
                            println(CMCL.getString("MESSAGE_NOT_FOUND_LIBRARY_DOWNLOAD_URL", library.optString("name")))
                            continue
                        }
                        val url: String? = pair.first
                        if (Utils.isEmpty(url)) {
                            val path: String = pair.second
                            val file = File(CMCL.librariesDir, path)
                            if (file.length() <= 0) {
                                val path2 = "/maven" + if (!path.startsWith("/")) "/$path" else path
                                try {
                                    println(CMCL.getString("MESSAGE_UNZIPPING_FILE", path2))
                                    FileUtils.createFile(file, true)
                                    FileUtils.bytes2File(
                                        file,
                                        Files.readAllBytes(installerFileSystem!!.getPath(path2)),
                                        false
                                    )
                                } catch (e: Exception) {
                                    println(CMCL.getString("MESSAGE_FAILED_TO_DECOMPRESS_FILE", path2, e))
                                }
                            }
                        } else {
                            LibrariesDownloader.downloadSingleLibrary(library)
                        }
                    }
                }
            }
            run {
                val forgePath: String = installProfile.optString("path")
                if (!Utils.isEmpty(forgePath)) {
                    val nameSplit = VersionLibraryUtils.splitLibraryName(forgePath)
                    if (nameSplit != null) {
                        val fileName: String = nameSplit.fileName
                        val path: String = Utils.getPathFromLibraryName(nameSplit) + "/" + fileName
                        val file: File = File(CMCL.librariesDir, path)
                        if (file.length() <= 0) {
                            val path2 = "/maven" + if (!path.startsWith("/")) "/$path" else path
                            try {
                                println(CMCL.getString("MESSAGE_UNZIPPING_FILE", path2))
                                FileUtils.createFile(file, true)
                                FileUtils.bytes2File(
                                    file,
                                    Files.readAllBytes(installerFileSystem!!.getPath(path2)),
                                    false
                                )
                            } catch (e: Exception) {
                                println(CMCL.getString("MESSAGE_FAILED_TO_DECOMPRESS_FILE", path2, e))
                            }
                        }
                    }
                }
            }
            val data: MutableMap<String, String> = HashMap()
            val temp = File(CMCL.CMCLWorkingDirectory, "forge" + System.currentTimeMillis())
            temp.mkdirs()
            val dataJSON: JSONObject = installProfile.optJSONObject("data")
            for ((key, value) in dataJSON.toMap().entries) {
                if (value is Map<*, *>) {
                    val clientObject = value["client"] as? String ?: continue
                    if (!Utils.isEmpty(clientObject)) {
                        if (clientObject[0] == '[' && clientObject[clientObject.length - 1] == ']') { //Artifact
                            val inside = clientObject.substring(1, clientObject.length - 1)
                            val nameSplit = SplitLibraryName.valueOf(inside) ?: continue
                            val libraryFile: File = nameSplit.physicalFile
                            data[key] = libraryFile.absolutePath
                        } else if (clientObject[0] == '\'' && clientObject[clientObject.length - 1] == '\'') { //Literal
                            val inside = clientObject.substring(1, clientObject.length - 1)
                            data[key] = inside
                        } else {
                            val target = File(temp, clientObject)
                            try {
                                if (target.exists()) target.delete()
                                FileUtils.bytes2File(
                                    target,
                                    Files.readAllBytes(installerFileSystem!!.getPath(clientObject)),
                                    false
                                )
                            } catch (e: Exception) {
                                println(CMCL.getString("MESSAGE_FAILED_TO_DECOMPRESS_FILE", clientObject, e))
                            }
                            data[key] = target.absolutePath
                        }
                    }
                }
            }
            data["SIDE"] = "client"
            data["MINECRAFT_JAR"] = minecraftJarFile.absolutePath
            data["MINECRAFT_VERSION"] = installProfile.optString("minecraft")
            data["ROOT"] = CMCL.gameDir.absolutePath
            data["INSTALLER"] = installer.absolutePath
            data["LIBRARY_DIR"] = CMCL.librariesDir.absolutePath
            val processorsJSON: JSONArray = installProfile.optJSONArray("processors")
            val processors: List<JSONObject> = JSONUtils.jsonArrayToJSONObjectList(processorsJSON) { jsonObject ->
                val sides: JSONArray? = jsonObject.optJSONArray("sides")
                var contains = false
                if (sides != null && sides.length() > 0) {
                    for (side in sides) {
                        if ("client" == side) {
                            contains = true
                            break
                        }
                    }
                } else {
                    contains = true
                }
                contains
            }
            for (processor in processors) {
                val splitLibraryName: SplitLibraryName = SplitLibraryName.valueOf(processor.optString("jar"))
                    ?: continue
                val processorJarPhysicalFile: File = splitLibraryName.physicalFile
                if (!processorJarPhysicalFile.exists() || !processorJarPhysicalFile.isFile()) {
                    continue
                }
                try {
                    JarFile(processorJarPhysicalFile).use { jarFile ->
                        val mainClass = jarFile.manifest.mainAttributes.getValue(Attributes.Name.MAIN_CLASS)
                        val classpath: MutableList<URL> = ArrayList()
                        classpath.add(processorJarPhysicalFile.toURI().toURL())
                        for (o in processor.optJSONArray("classpath")) {
                            if (o is String) {
                                val splitLibraryName1 = SplitLibraryName.valueOf(o)
                                if (splitLibraryName1 != null) {
                                    classpath.add(splitLibraryName1.physicalFile.toURI().toURL())
                                }
                            }
                        }
                        val args: MutableList<String> = ArrayList()
                        for (arg in processor.optJSONArray("args")) {
                            if (arg is String) {
                                val start = arg[0]
                                val end = arg[arg.length - 1]
                                if (start == '[' && end == ']') {
                                    val splitLibraryName1: SplitLibraryName =
                                        SplitLibraryName.valueOf(arg.substring(1, arg.length - 1))
                                            ?: continue
                                    args.add(splitLibraryName1.physicalFile.absolutePath)
                                } else {
                                    args.add(replaceTokens(data, arg))
                                }
                            }
                        }
                        val cl: ClassLoader = URLClassLoader(classpath.toTypedArray<URL>(), parentClassloader)
                        // Set the thread context classloader to be our newly constructed one so that service loaders work
                        val currentThread = Thread.currentThread()
                        val threadClassloader = currentThread.getContextClassLoader()
                        currentThread.setContextClassLoader(cl)
                        try {
                            val cls = Class.forName(mainClass, true, cl)
                            val main = cls.getDeclaredMethod("main", Array<String>::class.java)
                            main.invoke(null, args.toTypedArray<String>() as Any)
                        } catch (e: InvocationTargetException) {
                            e.cause?.printStackTrace()
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        } finally {
                            // Set back to the previous classloader
                            currentThread.setContextClassLoader(threadClassloader)
                        }
                    }
                } catch (e: Exception) {
                    println(CMCL.getString("MESSAGE_INSTALL_FORGE_FAILED_EXECUTE_PROCESSOR", e))
                }
            }
            Utils.close(zipFile)
            installer.delete()
            FileUtils.deleteDirectory(temp)
        } else if (installProfile.optJSONObject("install") != null && installProfile.optJSONObject("versionInfo") != null) {
            ///old
            if (installProfile.optJSONObject("install", JSONObject()).optString("minecraft") != minecraftVersion) {
                throw ExceptionWithDescription(
                    CMCL.getString(
                        "INSTALL_MODLOADER_FAILED_MC_VERSION_MISMATCH",
                        MODLOADER_NAME
                    )
                )
            }
            version = installProfile.optJSONObject("versionInfo")!!
            val installJSONObject: JSONObject = installProfile.optJSONObject("install")
            val path: String = installJSONObject.optString("path")
            val filePath: String = installJSONObject.optString("filePath")
            val nameSplit = VersionLibraryUtils.splitLibraryName(path)
            val libraryFile: File = nameSplit!!.physicalFile
            try {
                FileUtils.inputStream2File(zipFile.getInputStream(zipFile.getEntry(filePath)), libraryFile)
                zipFile.close()
                installer.delete()
            } catch (e: IOException) {
                throw ExceptionWithDescription(
                    CMCL.getString(
                        "INSTALL_MODLOADER_FAILED_WITH_REASON",
                        MODLOADER_NAME,
                        CMCL.getString("MESSAGE_FAILED_TO_DECOMPRESS_FILE", filePath, e)
                    )
                )
            }
        } else {
            throw ExceptionWithDescription(CMCL.getString("INSTALL_MODLOADER_FAILED_UNKNOWN_TYPE", MODLOADER_NAME))
        }
        val forgeLibraries: JSONArray? = version.optJSONArray("libraries")
        librariesNeedToInstall = LinkedList<JSONObject>()
        if (forgeLibraries != null) {
            for (j in forgeLibraries) {
                if (j is JSONObject) {
                    val name: String = j.optString("name")
                    if (!name.startsWith("net.minecraftforge:forge:")) {
                        val nameSplit2: SplitLibraryName? = VersionLibraryUtils.splitLibraryName(name)
                        if (nameSplit2 != null) {
                            val file: File = nameSplit2.physicalFile
                            if (!file.exists() && file.length() == 0L) librariesNeedToInstall.add(j)
                        }
                    }
                }
            }
            val mcLibraries: JSONArray? = headJSONObject.optJSONArray("libraries")
            headJSONObject.put(
                "libraries",
                VersionLibraryUtils.mergeLibraries(
                    JSONUtils.jsonArrayToJSONObjectList(mcLibraries),
                    JSONUtils.jsonArrayToJSONObjectList(forgeLibraries)
                )
            )
        }
        val mainClass: String = version.optString("mainClass")
        if (!Utils.isEmpty(mainClass)) headJSONObject.put("mainClass", mainClass)

        val forgeInHead = JSONObject()
        forgeInHead.put("version", forgeVersion)
        forgeInHead.put("jarUrl", finalDownload)
        headJSONObject.put("forge", forgeInHead)

        var minecraftArguments: String? = version.optString("minecraftArguments")
        val arguments: JSONObject? = version.optJSONObject("arguments")
        if (!Utils.isEmpty(minecraftArguments)) {
            val hmca: String = headJSONObject.optString("minecraftArguments")
            if (hmca.isEmpty()) headJSONObject.put("minecraftArguments", minecraftArguments) else {
                val arguments1 = Arguments(hmca, false)
                val arguments2 = Arguments(minecraftArguments, false)
                arguments1.merge(arguments2)
                headJSONObject.put("minecraftArguments", arguments1.toString("--").also { minecraftArguments = it })
            }
        }
        if (arguments != null) {
            val argumentsMC: JSONObject? = headJSONObject.optJSONObject("arguments")
            if (argumentsMC != null) {
                var gameMC: JSONArray? = argumentsMC.optJSONArray("game")
                var jvmMC: JSONArray? = argumentsMC.optJSONArray("jvm")
                val game: JSONArray? = arguments.optJSONArray("game")
                if (game != null && game.length() > 0) {
                    if (gameMC == null) argumentsMC.put("game", JSONArray().also { gameMC = it })
                    gameMC!!.putAll(game)
                }
                val jvm: JSONArray? = arguments.optJSONArray("jvm")
                if (jvm != null && jvm.length() > 0) {
                    if (jvmMC == null) argumentsMC.put("jvm", JSONArray().also { jvmMC = it })
                    jvmMC!!.putAll(jvm)
                }
            } else {
                headJSONObject.put("arguments", arguments)
            }
        }
        return Pair(true, librariesNeedToInstall)
    }

    @Throws(IOException::class)
    private fun listForgeLoaderVersions(minecraftVersion: String): JSONArray {
        return JSONArray(NetworkUtils[DownloadSource.getProvider().forge() + "minecraft/" + minecraftVersion])
    }

    /**
     * @from ForgeInstaller
     */
    fun replaceTokens(tokens: Map<String, String>, value: String): String {
        val buf = StringBuilder()
        var x = 0
        while (x < value.length) {
            val c = value[x]
            if (c == '\\') {
                require(x != value.length - 1) { "Illegal pattern (Bad escape): $value" }
                buf.append(value[++x])
            } else if (c == '{' || c == '\'') {
                val key = StringBuilder()
                var y = x + 1
                while (y <= value.length) {
                    require(y != value.length) { "Illegal pattern (Unclosed $c): $value" }
                    val d = value[y]
                    if (d == '\\') {
                        require(y != value.length - 1) { "Illegal pattern (Bad escape): $value" }
                        key.append(value[++y])
                    } else if (c == '{' && d == '}') {
                        x = y
                        break
                    } else if (c == '\'' && d == '\'') {
                        x = y
                        break
                    } else key.append(d)
                    y++
                }
                if (c == '\'') buf.append(key) else {
                    require(tokens.containsKey(key.toString())) { "Illegal pattern: $value Missing Key: $key" }
                    buf.append(tokens[key.toString()])
                }
            } else {
                buf.append(c)
            }
            x++
        }
        return buf.toString()
    }

    /**
     * @from ForgeInstaller
     */
    @get:Synchronized
    val parentClassloader: ClassLoader? by lazy { //Reflectively try and get the platform classloader, done this way to prevent hard dep on J9.
        if (!System.getProperty("java.version")
                .startsWith("1.")
        ) { //in 9+ the changed from 1.8 to just 9. So this essentially detects if we're <9
            try {
                val getPlatform = ClassLoader::class.java.getDeclaredMethod("getPlatformClassLoader")
                return@lazy getPlatform.invoke(null) as ClassLoader
            } catch (ignore: Exception) {
            }
        }
        null

    }
}
