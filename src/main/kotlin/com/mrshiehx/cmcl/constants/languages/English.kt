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
package com.mrshiehx.cmcl.constants.languages

import com.mrshiehx.cmcl.constants.Constants

object English : Language {
    //调用到getTextMap()或getHelpMap()再创建Map然后put，不要在初始化就put，
    //因为有些操作不需要获取用于向用户显示的文本，就不用花费这个时间了

    override val textMap: Map<String, String> by lazy {
        if (Constants.isDebug()) println("init english textMap start")
        val en: MutableMap<String, String> = HashMap()
        en["APPLICATION_NAME"] = "Console Minecraft Launcher (Kotlin)"
        en["MESSAGE_ABOUT_DESCRIPTION_1"] = "Console Minecraft Launcher (Kotlin) v%1\$s"
        en["MESSAGE_ABOUT_DESCRIPTION_2"] = "A Command-line Minecraft Java Edition Launcher written in Kotlin"
        en["MESSAGE_ABOUT_DESCRIPTION_4"] = "Source code repository: "
        en["MESSAGE_ABOUT_DESCRIPTION_6"] = "Dependency Libraries: "
        en["MESSAGE_ABOUT_DESCRIPTION_MAIN_DEVELOPERS"] = "Main Developers:"
        en["MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS"] = "Special Thanks: "
        en["MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_AUTHLIB_INJECTOR"] = "authlib-injector support"
        en["MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_BMCLAPI"] = "BMCLAPI download source provider"
        en["MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_MCBBS_NAME"] = "MCBBS....................."
        en["MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_MCBBS"] = "used to provide MCBBS download source"
        en["MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_TITLE"] = "Disclaimer"
        en["MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_CONTENT_1"] =
            "The copyright of Minecraft belongs to Mojang Studios and Microsoft. The software producer is not responsible for any copyright issues arising from the use of CMCL. Please support the official game."
        en["MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_CONTENT_2"] =
            "All consequences arising from the use of CMCL by the user shall be borne by the user himself. Any legal disputes and conflicts involving CMCL have nothing to do with the developer, and CMCL and the developer will not bear any responsibility."
        en["MESSAGE_OFFICIAL_LOGIN_FAILED_TITLE"] = "Failed to login the official account"
        en["MESSAGE_LOGINED_TITLE"] = "Login into the account successfully"
        en["MESSAGE_DOWNLOAD_SKIN_FILE_NOT_SET_TEXT"] = "You did not set a skin"
        en["MESSAGE_UNABLE_TO_LOGIN_MICROSOFT"] = "Unable to login temporarily, please try again later."
        en["MESSAGE_OFFICIAL_LOGIN_FAILED_MESSAGE"] =
            "Login to your account failed, probably because your account does not have Minecraft."
        en["MESSAGE_FAILED_REFRESH_TITLE"] = "Failed to refresh account"
        en["MESSAGE_STARTING_GAME"] = "Starting game..."
        en["MESSAGE_FINISHED_GAME"] = "Game finished"
        en["MESSAGE_FAILED_TO_CONNECT_TO_URL"] = "Failed to connect to %s, please check your network connection."
        en["MESSAGE_VERSIONS_LIST_IS_EMPTY"] = "The game versions list is empty"
        en["MESSAGE_INSTALL_INPUT_NAME"] = "Please enter a name of the new version: "
        en["MESSAGE_INSTALL_INPUT_NAME_EXISTS"] = "%s: The name already exists, please change a name."
        en["MESSAGE_FAILED_TO_CONTROL_VERSION_JSON_FILE"] =
            "Failed to download or parse the target version of the JSON file: %s"
        en["MESSAGE_INSTALL_NOT_FOUND_JAR_FILE_DOWNLOAD_INFO"] =
            "The download information of the client file in the JSON file of the target version not found."
        en["MESSAGE_INSTALL_JAR_FILE_DOWNLOAD_URL_EMPTY"] = "The download url of the client file is empty."
        en["MESSAGE_FAILED_TO_INSTALL_NEW_VERSION"] = "Failed to install the new version: %s"
        en["MESSAGE_INSTALL_DOWNLOADING_JAR_FILE"] = "Downloading the client file..."
        en["MESSAGE_INSTALL_DOWNLOADED_JAR_FILE"] = "Download the client file complete"
        en["MESSAGE_INSTALL_DOWNLOADING_ASSETS"] = "Downloading the asset files..."
        en["MESSAGE_INSTALL_DOWNLOADED_ASSETS"] = "Download the asset files complete"
        en["MESSAGE_INSTALL_DOWNLOAD_ASSETS_NO_INDEX"] =
            "Failed to download the asset files, the asset files index not found."
        en["MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS"] = "Failed to download the asset files: %s"
        en["MESSAGE_EXCEPTION_DETAIL_NOT_FOUND_URL"] = "Cannot find download url"
        en["MESSAGE_FAILED_DOWNLOAD_FILE"] = "%s: Failed to download the file"
        en["MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON"] = "Failed to download the file \"%s\": %s"
        en["MESSAGE_INSTALL_DOWNLOADING_LIBRARIES"] = "Downloading the library files..."
        en["MESSAGE_INSTALL_DOWNLOADED_LIBRARIES"] = "Download the library files complete"
        en["MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARIES"] = "Failed to download the library files: %s"
        en["MESSAGE_INSTALL_LIBRARIES_LIST_EMPTY"] = "The libraries list is empty"
        en["MESSAGE_FAILED_TO_DECOMPRESS_FILE"] = "Failed to decompress file \"%1\$s\": %2\$s"
        en["MESSAGE_INSTALL_DECOMPRESSING_NATIVE_LIBRARIES"] = "Decompressing the native library files..."
        en["MESSAGE_INSTALL_DECOMPRESSED_NATIVE_LIBRARIES"] = "Decompress the native library files complete"
        en["MESSAGE_FAILED_TO_COPY_FILE"] = "Failed to copy file \"%1\$s\" to \"%2\$s\": %3\$s"
        en["MESSAGE_INSTALLED_NEW_VERSION"] = "Install the new version complete"
        en["MESSAGE_UUID_ACCESSTOKEN_EMPTY"] =
            "UUID or accessToken is empty, you can try to refresh your account or login again."
        en["MESSAGE_DOWNLOADING_FILE"] = "Downloading %s"
        en["MESSAGE_DOWNLOADING_FILE_TO"] = "Downloading %s to %s"
        en["MESSAGE_COPYING_FILE"] = "Copying %s to %s"
        en["MESSAGE_UNZIPPING_FILE"] = "Decompressing %s"
        en["MESSAGE_GAME_CRASH_CAUSE_TIPS"] = "Game crash possible error: %s"
        en["MESSAGE_GAME_CRASH_CAUSE_URLCLASSLOADER"] =
            "Older versions of Minecraft may have this error because the Java version is too high, Java 8 and below can be used to fix this.\nYou can use \"version <version> --config=javaPath <Java path>\" to set the Java path for the version separately."
        en["MESSAGE_GAME_CRASH_CAUSE_LWJGL_FAILED_LOAD"] =
            "This error may occur because some of the native dependency library are missing or damaged, please re-download native dependency libraries via \"version <version> --complete=natives\" to fix this problem."
        en["MESSAGE_GAME_CRASH_CAUSE_MEMORY_TOO_SMALL"] =
            "The memory is not enough, you can try to adjust the memory to a larger number."
        en["MESSAGE_REDOWNLOADED_NATIVES"] = "Download native dependency libraries complete"
        en["MESSAGE_FAILED_SEARCH"] = "Failed to search: %s"
        en["MESSAGE_FAILED_RENAME_VERSION"] = "Failed to rename the version: %s"
        en["MESSAGE_START_INSTALLING_FORGE"] = "Start installing Forge"
        en["MESSAGE_INSTALLED_FORGE"] = "Forge installed successfully"
        en["MESSAGE_NOT_FOUND_LIBRARY_DOWNLOAD_URL"] = "Could not find the download URL of the dependency library %s"
        en["MESSAGE_INSTALL_NATIVES_EMPTY_JAR"] = "No native dependency library files that need to be decompressed"
        en["MESSAGE_INSTALL_FORGE_FAILED_EXECUTE_PROCESSOR"] = "Failed to execute processor: %s"
        en["MESSAGE_YGGDRASIL_LOGIN_SELECT_PROFILE"] = "Please choose a character (%d-%d): "
        en["MESSAGE_INPUT_VERSION_NAME"] = "Please enter the version name to store as: "
        en["MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON_WITH_URL"] =
            "Failed to download the file: %s, the file link is: %s, you can download and store it in %s by yourself"
        en["MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON_WITH_URL_WITH_NAME"] =
            "Failed to download the file: %s, the file link is: %s, you can download and store it in %s by yourself, and change the name to \"%s\""
        en["MESSAGE_START_INSTALLING_LITELOADER"] = "Start installing LiteLoader"
        en["MESSAGE_INSTALLED_LITELOADER"] = "LiteLoader installed successfully"
        en["MESSAGE_START_INSTALLING_OPTIFINE"] = "Start installing OptiFine"
        en["MESSAGE_INSTALLED_OPTIFINE"] = "OptiFine installed successfully"
        en["MESSAGE_INSTALL_MODPACK_UNKNOWN_TYPE"] = "Unable to install the modpack: Unknown modpack type."
        en["MESSAGE_INSTALL_MODPACK_NOT_FOUND_GAME_VERSION"] =
            "Failed to install the modpack: Could not find the version of the game to install."
        en["MESSAGE_INSTALL_MODPACK_COEXIST"] =
            "Failed to install the modpack: %1\$s and %2\$s cannot be installed at the same time."
        en["MESSAGE_INSTALL_MODPACK_NOT_SUPPORTED_NEOFORGE"] =
            "Sorry, this launcher does not support the installation of NeoForge at the moment, so this modpack cannot be installed."
        en["MESSAGE_COMPLETE_VERSION_IS_COMPLETE"] =
            "This version is complete and does not need to be completed. If the version is indeed incomplete after checking, please reinstall the version."
        en["MESSAGE_COMPLETED_VERSION"] = "Version completed successfully"
        en["MESSAGE_SELECT_DOWNLOAD_SOURCE"] =
            "Please select the download source for the first download (default is %d, stored as configuration \"downloadSource\"): "
        en["MESSAGE_SELECT_ACCOUNT"] = "Please enter the order number of the account you want to select (%d-%d): "
        en["MESSAGE_SELECT_ACCOUNT_TYPE"] =
            "No account is currently available, please select the account type of the new account (%d-%d): "
        en["MESSAGE_FAILED_TO_CHECK_FOR_UPDATES"] = "Failed to check for updates"
        en["MESSAGE_NEW_VERSION"] = "New Version: %s\nUpdated Date: %s\nDownload urls:\n%sUpdated Content:\n%s"
        en["MESSAGE_CURRENT_IS_LATEST_VERSION"] = "The current version is the latest version"
        en["MESSAGE_BEFORE_LIST_VERSIONS"] = "Game versions in directory %s: "
        en["MESSAGE_AUTHLIB_ACCOUNT_INCOMPLETE"] =
            "The authlib-injector account is incomplete, please delete it and login again."
        en["MESSAGE_NIDE8AUTH_ACCOUNT_INCOMPLETE"] =
            "The nide8auth account is incomplete, please delete it and login again."
        en["MESSAGE_ACCOUNT_FAILED_TO_VALIDATE"] = "Failed to validate account: %s"
        en["MESSAGE_ACCOUNT_INFO_EXPIRED_NEED_RELOGIN"] = "The information has expired, please login again."
        en["MESSAGE_ACCOUNT_INFO_MISSING_NEED_RELOGIN"] = "The information has missing, please login again."
        en["MESSAGE_AUTHLIB_ACCOUNT_REFRESH_NO_CHARACTERS"] =
            "Your character has been deleted, and there is no available character, please go to the authlib-injector login website to add a character and refresh or login again. Otherwise, the relevant functions in the game may not be available."
        en["MESSAGE_NIDE8AUTH_ACCOUNT_REFRESH_NO_CHARACTERS"] =
            "Your character has been deleted, and there is no available character, please go to the nide8auth login website to add a character and refresh or login again. Otherwise, the relevant functions in the game may not be available."
        en["MESSAGE_YGGDRASIL_ACCOUNT_REFRESH_OLD_CHARACTER_DELETED"] =
            "Your character has been deleted, please choose a new character."
        en["MESSAGE_GAME_CRASH_CAUSE_JVM_UNRECOGNIZED_OPTION"] =
            "You added wrong JVM arguments. Get information about it through \"jvmArgs --help\"."
        en["MESSAGE_TELL_USER_CHECK_ACCOUNT_CAN_BE_OFF"] =
            "If you don't want to check whether the account is available before startup, you can use \"config checkAccountBeforeStart false\" or \"version <version> --config=checkAccountBeforeStart false\" to turn it off."
        en["MESSAGE_STARTUP_INFO_MAIN"] = """
            Launching Version: ${"$"}{VERSION_NAME} (${"$"}{REAL_VERSION_NAME}) | Login Account: ${"$"}{PLAYER_NAME} (${"$"}{ACCOUNT_TYPE}) | Java Path: ${"$"}{JAVA_PATH}
            Game Exits with Launcher: ${"$"}{EXIT_WITH_MC} | Fullscreen: ${"$"}{FULLSCREEN} | Max Memory: ${"$"}{MAX_MEMORY} | Window Width: ${"$"}{WIDTH} | Window Height: ${"$"}{HEIGHT} | Check account before startup: ${"$"}{CHECK_ACCOUNT_BEFORE_START}
            Game Directory: ${"$"}{GAME_DIR}
            """.trimIndent()
        en["MESSAGE_STARTUP_INFO_ASSETS_DIR"] = "Resource Packs Directory: \${ASSETS_DIR}"
        en["MESSAGE_STARTUP_INFO_RESOURCE_PACKS_DIR"] = "Assets Directory: \${RESOURCE_PACKS_DIR}"
        en["MESSAGE_STARTUP_INFO_ARGS"] = """
            Custom JVM Arguments:
            ${"$"}{JVM_ARGS}
            Custom Game Arguments:
            ${"$"}{GAME_ARGS}
            """.trimIndent()
        en["MESSAGE_STARTUP_INFO_QUICK_PLAY_LOG_FILE_PATH"] = "Quick Play Log File Path: \${QUICK_PLAY_LOG_FILE_PATH}"
        en["MESSAGE_STARTUP_INFO_QUICK_PLAY_SAVE_NAME"] = "Quick Play Save Name: \${QUICK_PLAY_SAVE_NAME}"
        en["MESSAGE_STARTUP_INFO_QUICK_PLAY_SERVER_ADDRESS"] =
            "Quick Play Server Address: \${QUICK_PLAY_SERVER_ADDRESS}"
        en["MESSAGE_STARTUP_INFO_QUICK_PLAY_REALMS_ID"] = "Quick Play Realms ID: \${QUICK_PLAY_REALMS_ID}"
        en["MESSAGE_TO_SELECT_VERSION"] =
            "Please use \"-s <version>\" to select a launch-able version or \"install <version>\" to install a new version and select it."
        en["MESSAGE_PRINT_COMMAND_EXCEEDS_LENGTH_LIMIT"] =
            "Tip: The startup command is too long, you may not be able to run it directly in cmd or save it to a bat file and execute it. It is recommended that you use \"version [<version>] --export-script-ps=<script file>\" to export it as a PowerShell script file used later."
        en["MESSAGE_EXPORT_COMMAND_EXCEEDS_LENGTH_LIMIT"] =
            "Unable to export the script file: Due to the length limit of the bat script file, the startup command is too long and cannot be exported as a bat file. You can only use \"version [<version>] --export-script-ps=<script file with .ps1 as the suffix>\" to export as a PowerShell script file."
        en["MESSAGE_CONFIGURATIONS_TIP"] =
            "Note: For configurations whose type is Boolean, its value can be \"true\" which means \"yes\", or \"false\" which means \"no\"."
        en["MESSAGE_CONFIGURATIONS_TABLE_CONTENT"] =
            """accounts|JSON Array|Accounts (Non-direct modification, please use "account -h" to get the relevant tutorial for modification)
downloadSource|Integer|Download source, 0 is the official, 1 is BMCLAPI
language|Text|Language, zh is Simplified Chinese, en is English and cantonese is Cantonese (Simplified)
selectedVersion|Text|Selected start version, you can directly use "cmcl" to start it
maxMemory|Integer|[Game related] Maximum (Unit: MB)
gameDir|Text|[Game related] Custom the path of the game directory (or set working directory), default is .minecraft
assetsDir|Text|[Game related] Custom assets resource directory path, if empty, it is the assets directory in the game directory
resourcesDir|Text|[Game related] Custom resource pack directory path, if empty, it is the resourcepacks directory in the game directory
javaPath|Text|[Game related] Java Path (It will get automatically if it is empty)
windowSizeWidth|Integer|[Game related] The width of the game window
windowSizeHeight|Integer|[Game related] The height of the game window
isFullscreen|Boolean|[Game related] Whether the game window is fullscreen or not
exitWithMinecraft|Boolean|[Game related] When running the game, whether or not you need to exit the launcher and exit the game by the way
printStartupInfo|Boolean|[Game related] When starting the game, whether to output startup information (Java path, maximum memory, etc.)
checkAccountBeforeStart|Boolean|[Game related] Check whether the account is available before starting the game
jvmArgs|JSON Array|[Game related] Customize JVM arguments (Use "jvmArgs -h" to get the relevant tutorial for modification)
gameArgs|JSON Object|[Game related] Customize game arguments (Use "gameArgs -h" to get the relevant tutorial for modification)
qpLogFile|Text|[Game related] The log file path (relative to the game directory) of Quick Play (a new feature of Minecraft 1.20, set the following three configurations to start the game and directly enter the save, server or realms, only one item can be set), optional
qpSaveName|Text|[Game related] The name of the save that the quick play will join directly
qpServerAddress|Text|[Game related] The address (including port) of the server that the quick play will join directly, this configuration also applies to versions prior to 1.20
qpRealmsID|Text|[Game related] The ID of the realms that the quick play will join directly
proxyEnabled|Boolean|Whether to enable network proxy
proxyHost|Text|Proxy Host Address
proxyPort|Integer|Proxy Port
proxyUsername|Text|Proxy authentication username(optional for proxy)
proxyPassword|Text|Proxy authentication password(optional for proxy)
modDownloadSource|Text|Mod download source, curseforge or modrinth
modpackDownloadSource|Text|Modpack download source, curseforge or modrinth
simplifyCommands|JSON Object|Simplify commands (use "simplify -h" to get the relevant tutorial for modification)"""
        en["ERROR_WITH_MESSAGE"] = "Error: %1\$s\nError Message: %2\$s"
        en["EXCEPTION_VERSION_JSON_NOT_FOUND"] =
            "The JSON file or JAR file of the target version does not exist, please use \"-s <version>\" to select a launch-able version or \"install <version>\" to install a new version and select it."
        en["EXCEPTION_VERSION_NOT_FOUND"] = "%s: Version does not exist"
        en["EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND"] =
            "Cannot find the native libraries directory or it is empty, you can re-download the native library files via \"version <version> --complete=natives\" to start game."
        en["EXCEPTION_MAX_MEMORY_TOO_BIG"] = "The maximum memory is larger than the total physical memory size"
        en["EXCEPTION_MAX_MEMORY_MUST_BE_GREATER_THAN_ZERO"] = "Maximum memory must be greater than zero"
        en["EXCEPTION_JAVA_VERSION_TOO_LOW"] =
            "The minimum Java version required for this version of Minecraft is %d, the Java version you have selected is %d, please select a Java that meets the requirements and try again."
        en["EXCEPTION_WINDOW_SIZE_MUST_BE_GREATER_THAN_ZERO"] =
            "The width and height of the game window must be greater than zero"
        en["EXCEPTION_JAVA_NOT_FOUND"] = "Unable to launch game: the java file not found"
        en["EXCEPTION_READ_FILE"] = "Failed to read file"
        en["EXCEPTION_READ_FILE_WITH_PATH"] = "%s: Failed to read the file"
        en["EXCEPTION_PARSE_FILE"] = "Failed to parse file"
        en["EXCEPTION_PARSE_FILE_WITH_PATH"] = "Failed to parse the file \"%s\""
        en["EXCEPTION_WRITE_FILE"] = "Failed to write content to file"
        en["EXCEPTION_WRITE_FILE_WITH_PATH"] = "Failed to write content to the file \"%s\""
        en["EXCEPTION_UNABLE_PARSE"] = "Failed to parse"
        en["EXCEPTION_INSTALL_MODPACK"] = "Failed to install modpack: %s"
        en["EXCEPTION_EXECUTE_COMMAND"] = "Failed to execute the command"
        en["EXCEPTION_INCOMPLETE_VERSION"] =
            "This version is incomplete, please use \"version <version> --complete\" to complete the version before starting."
        en["EXCEPTION_NOT_FOUND_DOWNLOAD_LINK"] = "File download link not found."
        en["EXCEPTION_NOT_FOUND_DOWNLOAD_LINK_WITH_FILENAME"] =
            "The download url for the file \"%s\" could not be found."
        en["EXCEPTION_VERSION_JAR_NOT_FOUND"] =
            "The jar file of target version does not exist, please re-install this version."
        en["EXCEPTION_CREATE_FILE"] = "Failed to create file"
        en["EXCEPTION_CREATE_FILE_WITH_PATH"] = "%s: Failed to create file"
        en["EXCEPTION_OF_NETWORK_WITH_URL"] = "Network error while accessing %1\$s: %2\$s"
        en["EXCEPTION_NIDE8AUTH_JAVA_VERSION_TOO_LOW"] =
            "Unable to use nide8auth because the Java version is less than 8u101, please replace it with a Java that meets the requirements and try again."
        en["EXCEPTION_GET_USER_PROPERTIES"] = "Failed to get user profile: %s"
        en["EXCEPTION_SAVE_CONFIG"] = "Failed to save configuration: %s"
        en["EXCEPTION_READ_CONFIG_FILE"] =
            "Failed to read the configuration file, please make sure the configuration file (cmcl.json) is readable and the content is correct: %s"
        en["EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"] =
            "Network error: If it is not the problem of the target website, there may be a problem with your proxy, please check if your network proxy is available!"
        en["ON_AUTHENTICATED_PAGE_TEXT"] =
            "Microsoft account authorization has been completed. Please close this page and back to the launcher to complete login."
        en["WEB_TITLE_LOGIN_MICROSOFT_ACCOUNT_RESPONSE"] = "Login Microsoft Account - Console Minecraft Launcher"
        en["CONSOLE_UNSUPPORTED_VALUE"] = "Unsupported value: %s"
        en["CONSOLE_LOGIN_MICROSOFT_WAIT_FOR_RESPONSE"] =
            "Please login your Microsoft account in the browser, if there is no automatic redirect,\nplease manually copy the link to your browser:\n%s\nIf the login is successful, back to launcher and wait for the login to complete.\nIt will take some time to login, please be patient."
        en["CONSOLE_FAILED_REFRESH_OFFICIAL_NO_RESPONSE"] = "Server not responding"
        en["CONSOLE_FAILED_OPERATE"] = "Failed to operate: "
        en["CONSOLE_FILE_EXISTS"] = "The file \"%s\" already exists"
        en["CONSOLE_INCORRECT_JAVA"] =
            "Please modify a correct Java path by \"config javaPath <Java Path>\" or \"version <version> --config=javaPath <Java path>\""
        en["CONSOLE_FAILED_START"] = "Unable to start game"
        en["CONSOLE_START_COMMAND"] = "Launch Command: "
        en["CONSOLE_NO_SELECTED_VERSION"] =
            "Please use \"-s <version>\" to select a version to start, or start via \"cmcl <version>\" with the version name."
        en["CONSOLE_EMPTY_LIST"] = "The list is empty"
        en["CONSOLE_LACK_LIBRARIES_WHETHER_DOWNLOAD"] =
            "You are missing the above necessary dependent libraries to start the game. Do you want to download them?"
        en["CONSOLE_FAILED_LIST_VERSIONS"] = "Failed to get the versions list: %s"
        en["CONSOLE_INSTALL_SHOW_INCORRECT_TIME"] =
            "%s: Incorrect time format or the first time is bigger than the second time."
        en["CONSOLE_REPLACE_LOGGED_ACCOUNT"] =
            "You have already logged in to this account (order number is %d). Do you want to overwrite the original account?"
        en["CONSOLE_ACCOUNT_UN_OPERABLE_NEED_UUID_AND_URL_AND_TOKEN"] =
            "If it is an authlib-injector account, you must have the UUID, accessToken and the address of the authlib-injector server to perform this operation, you can try to refresh your account by \"account --refresh\" or login again."
        en["CONSOLE_ACCOUNT_UN_OPERABLE_MISSING_INFO"] =
            "You must be logged in with an official account, an authlib-injector account or a nide8auth account and have a UUID to perform this operation. If it is an authlib-injector account, the target server address is also required. If it is a nide8auth account, the target server ID is also required. You can try to refresh your account by \"account --refresh\" or login again."
        en["CONSOLE_INPUT_INT_WRONG"] = "Please enter a correct number within range. "
        en["CONSOLE_INPUT_STRING_NOT_FOUND"] = "Not found \"%s\". "
        en["CONSOLE_ONLY_HELP"] = "Please use the option -h or --help to get the help documentation."
        en["CONSOLE_IMMERSIVE_WRONG"] =
            "Incorrect command: %s. Please type help to get help documentation and read the text related to immersive mode carefully."
        en["CONSOLE_IMMERSIVE_NOT_FOUND"] = "%s: Command not found. Please type help to get the help documentation."
        en["CONSOLE_UNKNOWN_COMMAND_OR_MEANING"] =
            "%s: Unknown command or unknown meaning. Please use the option -h or --help to get the help documentation."
        en["CONSOLE_IMMERSIVE_MISSING_PARAMETER"] = "Missing parameter. Type help for help documentation."
        en["CONSOLE_NOT_FOUND_VERSION_OR_OPTION"] =
            "Could not find a start-able version or option with the name \"%s\". You can check the information you entered with the help documentation obtained by typing the option -h or --help."
        en["CONSOLE_HELP_WRONG_WRITE"] =
            "The correct format is -h or --help, without parameter value, instead of \"%s\"."
        en["CONSOLE_UNKNOWN_USAGE"] =
            "Unknown usage: %s. Please use the option -h or --help to get the help documentation."
        en["CONSOLE_ARG_CHECKING_ONE"] =
            "%s: Option usage is wrong or should not appear here. Please use the option -h or --help to get the help documentation."
        en["CONSOLE_ARG_CHECKING_PLURAL"] =
            "The following options are used incorrectly or should not be appear here. Please use the option -h or --help to get the help documentation.\n%s"
        en["CONSOLE_ASK_EXIT_WITH_MC"] =
            "Do you need to exit the game when exiting the launcher (can be turned on or off through \"config exitWithMinecraft true/false\")?"
        en["CONSOLE_ASK_PRINT_STARTUP_INFO"] =
            "Do you need to print startup information when starting the game (such as Java path, maximum memory, login account, etc., which can be turned on or off through \"config printStartupInfo true/false\")?"
        en["CONSOLE_ASK_CHECK_ACCOUNT"] =
            "Do you need to check whether the account is available before starting the game (it will take time before starting, you can turn it on or off through \"config checkAccountBeforeStart true/false\")?"
        en["CONSOLE_CHOOSE_DOWNLOAD_SOURCE_CF_OR_MR"] =
            "Please choose a download source (%d by default, stored as configuration \"modDownloadSource\"): "
        en["DATATYPE_STRING"] = "Text"
        en["DATATYPE_INTEGER"] = "Integer"
        en["DATATYPE_BOOLEAN"] = "Boolean"
        en["DATATYPE_FRACTION"] = "Fraction"
        en["TIME_FORMAT"] = "EEE, MMM d, yyyy HH:mm:ss"
        en["ACCOUNT_TYPE_MICROSOFT"] = "Microsoft Account"
        en["ACCOUNT_TYPE_OFFLINE"] = "Offline Account"
        en["ACCOUNT_TYPE_OAS"] = "authlib-injector Account"
        en["ACCOUNT_TYPE_NIDE8AUTH"] = "Nide8Auth Account"
        en["ACCOUNT_TYPE_NIDE8AUTH_WITH_DETAIL"] = "Nide8Auth Account: %s %s"
        en["ACCOUNT_NOT_EXISTS"] = "Account does not exist: %d"
        en["ACCOUNT_TYPE_OAS_WITH_DETAIL"] = "authlib-injector Account: %s %s"
        en["ACCOUNT_INVALID"] = "Invalid Account: %d"
        en["ACCOUNT_TIP_LOGIN_OFFLINE_PLAYERNAME"] = "Please enter the offline login player name: "
        en["ACCOUNT_TIP_LOGIN_OAS_ADDRESS"] = "Please enter the authlib-injector login server address: "
        en["ACCOUNT_TIP_LOGIN_NIDE8AUTH_SERVER_ID"] = "Please enter the nide8auth login server ID: "
        en["ACCOUNT_LOGIN_UNKNOWN_LOGIN_METHOD"] =
            "Unknown login method: %s. Please use the option -h or --help to get the help documentation."
        en["ACCOUNT_LOGIN_NEED_NAME"] = "Please specify -n<player name> or --name=<player name>."
        en["ACCOUNT_LOGIN_NEED_ADDRESS"] = "Please specify --address=<server address>."
        en["ACCOUNT_LOGIN_NEED_SERVER_ID"] = "Please specify --serverId=<server ID>."
        en["ACCOUNT_MICROSOFT_REFRESH_NOT_SAME"] =
            "It seems that the account you login on the website is not the account you login locally."
        en["NOT_SELECTED_AN_ACCOUNT"] =
            "No account selected. Please log in to your account, use \"account -l\" to list the accounts, remember the order number of the account you want to select, and then use \"account -s<Order Number>\" to select the account; Or add \"-s\" option after login account command."
        en["DATATYPE_JSON_ARRAY"] = "JSON Array"
        en["DATATYPE_JSON_OBJECT"] = "JSON Object"
        en["INPUT_ACCOUNT"] = "Account: "
        en["INPUT_PASSWORD"] = "Password: "
        en["FAILED_TO_LOGIN_OTHER_AUTHENTICATION_ACCOUNT"] = "Failed to login authlib-injector account: %s"
        en["FAILED_TO_LOGIN_YGGDRASIL_ACCOUNT_UNAVAILABLE_SERVER"] = "Target server access failed"
        en["FAILED_TO_LOGIN_OAA_NO_SELECTED_CHARACTER"] =
            "Login failed, please select an available character and try again."
        en["FAILED_TO_LOGIN_NIDE8AUTH_ACCOUNT"] = "Failed to login nide8auth account: %s"
        en["WARNING_SHOWING_PASSWORD"] = "Warning: Do this from a non-console and your password will not be hidden!"
        en["WARNING_VCFG_JAVA_INCORRECT"] =
            "Warning: The Java path of the standalone version configuration does not exist or is invalid, the global configuration value will be used by default!"
        en["WARNING_VCFG_MAX_MEMORY_INCORRECT"] =
            "Warning: The maximum memory of the standalone version configuration is less than or equal to zero, the global configuration value will be used by default!"
        en["WARNING_VCFG_WINDOW_SIZE_WIDTH_INCORRECT"] =
            "Warning: The game window width of the standalone version configuration is less than or equal to zero, the global configuration value will be used by default!"
        en["WARNING_VCFG_WINDOW_SIZE_HEIGHT_INCORRECT"] =
            "Warning: The game window height of the standalone version configuration is less than or equal to zero, the global configuration value will be used by default!"
        en["FILE_NOT_FOUND_OR_IS_A_DIRECTORY"] = "Target file not found or target file is a directory"
        en["SUCCESSFULLY_SET_SKIN"] = "Set skin successfully"
        en["UNAVAILABLE_AUTHLIB_ACCOUNT_REASON"] = "authlib-injector is misconfigured: %s"
        en["UNAVAILABLE_NIDE8AUTH_ACCOUNT_REASON"] = "nide8auth is misconfigured: %s"
        en["UNAVAILABLE_AUTHLIB_ACCOUNT"] =
            "authlib-injector account is not available, the game will use offline account."
        en["UNAVAILABLE_NIDE8AUTH_ACCOUNT"] = "nide8auth account is not available, the game will use offline account."
        en["UNAVAILABLE_CUSTOM_SKIN"] = "Custom skin is not available"
        en["PRINT_COMMAND_NOT_SUPPORT_OFFLINE_CUSTOM_SKIN"] =
            "Note: If you are using an offline account and use the command to start the game, custom skin will not be available."
        en["EMPTY_UUID"] = "UUID is empty"
        en["EMPTY_PLAYERNAME"] = "Player name is empty"
        en["ONLY_OFFLINE"] = "This feature only supports offline accounts"
        en["UPLOAD_SKIN_ONLY_OAS_OR_OFFLINE"] =
            "This function is not available for Microsoft account and nide8auth account."
        en["SKIN_TYPE_DEFAULT_OR_SLIM"] = "Do you want to set the skin model to slim (Alex)?"
        en["SKIN_STEVE_UNABLE_READ"] = "Failed to set, failed to read the Steve skin file!"
        en["SKIN_ALEX_UNABLE_READ"] = "Failed to set, failed to read the Alex skin file!"
        en["SKIN_STEVE_NOT_FOUND"] = "Failed to set, Steve skin file not found!"
        en["SKIN_ALEX_NOT_FOUND"] = "Failed to set, Alex skin file not found!"
        en["SKIN_CANCEL_ONLY_FOR_OFFLINE"] = "Unsetting the skin is only valid for offline accounts."
        en["CAPE_FILE_NOT_FOUND"] = "The cape file \"%s\" not found"
        en["CAPE_FILE_FAILED_LOAD"] = "Failed to load the cape file \"%s\""
        en["UNABLE_TO_START_OFFLINE_SKIN_SERVER"] = "Unable to customize skins with offline account"
        en["UNABLE_OFFLINE_CUSTOM_SKIN_STEVE_NOT_FOUND"] = "Can't use custom skin: Steve skin file not found!"
        en["UNABLE_OFFLINE_CUSTOM_SKIN_ALEX_NOT_FOUND"] = "Can't use custom skin: Alex skin file not found!"
        en["UNABLE_OFFLINE_CUSTOM_SKIN_STEVE_UNABLE_LOAD"] =
            "Can't use custom skin: failed to read the Steve skin file!"
        en["UNABLE_OFFLINE_CUSTOM_SKIN_ALEX_UNABLE_LOAD"] = "Can't use custom skin: failed to read the Alex skin file!"
        en["UNABLE_OFFLINE_CUSTOM_SKIN_FILE_NOT_FOUND"] = "Can't use custom skin: The skin file \"%s\" not found"
        en["UNABLE_OFFLINE_CUSTOM_SKIN_FILE_FAILED_LOAD"] = "Can't use custom skin: Failed to load the skin file \"%s\""
        en["UNABLE_GET_VERSION_INFORMATION"] = "Failed to read version information"
        en["UNABLE_SET_SKIN"] = "Failed to set skin"
        en["INSTALL_MODLOADER_FAILED_TO_GET_INSTALLABLE_VERSION"] =
            "Unable to install %s: Failed to get installable versions."
        en["INSTALL_MODLOADER_NO_INSTALLABLE_VERSION"] =
            "Unable to install %1\$s: There is no installable version of %1\$s, probably because %1\$s does not support this version of the game."
        en["INSTALL_MODLOADER_FAILED_TO_GET_TARGET_JSON"] = "Unable to install %1\$s: Failed to get target %1\$s JSON."
        en["INSTALL_MODLOADER_SELECT"] = "Please enter the version of %1\$s you want to install (default is %2\$s): "
        en["INSTALL_MODLOADER_SELECT_NOT_FOUND"] =
            "Version \"%1\$s\" not found, please enter the version of %2\$s you want to install (default is %3\$s): "
        en["INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE"] =
            "Would you like to continue installing the original version (without %s)?"
        en["INSTALL_MODLOADER_FAILED_TO_PARSE_TARGET_JSON"] =
            "Unable to install %1\$s: Failed to parse target %1\$s JSON."
        en["INSTALL_MODLOADER_ALREADY_INSTALL"] = "Unable to install %1\$s: The target version is installed %1\$s."
        en["INSTALL_MODLOADER_EMPTY_MC_VERSION"] =
            "Unable to install %1\$s: Could not get the target version of the game version."
        en["INSTALL_MODLOADER_FAILED_WITH_REASON"] = "Failed to install %s: %s"
        en["INSTALL_MODLOADER_ALREADY_INSTALL_ANOTHER_ONE"] =
            "Unable to install %1\$s: The target version already has %2\$s installed, %2\$s and %1\$s cannot coexist."
        en["INSTALL_MODLOADER_FAILED_DOWNLOAD"] = "Unable to install %s: download file failed"
        en["INSTALL_MODLOADER_DOWNLOADING_FILE"] = "Downloading file..."
        en["INSTALL_MODLOADER_NO_INSTALLABLE_VERSION_2"] =
            "Unable to install %1\$s: There is no installable version of %1\$s."
        en["INSTALL_MODLOADER_FAILED_UNKNOWN_TYPE"] = "Unable to install %1\$s: Unknown type of %1\$s."
        en["INSTALL_MODLOADER_FAILED_MC_VERSION_MISMATCH"] =
            "Unable to install %1\$s: The game version of the target %1\$s does not match the target game version."
        en["INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION"] = "\${NAME} version \"%s\" not found."
        en["INSTALL_MODLOADER_SELECT_NOT_FOUND_GAME_OR_TARGET_EXTRA"] =
            "Not found target game version or \${NAME} version."
        en["INSTALL_MODPACK_FAILED_DOWNLOAD_MOD"] = "Failed to download the mod with projectId %d: %s"
        en["INSTALL_MODPACK_EACH_MOD_GET_URL"] =
            "Traversing to get the download links of each mod (file), please be patient"
        en["INSTALL_MODPACK_COMPLETE"] = "Install modpack complete"
        en["INSTALL_MODPACK_MODRINTH_UNKNOWN_MODLOADER"] = "Unknown modloader: %s"
        en["INSTALL_OPTIFINE_INCOMPATIBLE_WITH_FORGE_17"] =
            "Unable to install OptiFine: The current game version of Forge is not compatible with OptiFine versions lower than H1 Pre2, please try a newer version of OptiFine."
        en["INSTALL_SHOW_UNKNOWN_TYPE"] =
            "%s: Unknown version type. Please use the option -h or --help to get the help documentation."
        en["INSTALL_COEXIST"] =
            "%1\$s and %2\$s cannot coexist. Please use the option -h or --help to get the help documentation."
        en["INSTALL_FABRIC_API_WITHOUT_FABRIC"] = "How to install Fabric API without installing Fabric?"
        en["INSTALLED_MODLOADER"] = "%s installed successfully"
        en["VERSION_INFORMATION_GAME_VERSION"] = "   Game Version:             "
        en["VERSION_INFORMATION_RELEASE_TIME"] = "   Version Release Time:     "
        en["VERSION_INFORMATION_FABRIC_VERSION"] = "   Fabric Version:           "
        en["VERSION_INFORMATION_FORGE_VERSION"] = "   Forge Version:            "
        en["VERSION_INFORMATION_JAVA_COMPONENT"] = "   Java Component:           "
        en["VERSION_INFORMATION_JAVA_VERSION"] = "   Java Version Requirement: "
        en["VERSION_INFORMATION_ASSETS_VERSION"] = "   Resource Version:         "
        en["VERSION_INFORMATION_LITELOADER_VERSION"] = "   LiteLoader Version:       "
        en["VERSION_INFORMATION_OPTIFINE_VERSION"] = "   OptiFine Version:         "
        en["VERSION_INFORMATION_QUILT_VERSION"] = "   Quilt Version:            "
        en["VERSION_INFORMATION_NEOFORGE_VERSION"] = "   NeoForge Version:         "
        en["VERSION_INFORMATION_VERSION_TYPE"] = "   Version Type:             "
        en["VERSION_INFORMATION_VERSION_TYPE_RELEASE"] = "Release"
        en["VERSION_INFORMATION_VERSION_TYPE_SNAPSHOT"] = "Snapshot"
        en["VERSION_INFORMATION_VERSION_TYPE_OLD_BETA"] = "Old Beta"
        en["VERSION_INFORMATION_VERSION_TYPE_OLD_ALPHA"] = "Old Alpha"
        en["VERSION_INFORMATION_GAME_VERSION_FAILED_GET"] = "Failed to get"
        en["VERSION_INFORMATION_VERSION_PATH"] = "   File Location:            "
        en["VERSION_UNKNOWN_COMPLETING"] = "Unknown completion: %s. Only assets, libraries, natives are supported."
        en["VERSION_COMPLETE_LIBRARIES_NO_NEED_TO"] = "No missing libraries need to be completed."
        en["CF_FAILED_TO_SHOW_SOMEONE"] = "Failed to display \${NAME} %d: %s"
        en["CF_AUTHOR_MORE"] = "and other %d authors"
        en["CF_SELECT_TARGET"] = "Please select the target \${NAME} (%d-%d): "
        en["CF_SUPPORTED_GAME_VERSION"] = "%s supported game versions: "
        en["CF_INPUT_GAME_VERSION"] = "Please enter the version you want to download: "
        en["CF_INPUT_VERSION"] =
            "Please select the \${NAME} version you want to download (%d-%d, cancel download if the value is -1): "
        en["CF_STORAGE_FILE_EXISTS"] = "Please enter a directory to store the \${NAME} file: "
        en["CF_NO_VERSION_FOR_GAME_VERSION"] = "There is no %s version available for this game version."
        en["CF_INFORMATION_NOTHING"] = "There is no information to display about this %s"
        en["CF_INFORMATION_MOD_NAME"] = "   Mod Name:                      "
        en["CF_INFORMATION_MOD_ID"] = "   Mod ID:                        "
        en["CF_INFORMATION_MODPACK_NAME"] = "   Modpack Name:                  "
        en["CF_INFORMATION_MODPACK_ID"] = "   Modpack ID:                    "
        en["CF_INFORMATION_AUTHORS"] = "   Authors:                       "
        en["CF_INFORMATION_AUTHOR"] = "   Author:                        "
        en["CF_INFORMATION_SUMMARY"] = "   Introduction:                  "
        en["CF_INFORMATION_ICON"] = "   Icon:                          "
        en["CF_INFORMATION_LATEST_GAME_VERSION"] = "   Latest Supported Game Version: "
        en["CF_INFORMATION_DATE_MODIFIED"] = "   Modified Date:                 "
        en["CF_INFORMATION_DOWNLOAD_COUNT"] = "   Download Count:                "
        en["CF_INFORMATION_DATE_CREATED"] = "   Created Date:                  "
        en["CF_INFORMATION_DATE_RELEASED"] = "   Released Date:                 "
        en["CF_INFORMATION_ISSUE_TRACKER_URL"] = "   Feedback:                      "
        en["CF_INFORMATION_SOURCE_URL"] = "   Source Code Repository:        "
        en["CF_INFORMATION_WEBSITE_URL"] = "   Webpage Introduction:          "
        en["CF_INFORMATION_WIKI_URL"] = "   Wiki Website:                  "
        en["CF_INFORMATION_CATEGORIES"] = "   Categories:                    "
        en["CF_INFORMATION_DISCORD_URL"] = "   Discord URL:                   "
        en["CF_INFORMATION_DONATION"] = "   Donation:         "
        en["CF_INFORMATION_DONATION_URL"] = "         Url: "
        en["CF_INFORMATION_AUTHOR_URL"] = "         Homepage: "
        en["CF_GET_BY_ID_FAILED"] =
            "Unable to get target \${NAME}: %s\nPossible reasons for this error:\n1. The target \${NAME} does not exist\n2. Network exception\n3. There is a problem with the server"
        en["CF_GET_BY_ID_NOT_OF_MC"] = "The target add-on is not a Minecraft \${NAME}, the game ID of the add-on is %d."
        en["CF_DEPENDENCIES_TIP"] =
            "This \${NAME} requires the following pre-\${NAME}s to work properly, will install the following pre-\${NAME}s first."
        en["CF_DEPENDENCY_INFORMATION_ID"] = "   ID:   %d"
        en["CF_DEPENDENCY_INFORMATION_ID_STRING"] = "   ID:   %s"
        en["CF_DEPENDENCY_INFORMATION_NAME"] = "   Name: %s"
        en["CF_BESEARCHED_MOD_ALC"] = "mod"
        en["CF_BESEARCHED_MOD_FUC"] = "Mod"
        en["CF_BESEARCHED_MODPACK_ALC"] = "modpack"
        en["CF_BESEARCHED_MODPACK_FUC"] = "Modpack"
        en["CF_GET_BY_ID_INCORRECT_CATEGORY"] =
            "The target game component is not a \${NAME}, the category ID of this component is %d."
        en["CF_GET_BY_ID_INCORRECT_CATEGORY_DETAIL"] =
            "The target game component is not a \${NAME}, the component is a \${TARGET}."
        en["CF_STORAGE_FILE_EXISTS_OPERATIONS"] =
            "[0]Overwrite target file  [1]Save to another directory  [2]Cancel download"
        en["CF_STORAGE_FILE_EXISTS_SELECT_OPERATION"] =
            "The file \"%s\" already exists, please choose your action (0-2): "
        en["MOD_FAILED_TO_GET_ALL_FILES"] = "Failed to get list of \${NAME} files: %s"
        en["MOD_UNKNOWN_SOURCE"] = "%s: Unknown download source. Use option -h or --help for more information."
        en["MOD_CONTAINS_BOTH_NAME_AND_ID"] = "-n or --name and --id cannot exist at the same time."
        en["MOD_CONTAINS_BOTH_NOT_NAME_AND_ID"] =
            "Must specify -n or --name or --id. Use option -h or --help for more information."
        en["MOD_SEARCH_LIMIT_GREATER_THAN_FIFTY"] = "If the download source is CurseForge, the maximum limit is 50."
        en["MOD_ID_LIMIT_COEXIST"] =
            "You don't need the search function of -n or --name, how can you use --limit to limit the search results?"
        en["MOD_CONTAINS_NOTHING"] = "Must specify --install, --info or --url."
        en["MOD_CONTAINS_TWO_OR_MORE"] = "Only one of --install, --info or --url can exist."
        en["NO_SEARCH_RESULTS"] = "No search results."
        en["DOWNLOAD_SOURCE_OFFICIAL"] = "Official"
        en["DOWNLOAD_SOURCE_BMCLAPI"] = "BMCLAPI"
        en["MODPACK_CONTAINS_TWO_OR_MORE"] = "Only one of --install, --info, --file or --url can exist."
        en["MODPACK_CONTAINS_NOTHING"] = "Must specify --install, --info, --file or --url."
        en["YES_SHORT"] = "Yes"
        en["TABLE_ACCOUNTS_LIST_HEADER_SELECTED"] = "Selected"
        en["TABLE_ACCOUNTS_LIST_HEADER_ORDER"] = "Order"
        en["TABLE_ACCOUNTS_LIST_HEADER_NAME"] = "Name"
        en["TABLE_ACCOUNTS_LIST_HEADER_TYPE"] = "Account Type"
        en["TABLE_ACCOUNTS_LIST_HEADER_OTHER_INFORMATION"] = "Other Information"
        en["TABLE_CONFIG_ALL_NAME"] = "Config Name"
        en["TABLE_CONFIG_ALL_TYPE"] = "Type"
        en["TABLE_CONFIG_ALL_VALUE"] = "Config Value"
        en["TABLE_CONFIG_ALL_VIEW_SEPARATELY"] = "View separately through \"cmcl config %s\""
        en["TABLE_SETTABLE_CONFIG_NAME"] = "Config Name"
        en["TABLE_SETTABLE_CONFIG_TYPE"] = "Type"
        en["TABLE_SETTABLE_CONFIG_MEANING"] = "Meaning"
        if (Constants.isDebug()) println("init english textMap finish")
        en
    }

    override val helpMap: Map<String, String> by lazy {
        if (Constants.isDebug()) println("init english helpMap start")
        val enHelp: MutableMap<String, String> = HashMap()
        //"     -i, --immersive           Enter immersive mode*\n" +
        /*"\n" +
                              " * Immersive mode: In this mode, only complete options can be used to execute the above command, such as help,\n" +
                              "   list, print; execute the command below without entering \"cmcl\", such as config maxMemory 2048;\n" +
                              "   starting the game should use start [<version>].\n" +*/
        enHelp["ROOT"] =
            """Console Minecraft Launcher v${Constants.CMCL_VERSION_NAME}: A Command-line Minecraft Java Edition Launcher written in Kotlin

Note:
  Content in square brackets is optional.
  A comma in an option means that both options can do the same thing.
  For specifying content for options,
  you can only add content directly after the abbreviated option (a hyphen) (no spaces), such as cmcl -lD:\.minecraft,
  you can only add an equal sign after the complete option (two hyphens) and then enter the content, such as cmcl --list=D:\.minecraft,
  or add a space after the two and then enter the content, such as cmcl -l D:\.minecraft; cmcl --list D:\.minecraft,
  for details, please refer to the example after the option description.

Usage:
cmcl [<version>]               Start the version selected via "cmcl -s <version>"
                               or the version specified by <version>.
                                  e.g cmcl 1.19: to start 1.19;
                                  If the currently selected version is 1.18.2,
                                  run it directly (without parameters), it will start 1.18.2.
     -h, --help                Get help documentation
     -l, --list[=<game dir>]   List all game versions in current game directory or <game dir>.
                                  e.g. cmcl -l; cmcl --list=D:\.minecraft
     -p, --print[=<version>]   Print the startup command for selected version or <version>.
                                  e.g. cmcl -p; cmcl --print=1.19
     -s, --select=<version>    Select version, after selection, you can directly use "cmcl" to start
     -a, --about               Show about information.
     -c, --check-for-updates   Check for updates
     <function> <option>...    Put the function name in "All functions" into <function> (without hyphen), and
                               put the option after the function name to perform the corresponding operation.
                                  cmcl install -h

All functions:
     function name     effect
     install           Install versions
     version           Version operation
     account           Account operation
     config            Modify launcher configuration
     simplify          Set simplified commands
     jvmArgs           Custom JVM arguments
     gameArgs          Custom game arguments
     mod               Mods searching and installation
     modpack           Modpacks searching and installation
    
Each function can use -h or --help to get help documentation for related options.
In the function help documentation, the content following the description is a usage example."""
        enHelp["install"] = """Install Version
  Function Name: install
  First usage: 
     install <version to be installed> <option>...  Install a version   cmcl install 1.18.2
  Options:
   -n, --name=<version storage name>    Indicates the name of the version store locally.
                                           cmcl install 1.18.2 -n my1.18.2
   -s, --select                         Select this version after installing.
                                           cmcl install 1.18.2 -s
   --fabric[=<Fabric version>]          Install Fabric, you can choose whether to install Fabric API and specify
      [--api[=<Fabric API version>]]    its version. Not compatible with Forge, LiteLoader, OptiFine, Quilt.
                                           cmcl install 1.19 --fabric=0.14.12 --api
   --forge[=<Forge version>]            Install Forge
                                           cmcl install 1.19 --forge
   --liteloader[=<LiteLoader version>]  Install LiteLoader
                                           cmcl install 1.12 --liteloader=1.12-SNAPSHOT
   --optifine[=<OptiFine version>]      Install OptiFine
   --quilt[=<Quilt version>]            Install Quilt. Not compatible with Forge, LiteLoader, OptiFine, Fabric.
   -t, --thread=<thread count>          Set the number of threads for downloading assets (64 by default)
                                           cmcl install 1.19 --thread=96
   --no-assets                          Do not download assets
   --no-libraries                       Do not download libraries
   --no-natives                         Do not download native libraries
  Second Usage: 
     install --show=<version type> [-t, --time=<time range>]
        Show all installable versions.
        Version type: All: a/all; Release: r/release; Snapshot: s/snapshot;
                      Old Alpha: oa/oldAlpha; Old Beta: ob/oldBeta.
        Time range format: <from year>-<from month>-<from day>/<to year>-<to month>-<to day>
           cmcl install --show=all                            Show all installable versions
           cmcl install --show=r                              Show all release versions
           cmcl install --show=s --time=2020-05-09/2021-10-23 Show snapshot versions from May 9, 2020 to October 23, 2021"""
        enHelp["version"] = """Version
  Function Name: version
  Basic usage: version [<target version>] <option>... Operate target version or selected version
  Options:
   --info                                View version information. cmcl version 1.19 --info
   -d, --delete                          Delete the version.       cmcl version -d
   --rename=<new name>                   Rename the version
   --complete[=assets|libraries|natives] Complete assets, libraries or native libraries, if you don't
    [-t, --thread=<thread count>]        specify which content to complete, the version installed by the
                                         mod loader will be completed. When completing assets, you can also
                                         specify the number of threads by specifying -t, --thread=<thread count>.
                                            cmcl version 1.19 --complete
                                            cmcl version --complete=assets
   --config=<config name> [<content>]    Set the configuration separately for the version, use "config --view" to
                                         view the content with "[Game related]" is the configuration that can be set.
                                         If no <content> is entered, the global configuration will be used.
   --fabric[=<Fabric version>]           Install Fabric for the version, you can specify the version.
      [--api[=<Fabric API version>]]     Add --api to install Fabric API, you can also specify the version.
                                            cmcl version 1.19 --fabric --api=0.58.0
   --forge[=<Forge version>]             Install Forge for the version, you can specify the version.
                                            cmcl version 1.19 --forge=42.0.0
   --liteloader[=<LiteLoader version>]   Install LiteLoader for the version, you can specify the version.
   --optifine[=<OptiFine version>]       Install OptiFine for the version, you can specify the version.
   --quilt[=<Quilt version>]             Install Quilt for the version, you can specify the version.
   --isolate                             Set version working directory isolation(override gameDir configuration)
   --unset-isolate                       Unset version working directory isolation
   -p, --print-command                   Print the startup command.
   --export-script=<script file>         Export launch script (bat format under Windows, otherwise sh format)
   --export-script-ps=<script file>      Export PowerShell launch script (.ps1)"""
        enHelp["account"] = """Account
  Function Name: account
  Note: The order number of the account can be obtained through -l or --list.
  Options:
   -s, --select=<order>                     Select an account.                      cmcl account -s 3
   -l, --list                               List all accounts.                      cmcl account --list
   -d, --delete=<order>                     Delete an account.                      cmcl account --delete=4
   -r, --refresh                            Refresh the currently selected account. cmcl account --refresh
   --cape[=<cape file path>]                Custom cape, if not entered path, the cape will be unset.
                                            This feature is only available for offline accounts*.
   --download-skin=<skin file storage path> Download skin file.      cmcl account --download-skin=D:\mySkin.png
   --skin[=steve|alex|<skin file path>]     Set the skin to Steve, Alex, or a custom skin. If it is an offline
                                            account and if you do not enter path, the skin will be unset.
                                            This feature is not available for Microsoft accounts and nide8auth*.
                                               cmcl account --skin
                                               cmcl account --skin=steve
                                               cmcl account --skin=D:\handsome.png
   --login=offline|microsoft|authlib|nide8auth [-s, --select]
       Login your account (and select).
         offline: To login an offline account, need to specify: -n, --name=<player name>,
            cmcl account --login=offline --name=Alexander
         microsoft: To login a Microsoft account, no content need to be specified,
            cmcl account --login=microsoft
         authlib: To login an authlib-injector account, need to specify: --address=<server address>,
            cmcl account --login=authlib --address=127.0.0.1
         nide8auth: To login an nide8auth account, need to specify: --serverId=<server ID>,
            cmcl account --login=nide8auth --serverId=1234567890abcdef1234567890abcdef
   * Some accounts do not support some functions, because there are no available APIs, please go to the corresponding website to do this by yourself."""
        enHelp["config"] = """Configuration
  Function Name: config
  Options:
   <config name> [<content>]        If <content> is not empty, the configuration will be set,
                                    you can use -v to view the settable configuration;
                                    otherwise, output the configuration value corresponding to <config name>.
                                       cmcl config javaPath        Output Java path
                                       cmcl config maxMemory 2048  Modify maximum memory
   -a, --all                        Output all configuration content
                                       cmcl config -a
   --getRaw[=<indent number>]       Output the original content of the configuration, <indent number> defaults to 2.
                                       cmcl config --getRaw
   -d, --delete=<config name>       Delete the configuration corresponding to <config name>.
                                       cmcl config -d javaPath
                                       cmcl config --delete=javaPath
   -c, --clear                      Clear configuration.
                                       cmcl config --clear
   -v, --view                       View all settable configurations.
                                       cmcl config -v"""
        enHelp["simplify"] = """Set simplified commands
  Function Name: simplify
  Options:
   -p, --print
        View all simplified commands that have been set.
           cmcl simplify -p
   -s, --set=<Simplified Command> "<Original Command>"
        Set or modify simplified commands. After setting, enter
        "cmcl <Simplified Command>" to run "cmcl <Original Command>".
        As shown in the example below, after setting, enter "cmcl ds2"
        to realize the function of "cmcl config downloadSource 2",
        which is more convenient.
           cmcl simplify -s ds2 "config downloadSource 2"
   -d, --delete=<Simplified Command>
        Delete a simplified command.
           cmcl simplify -d ds2"""
        enHelp["jvmArgs"] = """Custom JVM Arguments
  Function Name: jvmArgs
  Options:
   -p, --print[=<indent number>] [-v, --version=<version>]        Output all arguments, <indent number> is 2 by
                                                                  default, and the version can be specified.
                                                                     cmcl jvmArgs -p2 -v1.19
   -a, --add=<content> [-v, --version=<version>]                  Add an argument, the version can be specified.
                                                                  To prevent parsing errors, enclose content in
                                                                  double quotes and use an equals sign to specify
                                                                  the content. Note: Spaces cannot be used heres
                                                                  instead of the equal sign.
                                                                     cmcl jvmArgs --add="-Dfile.encoding=UTF-8"
   -d, --delete=<order, starts from 0> [-v, --version=<version>]  Delete an argument, the version can be specified.
                                                                     cmcl jvmArgs --delete=2 --version=1.19"""
        enHelp["gameArgs"] = """Custom Game Arguments
  Function Name: gameArgs
  Options:
   -p, --print[=<indent number>] [-v, --version=<version>]        Output all arguments, <indent number> is 2 by
                                                                  default, and the version can be specified.
                                                                     cmcl gameArgs --print --version=1.19
   -a, --add=<config name> [<content>] [-v, --version=<version>]  Add an argument, the version can be specified.
                                                                     cmcl gameArgs -a width 256
   -d, --delete=<config name> [-v, --version=<version>]           Delete an argument, the version can be specified.
                                                                     cmcl gameArgs --delete=demo"""
        enHelp["mod"] = """Mod
  Function Name: mod
  Note: <source> can input cf or curseforge (CurseForge), mr or modrinth (Modrinth).
  Options:
   --url=<mod url> Download mod from Internet
   --install
      [--source=<source>]
      -n, --name=<mod name>|--id=<mod ID>
      [--limit=<limit the number of search results>]
      [--game-version=<game version>]
      [-v, --version=<mod version>]
     Search and install mod by mod name or ID. When searching by mod name, the number
     of results defaults to 50, it can be restricted. If <source> is CurseForge, the limit is 50 at most.
        cmcl mod --install -nMinis --limit=30
        cmcl mod --install --source=curseforge --id=297344
        cmcl mod --install --name=Sodium --limit=30
        cmcl mod --install --source=mr --id=GBeCx05I
   --info
      [--source=<source>]
      -n, --name=<mod name>|--id=<mod ID>
      [--limit=<limit the number of search results>]
     Search and display mod information by mod name or ID. When searching by mod name, the number
     of results defaults to 50, it can be restricted. If <source> is CurseForge, the limit is 50 at most."""
        enHelp["modpack"] = """Modpack
  Function Name: modpack
  Note: <source> can input cf or curseforge (CurseForge), mr or modrinth (Modrinth).
        -t, --thread=<thread count>, --no-assets, --no-libraries and --no-natives are also supported when installing the modpack.
        For their functions, please refer to the help documentation of "Install Version"(install).
  Options:
   --url=<modpack url> [--storage=<version storage name>]   Download and install modpack from Internet
   --file=<modpack path> [--storage=<version storage name>] Install local modpack
   --install
      [--source=<source>]
      -n, --name=<modpack name>|--id=<modpack ID>
      [--limit=<limit the number of search results>]
      [--storage=<version storage name>]
      [-k, --keep-file]
      [--game-version=<game version>]
      [-v, --version=<modpack version>]
     Search and install modpack by modpack name or ID. When searching by modpack name, the number
     of results defaults to 50, it can be restricted. If <source> is CurseForge, the limit is 50 at most.
     Adding -k or --keep-file means to keep the file (in directory .cmcl/modpacks) after installation.
        cmcl modpack --install -nRLCraft --limit=30 --storage="New Game"
        cmcl modpack --install --source=curseforge --id=285109
        cmcl modpack --install --name="Sugar Optimization" --limit=30 --storage NewModpack
        cmcl modpack --install --source=mr --id=BYN9yKrV
   --info
      [--source=<source>]
      -n, --name=<modpack name>|--id=<modpack ID>
      [--limit=<limit the number of search results>]
     Search and display modpack information by modpack name or ID. When searching by modpack name, the number
     of results defaults to 50, it can be restricted. If <source> is CurseForge, the limit is 50 at most."""
        if (Constants.isDebug()) println("init english helpMap finish")
        enHelp
    }
}
