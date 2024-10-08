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
package com.mrshiehx.cmcl.constants.languages.chinese

import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.constants.languages.Language

object SimplifiedChinese : Language {
    //调用到getTextMap()或getHelpMap()再创建Map然后put，不要在初始化就put，
    //因为有些操作不需要获取用于向用户显示的文本，就不用花费这个时间了

    override val textMap: Map<String, String> by lazy {
        if (Constants.isDebug()) println("init SimplifiedChinese textMap start")
        val zh: MutableMap<String, String> = HashMap()
        zh["APPLICATION_NAME"] = "Console Minecraft Launcher (Kotlin)"
        zh["MESSAGE_ABOUT_DESCRIPTION_1"] = "Console Minecraft Launcher (Kotlin) v%1\$s"
        zh["MESSAGE_ABOUT_DESCRIPTION_2"] = "一个使用 Kotlin 编写的命令行 Minecraft Java 版启动器"
        zh["MESSAGE_ABOUT_DESCRIPTION_4"] = "源代码仓库："
        zh["MESSAGE_ABOUT_DESCRIPTION_6"] = "依赖库："
        zh["MESSAGE_ABOUT_DESCRIPTION_MAIN_DEVELOPERS"] = "主要开发者："
        zh["MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS"] = "特别鸣谢："
        zh["MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_AUTHLIB_INJECTOR"] = "authlib-injector 相关支持"
        zh["MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_BMCLAPI"] = "提供BMCLAPI下载源"
        zh["MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_MCBBS_NAME"] = "MCBBS 我的世界中文论坛...."
        zh["MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_MCBBS"] = "曾提供MCBBS下载源"
        zh["MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_TITLE"] = "免责声明"
        zh["MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_CONTENT_1"] =
            "Minecraft 版权归 Mojang Studios 与 Microsoft 所有，使用CMCL产生的所有版权问题，软件制作方概不负责，请支持正版。"
        zh["MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_CONTENT_2"] =
            "用户因使用CMCL而产生的一切后果由用户自己承担，任何涉及CMCL的法律纠纷与冲突与开发者无关，CMCL与开发者将不承担任何责任。"
        zh["MESSAGE_OFFICIAL_LOGIN_FAILED_TITLE"] = "登录正版账号失败"
        zh["MESSAGE_LOGINED_TITLE"] = "登录账号成功"
        zh["MESSAGE_DOWNLOAD_SKIN_FILE_NOT_SET_TEXT"] = "未设置皮肤"
        zh["MESSAGE_FAILED_REFRESH_TITLE"] = "刷新账号失败"
        zh["MESSAGE_UNABLE_TO_LOGIN_MICROSOFT"] = "暂时无法登录，请稍后再试。"
        zh["MESSAGE_OFFICIAL_LOGIN_FAILED_MESSAGE"] = "登录账号失败，可能是因为您的账号尚未拥有 Minecraft。"
        zh["MESSAGE_STARTING_GAME"] = "启动游戏中..."
        zh["MESSAGE_FINISHED_GAME"] = "游戏结束"
        zh["MESSAGE_FAILED_TO_CONNECT_TO_URL"] = "连接到 %s 失败，请检查您的网络连接。"
        zh["MESSAGE_VERSIONS_LIST_IS_EMPTY"] = "游戏版本列表为空。"
        zh["MESSAGE_INSTALL_INPUT_NAME"] = "请输入新版本的名称："
        zh["MESSAGE_INSTALL_INPUT_NAME_EXISTS"] = "%s：该名称已存在，请更换一个名称。"
        zh["MESSAGE_FAILED_TO_CONTROL_VERSION_JSON_FILE"] = "下载或解析目标版本的 JSON 文件失败：%s"
        zh["MESSAGE_INSTALL_NOT_FOUND_JAR_FILE_DOWNLOAD_INFO"] = "在目标版本的JSON文件中找不到客户端文件的下载信息。"
        zh["MESSAGE_INSTALL_JAR_FILE_DOWNLOAD_URL_EMPTY"] = "客户端文件的下载链接为空。"
        zh["MESSAGE_FAILED_TO_INSTALL_NEW_VERSION"] = "安装新版本失败：%s"
        zh["MESSAGE_INSTALL_DOWNLOADING_JAR_FILE"] = "正在下载客户端文件..."
        zh["MESSAGE_INSTALL_DOWNLOADED_JAR_FILE"] = "下载客户端文件完成"
        zh["MESSAGE_INSTALL_DOWNLOADING_ASSETS"] = "正在下载资源文件中..."
        zh["MESSAGE_INSTALL_DOWNLOADED_ASSETS"] = "下载资源文件完成"
        zh["MESSAGE_INSTALL_DOWNLOAD_ASSETS_NO_INDEX"] = "下载资源文件失败，找不到资源文件索引。"
        zh["MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_ASSETS"] = "下载资源文件失败：%s"
        zh["MESSAGE_EXCEPTION_DETAIL_NOT_FOUND_URL"] = "找不到下载链接"
        zh["MESSAGE_FAILED_DOWNLOAD_FILE"] = "%s：下载文件失败"
        zh["MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON"] = "下载文件“%s”失败：%s"
        zh["MESSAGE_INSTALL_DOWNLOADING_LIBRARIES"] = "正在下载依赖库文件..."
        zh["MESSAGE_INSTALL_DOWNLOADED_LIBRARIES"] = "下载依赖库文件完成"
        zh["MESSAGE_INSTALL_FAILED_TO_DOWNLOAD_LIBRARIES"] = "下载依赖库文件失败：%s"
        zh["MESSAGE_INSTALL_LIBRARIES_LIST_EMPTY"] = "依赖库列表为空"
        zh["MESSAGE_FAILED_TO_DECOMPRESS_FILE"] = "解压缩文件“%1\$s”失败：%2\$s"
        zh["MESSAGE_INSTALL_DECOMPRESSING_NATIVE_LIBRARIES"] = "正在解压原生依赖库文件..."
        zh["MESSAGE_INSTALL_DECOMPRESSED_NATIVE_LIBRARIES"] = "解压原生依赖库文件完成"
        zh["MESSAGE_FAILED_TO_COPY_FILE"] = "复制文件“%1\$s”到“%2\$s”失败：%3\$s"
        zh["MESSAGE_INSTALLED_NEW_VERSION"] = "安装新版本完成"
        zh["MESSAGE_UUID_ACCESSTOKEN_EMPTY"] =
            "UUID 或 AccessToken 为空，可尝试通过“account --refresh”刷新账号或重新登录。"
        zh["MESSAGE_DOWNLOADING_FILE"] = "正在下载 %s"
        zh["MESSAGE_DOWNLOADING_FILE_TO"] = "正在下载 %s 到 %s"
        zh["MESSAGE_COPYING_FILE"] = "正在复制 %s 到 %s"
        zh["MESSAGE_UNZIPPING_FILE"] = "正在解压 %s"
        zh["MESSAGE_GAME_CRASH_CAUSE_TIPS"] = "游戏崩溃可能的错误：%s"
        zh["MESSAGE_GAME_CRASH_CAUSE_URLCLASSLOADER"] =
            "旧版本 Minecraft 出现此错误可能是因为 Java 版本过高，可使用 Java 8 或更低版本以修复此问题。\n您可以使用“version <版本> --config=javaPath <Java 路径>”为版本单独设置 Java 路径。"
        zh["MESSAGE_GAME_CRASH_CAUSE_LWJGL_FAILED_LOAD"] =
            "出现此错误可能是因为原生依赖库缺失或损坏，请通过“version <版本> --complete=natives”重新下载依赖库文件以修复此问题。"
        zh["MESSAGE_GAME_CRASH_CAUSE_MEMORY_TOO_SMALL"] = "内存不足，可尝试把内存调整为一个更大的数。"
        zh["MESSAGE_REDOWNLOADED_NATIVES"] = "下载原生依赖库文件完成"
        zh["MESSAGE_FAILED_SEARCH"] = "搜索失败：%s"
        zh["MESSAGE_FAILED_RENAME_VERSION"] = "重命名版本失败：%s"
        zh["MESSAGE_START_INSTALLING_FORGE"] = "开始安装 Forge"
        zh["MESSAGE_INSTALLED_FORGE"] = "安装 Forge 成功"
        zh["MESSAGE_NOT_FOUND_LIBRARY_DOWNLOAD_URL"] = "找不到依赖库 %s 的下载链接"
        zh["MESSAGE_INSTALL_NATIVES_EMPTY_JAR"] = "无需要解压的原生依赖库文件"
        zh["MESSAGE_INSTALL_FORGE_FAILED_EXECUTE_PROCESSOR"] = "执行任务失败：%s"
        zh["MESSAGE_YGGDRASIL_LOGIN_SELECT_PROFILE"] = "请选择一个角色(%d-%d)："
        zh["MESSAGE_INPUT_VERSION_NAME"] = "请输入要存储为的版本名称："
        zh["MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON_WITH_URL"] =
            "下载文件失败：%s，文件链接为：%s，可自行下载并存储到 %s 中"
        zh["MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON_WITH_URL_WITH_NAME"] =
            "下载文件失败：%s，文件链接为：%s，可自行下载并存储到 %s 中，并把名称修改为“%s”"
        zh["MESSAGE_START_INSTALLING_LITELOADER"] = "开始安装 LiteLoader"
        zh["MESSAGE_INSTALLED_LITELOADER"] = "安装 LiteLoader 完成"
        zh["MESSAGE_START_INSTALLING_OPTIFINE"] = "开始安装 OptiFine"
        zh["MESSAGE_INSTALLED_OPTIFINE"] = "安装 OptiFine 完成"
        zh["MESSAGE_INSTALL_MODPACK_UNKNOWN_TYPE"] = "安装整合包失败：未知整合包类型。"
        zh["MESSAGE_INSTALL_MODPACK_NOT_FOUND_GAME_VERSION"] = "安装整合包失败：找不到要安装的游戏版本。"
        zh["MESSAGE_INSTALL_MODPACK_COEXIST"] = "安装整合包失败：%1\$s 和 %2\$s 不能同时安装。"
        zh["MESSAGE_INSTALL_MODPACK_NOT_SUPPORTED_NEOFORGE"] =
            "抱歉，本启动器暂时不支持安装 NeoForge，将无法安装此整合包。"
        zh["MESSAGE_COMPLETE_VERSION_IS_COMPLETE"] =
            "该版本为完整的版本，无需补全。若经检查版本的确不完整，请您手动重新安装该版本。"
        zh["MESSAGE_COMPLETED_VERSION"] = "补充版本成功"
        zh["MESSAGE_SELECT_DOWNLOAD_SOURCE"] = "首次下载请选择下载源(默认为%d，存储为配置 downloadSource)："
        zh["MESSAGE_SELECT_ACCOUNT"] = "请输入要选择账号的序号(%d-%d)："
        zh["MESSAGE_SELECT_ACCOUNT_TYPE"] = "暂无可用的账号，请选择新账号的账号类型(%d-%d)："
        zh["MESSAGE_FAILED_TO_CHECK_FOR_UPDATES"] = "检查更新失败"
        zh["MESSAGE_NEW_VERSION"] = "新版本：%s\n更新日期：%s\n下载地址：\n%s更新内容：\n%s"
        zh["MESSAGE_CURRENT_IS_LATEST_VERSION"] = "当前已是最新版本"
        zh["MESSAGE_BEFORE_LIST_VERSIONS"] = "目录 %s 中的游戏版本："
        zh["MESSAGE_AUTHLIB_ACCOUNT_INCOMPLETE"] = "外置登录账号不完整，请把其删除后重新登录。"
        zh["MESSAGE_NIDE8AUTH_ACCOUNT_INCOMPLETE"] = "统一通行证不完整，请把其删除后重新登录。"
        zh["MESSAGE_ACCOUNT_FAILED_TO_VALIDATE"] = "账号校验失败：%s"
        zh["MESSAGE_ACCOUNT_INFO_EXPIRED_NEED_RELOGIN"] = "信息过期，请重新登录。"
        zh["MESSAGE_ACCOUNT_INFO_MISSING_NEED_RELOGIN"] = "信息缺失，请重新登录。"
        zh["MESSAGE_AUTHLIB_ACCOUNT_REFRESH_NO_CHARACTERS"] =
            "您的角色已被删除，也没有可用的角色，请您前往外置登录的网站添加角色后重新刷新或登录。否则游戏中的相关功能可能不可用。"
        zh["MESSAGE_NIDE8AUTH_ACCOUNT_REFRESH_NO_CHARACTERS"] =
            "您的角色已被删除，也没有可用的角色，请您前往统一通行证的网站添加角色后重新刷新或登录。否则游戏中的相关功能可能不可用。"
        zh["MESSAGE_YGGDRASIL_ACCOUNT_REFRESH_OLD_CHARACTER_DELETED"] = "您的角色已被删除，请选择一个新的角色。"
        zh["MESSAGE_GAME_CRASH_CAUSE_JVM_UNRECOGNIZED_OPTION"] =
            "您添加了错误的JVM参数。通过“jvmArgs --help”获取相关信息。"
        zh["MESSAGE_TELL_USER_CHECK_ACCOUNT_CAN_BE_OFF"] =
            "如果您不希望启动前检查账号是否可用，可使用“config checkAccountBeforeStart false”或“version <版本> --config=checkAccountBeforeStart false”关闭。"
        zh["MESSAGE_STARTUP_INFO_MAIN"] = """
        启动版本：${"$"}{VERSION_NAME} (${"$"}{REAL_VERSION_NAME}) | 登录账号：${"$"}{PLAYER_NAME} (${"$"}{ACCOUNT_TYPE}) | Java 路径：${"$"}{JAVA_PATH}
        游戏跟随启动器退出：${"$"}{EXIT_WITH_MC} | 全屏：${"$"}{FULLSCREEN} | 最大内存：${"$"}{MAX_MEMORY} | 窗口宽：${"$"}{WIDTH} | 窗口高：${"$"}{HEIGHT} | 启动前检查账号：${"$"}{CHECK_ACCOUNT_BEFORE_START}
        游戏目录：${"$"}{GAME_DIR}
        """.trimIndent()
        zh["MESSAGE_STARTUP_INFO_ASSETS_DIR"] = "资源目录：\${ASSETS_DIR}"
        zh["MESSAGE_STARTUP_INFO_RESOURCE_PACKS_DIR"] = "资源包目录：\${RESOURCE_PACKS_DIR}"
        zh["MESSAGE_STARTUP_INFO_ARGS"] = """
        自定义JVM参数：
        ${"$"}{JVM_ARGS}
        自定义游戏参数：
        ${"$"}{GAME_ARGS}
        """.trimIndent()
        zh["MESSAGE_STARTUP_INFO_QUICK_PLAY_LOG_FILE_PATH"] = "快速游玩日志文件路径：\${QUICK_PLAY_LOG_FILE_PATH}"
        zh["MESSAGE_STARTUP_INFO_QUICK_PLAY_SAVE_NAME"] = "快速游玩直接进入的存档名：\${QUICK_PLAY_SAVE_NAME}"
        zh["MESSAGE_STARTUP_INFO_QUICK_PLAY_SERVER_ADDRESS"] =
            "快速游玩直接进入的服务器地址：\${QUICK_PLAY_SERVER_ADDRESS}"
        zh["MESSAGE_STARTUP_INFO_QUICK_PLAY_REALMS_ID"] = "快速游玩直接进入的领域ID：\${QUICK_PLAY_REALMS_ID}"
        zh["MESSAGE_TO_SELECT_VERSION"] =
            "请使用“-s <版本名称>”选择一个可启动的版本或使用“install <版本名称>”安装一个新的版本并选择。"
        zh["MESSAGE_PRINT_COMMAND_EXCEEDS_LENGTH_LIMIT"] =
            "提示：启动命令过长，您可能无法直接在cmd中运行或保存至bat文件后执行，推荐您使用“version [<版本>] --export-script-ps=<脚本文件>”导出为 PowerShell 脚本文件后使用。"
        zh["MESSAGE_EXPORT_COMMAND_EXCEEDS_LENGTH_LIMIT"] =
            "无法导出脚本文件：由于bat脚本文件的长度限制，启动命令过长，无法导出为bat文件，只能使用“version [<版本>] --export-script-ps=<以.ps1为后缀的脚本文件>”导出为 PowerShell 脚本文件。"
        zh["MESSAGE_CONFIGURATIONS_TIP"] =
            "注：类型为布尔值的配置，它的值可以输入表示“是”的“true”，也可以是表示“否”的“false”。"
        zh["MESSAGE_CONFIGURATIONS_TABLE_CONTENT"] = """
        accounts|JSON数组|账号（非直接修改，请通过“account -h”获得相关使用教程以进行修改）
        downloadSource|整数|下载源，0为官方，1为BMCLAPI
        language|文本|语言，zh为简体中文，en为英文，cantonese是粤语（简体）
        selectedVersion|文本|已选择的版本，可直接使用“cmcl”进行启动
        maxMemory|整数|[游戏相关]最大内存（单位：MB）
        gameDir|文本|[游戏相关]自定义游戏目录路径（或设置版本隔离），默认为.minecraft
        assetsDir|文本|[游戏相关]自定义assets资源目录路径，若为空则为游戏目录内的assets目录
        resourcesDir|文本|[游戏相关]自定义资源包目录路径，若为空则为游戏目录内的resourcepacks目录
        javaPath|文本|[游戏相关]Java 路径（如果为空会自动获得）
        windowSizeWidth|整数|[游戏相关]游戏窗口的宽
        windowSizeHeight|整数|[游戏相关]游戏窗口的高
        isFullscreen|布尔值|[游戏相关]游戏窗口是否为全屏
        exitWithMinecraft|布尔值|[游戏相关]运行游戏时，是否需要退出启动器时顺便退出游戏
        printStartupInfo|布尔值|[游戏相关]开始游戏的时候，是否输出启动信息（Java 路径、最大内存等）
        checkAccountBeforeStart|布尔值|[游戏相关]开始游戏之前，是否检查账号是否可用
        jvmArgs|JSON数组|[游戏相关]自定义JVM参数（通过“jvmArgs -h”获得相关使用教程以进行修改）
        gameArgs|JSON对象|[游戏相关]自定义游戏参数（通过“gameArgs -h”获得相关使用教程以进行修改）
        qpLogFile|文本|[游戏相关]快速游玩（Quick Play，Minecraft 1.20 的新功能，设置下面三项配置即可启动游戏后分别直接进入存档、服务器、领域，只能设置一项）的日志文件路径（相对于游戏目录），可选
        qpSaveName|文本|[游戏相关]快速游玩直接进入的存档名称
        qpServerAddress|文本|[游戏相关]快速游玩直接进入的服务器地址（包括端口），该配置也适用于1.20之前的版本
        qpRealmsID|文本|[游戏相关]快速游玩直接进入的领域ID
        proxyEnabled|布尔值|是否开启网络代理
        proxyHost|文本|代理主机地址
        proxyPort|整数|代理端口
        proxyUsername|文本|代理验证的账户（代理可选）
        proxyPassword|文本|代理验证的密码（代理可选）
        modDownloadSource|文本|模组下载源，curseforge或modrinth
        modpackDownloadSource|文本|整合包下载源，curseforge或modrinth
        simplifyCommands|JSON对象|简化命令（通过“simplify -h”获得相关使用教程以进行修改）
        """.trimIndent()
        zh["ERROR_WITH_MESSAGE"] = "错误：%1\$s\n错误信息：%2\$s"
        zh["EXCEPTION_VERSION_JSON_NOT_FOUND"] =
            "目标启动版本的JSON文件或JAR文件不存在，请使用“-s <版本名称>”选择一个可启动的版本或使用“install <版本名称>”安装一个新的版本并选择。"
        zh["EXCEPTION_VERSION_NOT_FOUND"] = "%s：游戏版本不存在"
        zh["EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND"] =
            "找不到原生依赖库（natives）目录或为空，您需要使用“version <版本名称> --complete=natives”下载原生依赖库文件以启动游戏。"
        zh["EXCEPTION_MAX_MEMORY_TOO_BIG"] = "最大内存大于物理内存总大小"
        zh["EXCEPTION_MAX_MEMORY_MUST_BE_GREATER_THAN_ZERO"] = "最大内存必须大于零"
        zh["EXCEPTION_JAVA_VERSION_TOO_LOW"] =
            "此 Minecraft 版本的 Java 版本最低要求为 %d，您选择的 Java 版本为 %d，请选择一个达到要求的 Java 后重试。"
        zh["EXCEPTION_WINDOW_SIZE_MUST_BE_GREATER_THAN_ZERO"] = "游戏窗口的宽和高必须大于零"
        zh["EXCEPTION_VERSION_JAR_NOT_FOUND"] = "目标版本的JAR文件不存在，请重新安装该版本。"
        zh["EXCEPTION_CREATE_FILE"] = "创建文件失败"
        zh["EXCEPTION_CREATE_FILE_WITH_PATH"] = "%s：创建文件失败"
        zh["EXCEPTION_OF_NETWORK_WITH_URL"] = "访问 %1\$s 时网络错误：%2\$s"
        zh["EXCEPTION_NIDE8AUTH_JAVA_VERSION_TOO_LOW"] =
            "Java 版本小于 8u101 无法使用统一通行证，请更换一个符合要求的 Java 后重试。"
        zh["EXCEPTION_GET_USER_PROPERTIES"] = "获取用户资料失败：%s"
        zh["EXCEPTION_SAVE_CONFIG"] = "保存配置失败：%s"
        zh["EXCEPTION_READ_CONFIG_FILE"] = "读取配置文件失败，请确保配置文件 cmcl.json 是可读取的文件且内容无误：%s"
        zh["EXCEPTION_JAVA_NOT_FOUND"] = "无法启动游戏，找不到 Java 文件。"
        zh["EXCEPTION_READ_FILE"] = "读取文件失败"
        zh["EXCEPTION_READ_FILE_WITH_PATH"] = "%s：读取文件失败"
        zh["EXCEPTION_PARSE_FILE"] = "解析文件失败"
        zh["EXCEPTION_PARSE_FILE_WITH_PATH"] = "解析文件“%s”失败"
        zh["EXCEPTION_WRITE_FILE"] = "写入文件失败"
        zh["EXCEPTION_WRITE_FILE_WITH_PATH"] = "写入内容到文件“%s”失败"
        zh["EXCEPTION_UNABLE_PARSE"] = "解析失败"
        zh["EXCEPTION_INSTALL_MODPACK"] = "安装整合包失败：%s"
        zh["EXCEPTION_EXECUTE_COMMAND"] = "执行命令失败"
        zh["EXCEPTION_INCOMPLETE_VERSION"] =
            "该版本不完整，请通过“version <版本名称> --complete”把该版本补充完整后再启动。"
        zh["EXCEPTION_NOT_FOUND_DOWNLOAD_LINK"] = "找不到文件下载地址"
        zh["EXCEPTION_NOT_FOUND_DOWNLOAD_LINK_WITH_FILENAME"] = "找不到文件“%s”的下载地址"
        zh["EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"] =
            "网络错误：如果不是目标网站的问题，则可能是您的代理出现了问题，请检查您的网络代理是否可用！"
        zh["ON_AUTHENTICATED_PAGE_TEXT"] = "已完成微软账户授权，请关闭此页面并返回到启动器完成登录。"
        zh["WEB_TITLE_LOGIN_MICROSOFT_ACCOUNT_RESPONSE"] = "登录微软账户 - Console Minecraft Launcher"
        zh["CONSOLE_UNSUPPORTED_VALUE"] = "不支持的值：%s"
        zh["CONSOLE_LOGIN_MICROSOFT_WAIT_FOR_RESPONSE"] =
            "请在浏览器内登录您的微软账户，如果没有自动跳转，请手动复制该链接至浏览器：\n%s\n如果登录成功，请返回到此处，等待完成登录。\n登录需要一定的时间，请耐心等待。"
        zh["CONSOLE_FAILED_REFRESH_OFFICIAL_NO_RESPONSE"] = "服务器无响应"
        zh["CONSOLE_FAILED_OPERATE"] = "操作失败："
        zh["CONSOLE_FILE_EXISTS"] = "文件“%s”已存在"
        zh["CONSOLE_INCORRECT_JAVA"] =
            "请通过 “config javaPath <Java路径>”或“version <版本> --config=javaPath <Java 路径>” 修改一个正确的 Java 路径"
        zh["CONSOLE_FAILED_START"] = "启动游戏失败"
        zh["CONSOLE_START_COMMAND"] = "启动命令："
        zh["CONSOLE_NO_SELECTED_VERSION"] =
            "请使用“-s <版本名称>”以选择一个版本以启动，或通过“cmcl <版本名称>”带上版本名称以启动。"
        zh["CONSOLE_EMPTY_LIST"] = "列表为空"
        zh["CONSOLE_LACK_LIBRARIES_WHETHER_DOWNLOAD"] = "您缺少了以上启动游戏的必要依赖库。是否下载它们？"
        zh["CONSOLE_FAILED_LIST_VERSIONS"] = "获得版本列表失败：%s"
        zh["CONSOLE_INSTALL_SHOW_INCORRECT_TIME"] = "%s：不正确的时间格式或第一个时间大于第二个时间。"
        zh["CONSOLE_REPLACE_LOGGED_ACCOUNT"] = "您已经登录了此账号（序号为 %d），是否覆盖原来的账号？"
        zh["CONSOLE_ACCOUNT_UN_OPERABLE_NEED_UUID_AND_URL_AND_TOKEN"] =
            "如果是外置账号，必须有UUID、accessToken以及外置登录服务器的地址才能进行此操作，可尝试通过“account --refresh”刷新账号或重新登录。"
        zh["CONSOLE_ACCOUNT_UN_OPERABLE_MISSING_INFO"] =
            "必须是正版账号、外置账号或统一通行证登录且有 UUID 才能进行此操作。如果是外置账号还需要目标服务器地址，如果是统一通行证还需要目标服务器ID，可尝试通过“account --refresh”刷新账号或重新登录。"
        zh["CONSOLE_INPUT_INT_WRONG"] = "请输入一个正确的、在范围内的数字。"
        zh["CONSOLE_INPUT_STRING_NOT_FOUND"] = "找不到“%s”。"
        zh["CONSOLE_ONLY_HELP"] = "请使用选项 -h 或 --help 以获取帮助文档。"
        zh["CONSOLE_IMMERSIVE_WRONG"] = "不正确的命令：%s。请输入 help 以获取帮助文档并仔细阅读与沉浸模式相关的文字。"
        zh["CONSOLE_IMMERSIVE_NOT_FOUND"] = "%s：找不到命令。请输入 help 以获取帮助文档。"
        zh["CONSOLE_UNKNOWN_COMMAND_OR_MEANING"] = "%s：未知命令或意义不明。请使用选项 -h 或 --help 以获取帮助文档。"
        zh["CONSOLE_IMMERSIVE_MISSING_PARAMETER"] = "缺少参数。请输入 help 以获取帮助文档。"
        zh["CONSOLE_NOT_FOUND_VERSION_OR_OPTION"] =
            "找不到以“%s”为名的可启动版本或选项。您可以借助由输入选项 -h 或 --help 以获取的帮助文档检查输入的信息是否有误。"
        zh["CONSOLE_HELP_WRONG_WRITE"] = "正确写法是 -h 或 --help，不携带参数值，而不是“%s”。"
        zh["CONSOLE_UNKNOWN_USAGE"] = "未知用法：%s。请使用选项 -h 或 --help 以获取帮助文档。"
        zh["CONSOLE_ARG_CHECKING_ONE"] = "%s：选项用法错误或不应在此出现。请使用选项 -h 或 --help 以获取帮助文档"
        zh["CONSOLE_ARG_CHECKING_PLURAL"] =
            "以下选项用法错误或不应在此出现。请使用选项 -h 或 --help 以获取帮助文档。\n%s"
        zh["CONSOLE_ASK_EXIT_WITH_MC"] =
            "请问是否需要退出启动器时顺便退出游戏（可通过“config exitWithMinecraft true/false”开启或关闭）？"
        zh["CONSOLE_ASK_PRINT_STARTUP_INFO"] =
            "请问是否需要启动游戏时打印启动信息（如Java 路径、最大内存、登录的账号等，可通过“config printStartupInfo true/false”开启或关闭）？"
        zh["CONSOLE_ASK_CHECK_ACCOUNT"] =
            "请问是否需要在启动游戏之前检查账号是否可用（启动前会花时间，可通过“config checkAccountBeforeStart true/false”开启或关闭）？"
        zh["CONSOLE_CHOOSE_DOWNLOAD_SOURCE_CF_OR_MR"] = "请选择下载源(默认为%d，存储为配置 modDownloadSource)："
        zh["DATATYPE_STRING"] = "文本"
        zh["DATATYPE_INTEGER"] = "整数"
        zh["DATATYPE_BOOLEAN"] = "布尔值"
        zh["DATATYPE_FRACTION"] = "小数"
        zh["TIME_FORMAT"] = "yyyy年MM月dd日 HH:mm:ss"
        zh["ACCOUNT_TYPE_MICROSOFT"] = "微软账户"
        zh["ACCOUNT_TYPE_OFFLINE"] = "离线账号"
        zh["ACCOUNT_TYPE_OAS"] = "外置账号"
        zh["ACCOUNT_TYPE_NIDE8AUTH"] = "统一通行证"
        zh["ACCOUNT_TYPE_NIDE8AUTH_WITH_DETAIL"] = "统一通行证：%s %s"
        zh["ACCOUNT_NOT_EXISTS"] = "账号不存在：%d"
        zh["ACCOUNT_TYPE_OAS_WITH_DETAIL"] = "外置账号：%s %s"
        zh["ACCOUNT_INVALID"] = "账号无效：%d"
        zh["ACCOUNT_TIP_LOGIN_OFFLINE_PLAYERNAME"] = "请输入离线登录玩家名："
        zh["ACCOUNT_TIP_LOGIN_OAS_ADDRESS"] = "请输入外置登录服务器地址："
        zh["ACCOUNT_TIP_LOGIN_NIDE8AUTH_SERVER_ID"] = "请输入统一通行证服务器ID："
        zh["ACCOUNT_LOGIN_UNKNOWN_LOGIN_METHOD"] = "未知登录方式：%s。请使用选项 -h 或 --help 以获取帮助文档。"
        zh["ACCOUNT_LOGIN_NEED_NAME"] = "请指定 -n<用户名> 或 --name=<用户名>。"
        zh["ACCOUNT_LOGIN_NEED_ADDRESS"] = "请指定 --address=<服务器地址>。"
        zh["ACCOUNT_LOGIN_NEED_SERVER_ID"] = "请指定 --serverId=<服务器ID>。"
        zh["ACCOUNT_MICROSOFT_REFRESH_NOT_SAME"] = "貌似你在网站登录的账号不是你在本地登录的账号。"
        zh["NOT_SELECTED_AN_ACCOUNT"] =
            "未选择账号。请登录了您的账号之后，使用“account -l”列出账号，记住您要选择的账号的序号，然后使用“account -s<序号>”选择账号；或者在登录账号命令后面加上“-s”的选项。"
        zh["DATATYPE_JSON_ARRAY"] = "JSON数组"
        zh["DATATYPE_JSON_OBJECT"] = "JSON对象"
        zh["INPUT_ACCOUNT"] = "账号："
        zh["INPUT_PASSWORD"] = "密码："
        zh["FAILED_TO_LOGIN_OTHER_AUTHENTICATION_ACCOUNT"] = "登录外置账号失败：%s"
        zh["FAILED_TO_LOGIN_YGGDRASIL_ACCOUNT_UNAVAILABLE_SERVER"] = "目标服务器访问失败"
        zh["FAILED_TO_LOGIN_OAA_NO_SELECTED_CHARACTER"] = "登录失败，请选择一个可用的角色后重试。"
        zh["FAILED_TO_LOGIN_NIDE8AUTH_ACCOUNT"] = "登录统一通行证失败：%s"
        zh["WARNING_SHOWING_PASSWORD"] = "警告：在非控制台执行该操作，您的密码将不会被隐藏！"
        zh["WARNING_VCFG_JAVA_INCORRECT"] = "警告：独立版本配置的 Java 路径不存在或无效，已使用全局配置值！"
        zh["WARNING_VCFG_MAX_MEMORY_INCORRECT"] = "警告：独立版本配置的最大内存小于或等于零，已使用全局配置值！"
        zh["WARNING_VCFG_WINDOW_SIZE_WIDTH_INCORRECT"] = "警告：独立版本配置的窗口宽小于或等于零，已使用全局配置值！"
        zh["WARNING_VCFG_WINDOW_SIZE_HEIGHT_INCORRECT"] = "警告：独立版本配置的窗口高小于或等于零，已使用全局配置值！"
        zh["FILE_NOT_FOUND_OR_IS_A_DIRECTORY"] = "找不到目标文件或目标文件是个目录"
        zh["SUCCESSFULLY_SET_SKIN"] = "设置皮肤成功"
        zh["UNAVAILABLE_AUTHLIB_ACCOUNT_REASON"] = "authlib-injector 配置错误：%s"
        zh["UNAVAILABLE_NIDE8AUTH_ACCOUNT_REASON"] = "统一通行证配置错误：%s"
        zh["UNAVAILABLE_AUTHLIB_ACCOUNT"] = "外置账号不可用，游戏将使用离线账号。"
        zh["UNAVAILABLE_NIDE8AUTH_ACCOUNT"] = "统一通行证不可用，游戏将使用离线账号。"
        zh["UNAVAILABLE_CUSTOM_SKIN"] = "自定义皮肤不可用"
        zh["PRINT_COMMAND_NOT_SUPPORT_OFFLINE_CUSTOM_SKIN"] =
            "注意：如果你是离线账号，使用命令启动游戏，自定义皮肤将不可用。"
        zh["EMPTY_UUID"] = "UUID为空"
        zh["EMPTY_PLAYERNAME"] = "玩家名称为空"
        zh["ONLY_OFFLINE"] = "该功能仅支持离线账号"
        zh["UPLOAD_SKIN_ONLY_OAS_OR_OFFLINE"] = "此功能微软账户与统一通行证不可用。"
        zh["SKIN_TYPE_DEFAULT_OR_SLIM"] = "是否将皮肤模型设置为苗条（Alex）？"
        zh["SKIN_STEVE_UNABLE_READ"] = "设置失败，Steve 皮肤文件读取失败！"
        zh["SKIN_ALEX_UNABLE_READ"] = "设置失败，Alex 皮肤文件读取失败！"
        zh["SKIN_STEVE_NOT_FOUND"] = "设置失败，找不到 Steve 皮肤文件！"
        zh["SKIN_ALEX_NOT_FOUND"] = "设置失败，找不到 Alex 皮肤文件！"
        zh["SKIN_CANCEL_ONLY_FOR_OFFLINE"] = "取消设置皮肤仅对离线账号有效。"
        zh["CAPE_FILE_NOT_FOUND"] = "找不到披风文件“%s”"
        zh["CAPE_FILE_FAILED_LOAD"] = "披风文件“%s”加载失败"
        zh["UNABLE_TO_START_OFFLINE_SKIN_SERVER"] = "无法使用离线账号自定义皮肤"
        zh["UNABLE_OFFLINE_CUSTOM_SKIN_STEVE_NOT_FOUND"] = "无法使用自定义皮肤：找不到 Steve 皮肤文件！"
        zh["UNABLE_OFFLINE_CUSTOM_SKIN_ALEX_NOT_FOUND"] = "无法使用自定义皮肤：找不到 Alex 皮肤文件！"
        zh["UNABLE_OFFLINE_CUSTOM_SKIN_STEVE_UNABLE_LOAD"] = "无法使用自定义皮肤：读取 Steve 皮肤文件失败！"
        zh["UNABLE_OFFLINE_CUSTOM_SKIN_ALEX_UNABLE_LOAD"] = "无法使用自定义皮肤：读取 Alex 皮肤文件失败！"
        zh["UNABLE_OFFLINE_CUSTOM_SKIN_FILE_NOT_FOUND"] = "无法使用自定义皮肤：找不到皮肤文件“%s”"
        zh["UNABLE_OFFLINE_CUSTOM_SKIN_FILE_FAILED_LOAD"] = "无法使用自定义皮肤：皮肤文件“%s”加载失败"
        zh["UNABLE_GET_VERSION_INFORMATION"] = "读取版本信息失败"
        zh["UNABLE_SET_SKIN"] = "设置皮肤失败"
        zh["INSTALL_MODLOADER_FAILED_TO_GET_INSTALLABLE_VERSION"] = "无法安装 %s：获得可安装的版本失败。"
        zh["INSTALL_MODLOADER_NO_INSTALLABLE_VERSION"] =
            "无法安装 %1\$s：没有可安装的 %1\$s 版本，可能是因为 %1\$s 不支持此游戏版本。"
        zh["INSTALL_MODLOADER_FAILED_TO_GET_TARGET_JSON"] = "无法安装 %1\$s：获得目标 %1\$s JSON 失败。"
        zh["INSTALL_MODLOADER_SELECT"] = "请输入您要安装的 %1\$s 版本(默认选择%2\$s)："
        zh["INSTALL_MODLOADER_SELECT_NOT_FOUND"] = "版本“%1\$s”找不到，请输入您要安装的 %2\$s 版本(默认选择%3\$s)："
        zh["INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE"] = "请问是否继续安装原版（无 %s）？"
        zh["INSTALL_MODLOADER_FAILED_TO_PARSE_TARGET_JSON"] = "无法安装 %1\$s：解析目标 %1\$s JSON 失败。"
        zh["INSTALL_MODLOADER_ALREADY_INSTALL"] = "无法安装 %1\$s：目标版本已安装 %1\$s。"
        zh["INSTALL_MODLOADER_EMPTY_MC_VERSION"] = "无法安装 %s：无法获得目标版本的游戏版本。"
        zh["INSTALL_MODLOADER_FAILED_WITH_REASON"] = "安装 %s 失败：%s"
        zh["INSTALL_MODLOADER_ALREADY_INSTALL_ANOTHER_ONE"] =
            "无法安装 %1\$s：目标版本已安装 %2\$s，%2\$s 和 %1\$s 不能共存。"
        zh["INSTALL_MODLOADER_FAILED_DOWNLOAD"] = "无法安装 %s：下载文件失败"
        zh["INSTALL_MODLOADER_DOWNLOADING_FILE"] = "下载文件中..."
        zh["INSTALL_MODLOADER_NO_INSTALLABLE_VERSION_2"] = "无法安装 %1\$s：没有可安装的 %1\$s 版本。"
        zh["INSTALL_MODLOADER_FAILED_UNKNOWN_TYPE"] = "无法安装 %1\$s：未知 %1\$s 类型。"
        zh["INSTALL_MODLOADER_FAILED_MC_VERSION_MISMATCH"] =
            "无法安装 %1\$s：目标 %1\$s 的游戏版本与目标游戏版本不匹配。"
        zh["INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION"] = "\${NAME} 版本“%s”找不到。"
        zh["INSTALL_MODLOADER_SELECT_NOT_FOUND_GAME_OR_TARGET_EXTRA"] = "找不到目标游戏版本或 \${NAME} 版本。"
        zh["INSTALL_SHOW_UNKNOWN_TYPE"] = "%s：未知版本类型。请使用选项 -h 或 --help 以获取帮助文档。"
        zh["INSTALL_COEXIST"] = "%1\$s 与 %2\$s 不能共存。请使用选项 -h 或 --help 以获取帮助文档。"
        zh["INSTALL_FABRIC_API_WITHOUT_FABRIC"] = "没装 Fabric，怎么装 Fabric API 呢？"
        zh["INSTALL_MODPACK_FAILED_DOWNLOAD_MOD"] = "下载 projectId 为 %d 的模组失败：%s"
        zh["INSTALL_MODPACK_EACH_MOD_GET_URL"] = "正在遍历获得各个模组（文件）的下载链接，请耐心等待"
        zh["INSTALL_MODPACK_COMPLETE"] = "安装整合包完成"
        zh["INSTALL_MODPACK_MODRINTH_UNKNOWN_MODLOADER"] = "未知模组加载器：%s"
        zh["INSTALL_OPTIFINE_INCOMPATIBLE_WITH_FORGE_17"] =
            "无法安装 OptiFine：当前游戏版本的 Forge 与低于 H1 Pre2 版本的 OptiFine 不兼容，请换一个版本更新的 OptiFine 后重试。"
        zh["INSTALLED_MODLOADER"] = "安装 %s 成功"
        zh["VERSION_INFORMATION_GAME_VERSION"] = "   游戏版本：       "
        zh["VERSION_INFORMATION_RELEASE_TIME"] = "   版本发布时间：   "
        zh["VERSION_INFORMATION_FABRIC_VERSION"] = "   Fabric 版本：    "
        zh["VERSION_INFORMATION_FORGE_VERSION"] = "   Forge 版本：     "
        zh["VERSION_INFORMATION_JAVA_COMPONENT"] = "   Java 组件：      "
        zh["VERSION_INFORMATION_JAVA_VERSION"] = "   Java 版本要求：  "
        zh["VERSION_INFORMATION_ASSETS_VERSION"] = "   资源版本：       "
        zh["VERSION_INFORMATION_LITELOADER_VERSION"] = "   LiteLoader 版本："
        zh["VERSION_INFORMATION_OPTIFINE_VERSION"] = "   OptiFine 版本：  "
        zh["VERSION_INFORMATION_QUILT_VERSION"] = "   Quilt 版本：     "
        zh["VERSION_INFORMATION_NEOFORGE_VERSION"] = "   NeoForge 版本：  "
        zh["VERSION_INFORMATION_VERSION_TYPE"] = "   版本类型：       "
        zh["VERSION_INFORMATION_VERSION_TYPE_RELEASE"] = "正式版"
        zh["VERSION_INFORMATION_VERSION_TYPE_SNAPSHOT"] = "快照版"
        zh["VERSION_INFORMATION_VERSION_TYPE_OLD_BETA"] = "远古 Beta 版"
        zh["VERSION_INFORMATION_VERSION_TYPE_OLD_ALPHA"] = "远古 Alpha 版"
        zh["VERSION_INFORMATION_VERSION_PATH"] = "   版本位置：       "
        zh["VERSION_INFORMATION_GAME_VERSION_FAILED_GET"] = "获取失败"
        zh["VERSION_UNKNOWN_COMPLETING"] = "未知补全项：%s。仅支持 assets、libraries、natives。"
        zh["VERSION_COMPLETE_LIBRARIES_NO_NEED_TO"] = "没有缺少的依赖库需要补全。"
        zh["CF_FAILED_TO_SHOW_SOMEONE"] = "显示第%d个\${NAME}失败：%s"
        zh["CF_AUTHOR_MORE"] = "等%d位作者"
        zh["CF_SELECT_TARGET"] = "请选择目标\${NAME}(%d-%d)："
        zh["CF_SUPPORTED_GAME_VERSION"] = "%s 支持的游戏版本："
        zh["CF_INPUT_GAME_VERSION"] = "请输入您要下载的版本："
        zh["CF_INPUT_VERSION"] = "请选择您要下载的\${NAME}版本(%d-%d，-1为取消下载)："
        zh["CF_STORAGE_FILE_EXISTS"] = "请输入一个存储该\${NAME}文件的目录："
        zh["CF_NO_VERSION_FOR_GAME_VERSION"] = "没有适用于此游戏版本的%s版本。"
        zh["CF_INFORMATION_NOTHING"] = "无任何关于此%s的可展示信息"
        zh["CF_INFORMATION_MOD_NAME"] = "   模组名称：          "
        zh["CF_INFORMATION_MOD_ID"] = "   模组ID：            "
        zh["CF_INFORMATION_MODPACK_NAME"] = "   整合包名称：        "
        zh["CF_INFORMATION_MODPACK_ID"] = "   整合包ID：          "
        zh["CF_INFORMATION_AUTHORS"] = "   作者：              "
        zh["CF_INFORMATION_AUTHOR"] = "   作者：              "
        zh["CF_INFORMATION_SUMMARY"] = "   简介：              "
        zh["CF_INFORMATION_ICON"] = "   图标：              "
        zh["CF_INFORMATION_LATEST_GAME_VERSION"] = "   最新支持的游戏版本："
        zh["CF_INFORMATION_DATE_MODIFIED"] = "   修改日期：          "
        zh["CF_INFORMATION_DOWNLOAD_COUNT"] = "   总下载量：          "
        zh["CF_INFORMATION_DATE_CREATED"] = "   创建日期：          "
        zh["CF_INFORMATION_DATE_RELEASED"] = "   发布日期：          "
        zh["CF_INFORMATION_ISSUE_TRACKER_URL"] = "   问题反馈：          "
        zh["CF_INFORMATION_SOURCE_URL"] = "   源代码仓库：        "
        zh["CF_INFORMATION_WEBSITE_URL"] = "   网页介绍：          "
        zh["CF_INFORMATION_WIKI_URL"] = "   维基网站：          "
        zh["CF_INFORMATION_CATEGORIES"] = "   类别：              "
        zh["CF_INFORMATION_DISCORD_URL"] = "   Discord 链接：      "
        zh["CF_INFORMATION_DONATION"] = "   捐赠：              "
        zh["CF_INFORMATION_DONATION_URL"] = "         地址："
        zh["CF_INFORMATION_AUTHOR_URL"] = "         主页："
        zh["CF_GET_BY_ID_FAILED"] =
            "无法获得目标\${NAME}：%s\n出现该错误有可能的原因：\n1.目标\${NAME}不存在\n2.网络异常\n3.服务器出现问题"
        zh["CF_GET_BY_ID_NOT_OF_MC"] = "目标游戏组件不是 Minecraft 的\${NAME}，该组件的游戏ID为%d。"
        zh["CF_DEPENDENCIES_TIP"] = "此\${NAME}需要以下前置\${NAME}才能正常运行，将会先安装以下前置\${NAME}。"
        zh["CF_DEPENDENCY_INFORMATION_ID"] = "   ID：  %d"
        zh["CF_DEPENDENCY_INFORMATION_ID_STRING"] = "   ID：  %s"
        zh["CF_DEPENDENCY_INFORMATION_NAME"] = "   名称：%s"
        zh["CF_BESEARCHED_MOD_ALC"] = "模组"
        zh["CF_BESEARCHED_MOD_FUC"] = "模组"
        zh["CF_BESEARCHED_MODPACK_ALC"] = "整合包"
        zh["CF_BESEARCHED_MODPACK_FUC"] = "整合包"
        zh["CF_GET_BY_ID_INCORRECT_CATEGORY"] = "目标游戏组件不是一个\${NAME}，该组件的类别ID为%d。"
        zh["CF_GET_BY_ID_INCORRECT_CATEGORY_DETAIL"] = "目标游戏组件不是一个\${NAME}，该游戏组件是一个\${TARGET}。"
        zh["CF_STORAGE_FILE_EXISTS_OPERATIONS"] = "[0]覆盖目标文件  [1]存储到其他目录  [2]取消下载"
        zh["CF_STORAGE_FILE_EXISTS_SELECT_OPERATION"] = "文件“%s”已存在，请选择您的操作(0-2)："
        zh["MOD_FAILED_TO_GET_ALL_FILES"] = "获得\${NAME}文件列表失败：%s"
        zh["MOD_UNKNOWN_SOURCE"] = "%s：未知下载源。使用选项 -h 或 --help 以获取更多信息。"
        zh["MOD_CONTAINS_BOTH_NAME_AND_ID"] = "-n 或 --name 与 --id 不能同时存在。"
        zh["MOD_CONTAINS_BOTH_NOT_NAME_AND_ID"] =
            "必须指定 -n 或 --name 或 --id。使用选项 -h 或 --help 以获取更多信息。"
        zh["MOD_SEARCH_LIMIT_GREATER_THAN_FIFTY"] = "如果下载源为 CurseForge，限制数量最大为50。"
        zh["MOD_ID_LIMIT_COEXIST"] = "都不用-n或者--name的搜索功能咯，还怎么用--limit限制搜索结果嘞？"
        zh["MOD_CONTAINS_NOTHING"] = "必须指定 --install、--info 或 --url。"
        zh["MOD_CONTAINS_TWO_OR_MORE"] = "--install、--info 或 --url 只能存在一个。"
        zh["NO_SEARCH_RESULTS"] = "无任何搜索结果。"
        zh["DOWNLOAD_SOURCE_OFFICIAL"] = "官方"
        zh["DOWNLOAD_SOURCE_BMCLAPI"] = "BMCLAPI"
        zh["MODPACK_CONTAINS_TWO_OR_MORE"] = "--install、--info、--file 或 --url 只能存在一个。"
        zh["MODPACK_CONTAINS_NOTHING"] = "必须指定 --install、--info、--file 或 --url。"
        zh["YES_SHORT"] = "是"
        zh["TABLE_ACCOUNTS_LIST_HEADER_SELECTED"] = "已选择"
        zh["TABLE_ACCOUNTS_LIST_HEADER_ORDER"] = "序号"
        zh["TABLE_ACCOUNTS_LIST_HEADER_NAME"] = "名称"
        zh["TABLE_ACCOUNTS_LIST_HEADER_TYPE"] = "账号类型"
        zh["TABLE_ACCOUNTS_LIST_HEADER_OTHER_INFORMATION"] = "其他信息"
        zh["TABLE_CONFIG_ALL_NAME"] = "配置名"
        zh["TABLE_CONFIG_ALL_TYPE"] = "类型"
        zh["TABLE_CONFIG_ALL_VALUE"] = "配置值"
        zh["TABLE_CONFIG_ALL_VIEW_SEPARATELY"] = "请通过“cmcl config %s”单独查看"
        zh["TABLE_SETTABLE_CONFIG_NAME"] = "配置名"
        zh["TABLE_SETTABLE_CONFIG_TYPE"] = "类型"
        zh["TABLE_SETTABLE_CONFIG_MEANING"] = "含义"
        if (Constants.isDebug()) println("init SimplifiedChinese textMap finish")
        zh
    }

    override val helpMap: Map<String, String> by lazy {
        if (Constants.isDebug()) println("init SimplifiedChinese helpMap start")
        val zhHelp: MutableMap<String, String> = HashMap()
        //"     -i, --immersive           进入沉浸模式*\n" +
        /*"\n" +
                      " *沉浸模式：在此模式下，执行上方的命令只能使用完整的选项，如help、list、print；\n" +
                      "            执行下方的命令无需输入“cmcl”，如config maxMemory 2048；\n" +
                      "            启动游戏应使用 start [<版本>]。\n" +*/
        zhHelp["ROOT"] =
            """Console Minecraft Launcher v${Constants.CMCL_VERSION_NAME}：一个使用 Kotlin 编写的命令行 Minecraft Java 版启动器

注：
  中括号内的内容是可选的。
  选项中的逗号意为两边的选项都能实现相同的功能。
  对于给选项指定内容，
  仅能在缩略选项（一条横杠）后面直接加上内容（无需空格），如cmcl -lD:\.minecraft，
  仅能在完整选项（两条横杠）后面加上等号再输入内容，如cmcl --list=D:\.minecraft，
  或在此两者后加上空格再输入内容，如cmcl -l D:\.minecraft；cmcl --list D:\.minecraft，
  详细请参考选项说明后面的例子。

用法：
cmcl [<版本>]                  启动通过“cmcl -s <版本>”选择的版本或<版本>所指定的版本。
                                  例：cmcl 1.19：启动1.19；
                                  假如现在选择的版本是1.18.2，直接运行（无参数），则为启动1.18.2。
     -h, --help                获得帮助文档
     -l, --list[=<游戏目录>]   列出当前游戏目录或<游戏目录>内的所有游戏版本。
                                  例：cmcl -l；cmcl --list=D:\.minecraft
     -p, --print[=<版本>]      打印选择的版本或<版本>的启动命令
                                  例：cmcl -p；cmcl --print=1.19
     -s, --select=<版本>       选择版本，选择后可直接使用“cmcl”进行启动
     -a, --about               显示关于信息
     -c, --check-for-updates   检查更新
     <功能> <选项>...          把“所有功能”中的功能名带入<功能>中（无需横
                               杠），把选项放在功能名后方，以执行相应的操作。
                                  cmcl install -h

 所有功能：
    功能名    作用
    install   安装版本
    version   版本操作
    account   账号操作
    config    更改启动器配置
    simplify  设置简化命令
    jvmArgs   自定义JVM参数
    gameArgs  自定义游戏参数
    mod       模组搜索与安装
    modpack   整合包搜索与安装
    
每个功能都能使用 -h 或 --help 获取帮助文档以获取相关的选项。功能帮助文档中，说明后面的内容是用法示例。"""
        zhHelp["install"] = """安装版本
  功能名：install
  用法一：install <要安装的版本> <选项>...   安装版本   cmcl install 1.18.2
  选项：
   -n, --name=<存储的版本名称>               指明版本存储在本地的名称。
                                                cmcl install 1.18.2 -n my1.18.2
   -s, --select                              安装完选择该版本
                                                cmcl install 1.18.2 -s
   --fabric[=<Fabric 版本>]                  安装 Fabric，可选择是否安装 Fabric API 并指定版本。
      [--api[=<Fabric API 版本>]]            与 Forge、LiteLoader、OptiFine、Quilt 不兼容。
                                                cmcl install 1.19 --fabric=0.14.12 --api
   --forge[=<Forge 版本>]                    安装 Forge
                                                cmcl install 1.19 --forge
   --liteloader[=<LiteLoader 版本>]          安装 LiteLoader
                                                cmcl install 1.12 --liteloader=1.12-SNAPSHOT
   --optifine[=<OptiFine 版本>]              安装 OptiFine
   --quilt[=<Quilt 版本>]                    安装 Quilt。与 Forge、LiteLoader、OptiFine、Fabric 不兼容。
   -t, --thread=<线程数>                     设置下载资源文件的线程数（默认为64）
                                                cmcl install 1.19 --thread=96
   --no-assets                               不下载资源文件
   --no-libraries                            不下载依赖库文件
   --no-natives                              不下载原生依赖库文件
  用法二：install --show=<版本类型> [-t, --time=<时间范围>]
             显示可安装的版本。
             版本类型：全部：a/all；正式版：r/release；快照版：s/snapshot；
                       远古alpha版：oa/oldAlpha；远古beta版：ob/oldBeta。
             时间范围格式：<从年>-<从月>-<从日>/<到年>-<到月>-<到日>
                cmcl install --show=all                            显示所有可安装版本
                cmcl install --show=r                              显示所有正式版
                cmcl install --show=s --time=2020-05-09/2021-10-23 显示从2020年5月9日到2021年10月23日的快照版本"""
        zhHelp["version"] = """版本
  功能名：version
  基本用法：version [<目标版本>] <选项>...   操作目标版本或已选择版本
  选项：
   --info                                查看版本信息。cmcl version 1.19 --info
   -d, --delete                          删除版本。    cmcl version -d
   --rename=<新名称>                     重命名版本
   --complete[=assets|libraries|natives] 补全资源（assets）、依赖库（libraries）或原生依赖库（natives），
    [-t, --thread=<线程数>]              若不指明补全哪个内容，则为补全使用模组加载器安装的版本。补全资源时
                                         也可以通过指定-t, --thread=<线程数>以指定线程数。
                                            cmcl version 1.19 --complete
                                            cmcl version --complete=assets
   --config=<配置名称> [<配置内容>]      为版本单独设置配置，使用“config --view”查看的内容中带有
                                         “[游戏相关]”的即为可设置的配置。不输入<配置内容>则为使用全局配置。
   --fabric[=<Fabric 版本>]              为版本安装 Fabric，可以指明版本。加上--api为安装 Fabric API，也可以指定版本。
      [--api[=<Fabric API 版本>]]           cmcl version 1.19 --fabric --api=0.58.0
   --forge[=<Forge 版本>]                为版本安装 Forge，可以指明版本。
                                            cmcl version 1.19 --forge=42.0.0
   --liteloader[=<LiteLoader 版本>]      为版本安装 LiteLoader，可以指明版本。
   --optifine[=<OptiFine 版本>]          为版本安装 OptiFine，可以指明版本。
   --quilt[=<Quilt 版本>]                为版本安装 Quilt，可以指明版本。
   --isolate                             设置版本隔离（覆盖gameDir配置）
   --unset-isolate                       取消设置版本隔离
   -p, --print-command                   打印启动命令
   --export-script=<脚本文件>            导出启动脚本（Windows 下为 bat 格式否则为 sh 格式）
   --export-script-ps=<脚本文件>         导出 PowerShell 启动脚本（.ps1）"""
        zhHelp["account"] = """账号
  功能名：account
  注：账号的序号可通过 -l 或 --list 获取。
  选项：
   -s, --select=<序号>                   选择账号。          cmcl account -s 3
   -l, --list                            列出所有账号。      cmcl account --list
   -d, --delete=<序号>                   删除账号。          cmcl account --delete=4
   -r, --refresh                         刷新当前选择的账号。cmcl account --refresh
   --cape[=<披风文件路径>]               自定义披风，如果不输入则为取消设置披风。此功能仅离线账号可用*。
   --download-skin=<皮肤文件存储路径>    下载皮肤文件。      cmcl account --download-skin=D:\mySkin.png
   --skin[=steve|alex|<皮肤文件路径>]    设置皮肤为Steve、Alex，或自定义皮肤，如果是离线账号，不输入则为取消设置皮肤。
                                         此功能微软账户与统一通行证不可用*。
                                            cmcl account --skin
                                            cmcl account --skin=steve
                                            cmcl account --skin=D:\handsome.png
   --login=offline|microsoft|authlib|nide8auth [-s, --select]
       登录账号（并选择）。
         offline 为登录离线账号，需要注明的内容：-n, --name=<用户名>，
            cmcl account --login=offline --name=Alexander
         microsoft 为登录微软账户，不需要注明内容，
            cmcl account --login=microsoft
         authlib 为外置登录，需要注明：--address=<服务器地址>，
            cmcl account --login=authlib --address=127.0.0.1
         nide8auth 为统一通行证登录，需要注明：--serverId=<服务器ID>，
            cmcl account --login=nide8auth --serverId=1234567890abcdef1234567890abcdef
   * 某些账号不支持某些功能，因无可供调用的接口，请自行到相应的网站进行此操作。"""
        zhHelp["config"] = """配置
  功能名：config
  选项：
   <配置名> [<配置值>]       若<配置值>不为空，则为设置配置，通过-v查看可设置配置；
                             否则为输出<配置名>所对应的配置值。
                                cmcl config javaPath       输出 Java 路径
                                cmcl config maxMemory 2048 修改最大内存
   -a, --all                 输出全部配置内容
                                cmcl config -a
   --getRaw[=<缩进空格数>]   输出配置原内容，<缩进空格数>默认为2。
                                cmcl config --getRaw
   -d, --delete=<配置名>     删除<配置名>所对应的配置。
                                cmcl config -d javaPath
                                cmcl config --delete=javaPath
   -c, --clear               清空配置。
                                cmcl config --clear
   -v, --view                查看所有可设置的配置。
                                cmcl config -v"""
        zhHelp["simplify"] = """设置简化命令
  功能名：simplify
  选项：
   -p, --print                     查看所有已设置的简化命令。
                                      cmcl simplify -p
   -s, --set=<简化命令> "<原命令>" 设置或修改简化命令。设置后，输入“cmcl <简化命令>”就能运行“cmcl <原命令>”。如示例，
                                   设置后输入“cmcl ds2”就能实现“cmcl config downloadSource 2”的功能，更加简便。
                                      cmcl simplify -s ds2 "config downloadSource 2"
   -d, --delete=<简化命令>         删除简化命令。
                                      cmcl simplify -d ds2"""
        zhHelp["jvmArgs"] = """自定义JVM参数
  功能名：jvmArgs
  选项：
   -p, --print[=<缩进空格数>] [-v, --version=<版本>]    输出所有参数，缩进的空格数默认为2，可指定版本。
                                                           cmcl jvmArgs -p2 -v1.19
   -a, --add=<参数内容> [-v, --version=<版本>]          添加参数，可指定版本。为了防止解析错误，请为内容加上双引
                                                        号并且使用等号指定内容。注意：此处不能使用空格代替等于号。
                                                           cmcl jvmArgs --add="-Dfile.encoding=UTF-8"
   -d, --delete=<序号，从0开始> [-v, --version=<版本>]  删除参数，可指定版本。
                                                           cmcl jvmArgs --delete=2 --version=1.19"""
        zhHelp["gameArgs"] = """自定义游戏参数
  功能名：gameArgs
  选项：
   -p, --print[=<缩进空格数>] [-v, --version=<版本>]                输出所有参数，缩进的空格数默认为2，可指定版本。
                                                                       cmcl gameArgs --print --version=1.19
   -a, --add=<参数名称> [<参数值>] [-v, --version=<版本>]           添加参数，可指定版本。
                                                                       cmcl gameArgs -a width 256
   -d, --delete=<参数名称> [-v, --version=<版本>]                   删除参数，可指定版本。
                                                                       cmcl gameArgs --delete=demo"""
        zhHelp["mod"] = """模组
  功能名：mod
  注：<下载源>可输入cf或curseforge（CurseForge）、mr或modrinth（Modrinth）。
  选项：
   --url=<模组地址> 从互联网下载模组
   --install
      [--source=<下载源>]
      -n, --name=<模组名称>|--id=<模组ID>
      [--limit=<限制搜索结果数量>]
      [--game-version=<游戏版本>]
      [-v, --version=<模组版本>]
     通过模组名称或ID搜索并安装模组。通过模组名称搜索时，结果数量默认为50，
     可以对其进行限制。若<下载源>为 CurseForge，限制数量最大为50。
        cmcl mod --install -nMinis --limit=30
        cmcl mod --install --source=curseforge --id=297344
        cmcl mod --install --name=Sodium --limit=30
        cmcl mod --install --source=mr --id=GBeCx05I
   --info
      [--source=<下载源>]
      -n, --name=<模组名称>|--id=<模组ID>
      [--limit=<限制搜索结果数量>]
     通过模组名称或ID搜索并显示模组信息。通过模组名称搜索时，结果数量默认为50，
     可以对其进行限制。若<下载源>为 CurseForge，限制数量最大为50。"""
        zhHelp["modpack"] = """整合包
  功能名：modpack
  注：<下载源>可输入cf或curseforge（CurseForge）、mr或modrinth（Modrinth）。
      安装整合包时也支持-t, --thread=<线程数>、--no-assets、--no-libraries与--no-natives，
      它们的作用请查看“安装版本”（install）的帮助文档。
  选项：
   --url=<整合包地址> [--storage=<存储的版本名称>]  从互联网下载并安装整合包
   --file=<整合包路径> [--storage=<存储的版本名称>] 安装本地整合包
   --install
      [--source=<下载源>]
      -n, --name=<整合包名称>|--id=<整合包ID>
      [--limit=<限制搜索结果数量>]
      [--storage=<存储的版本名称>]
      [-k, --keep-file]
      [--game-version=<游戏版本>]
      [-v, --version=<整合包版本>]
     通过整合包名称或ID搜索并安装整合包。通过整合包名称搜索时，结果数量默认为50，可以对其进行限制。
     若<下载源>为 CurseForge，限制数量最大为50。加上-k 或 --keep-file 意为安装后保留文件（在目录 .cmcl/modpacks）。
        cmcl modpack --install -nRLCraft --limit=30 --storage="New Game"
        cmcl modpack --install --source=curseforge --id=285109
        cmcl modpack --install --name="Sugar Optimization" --limit=30 --storage NewModpack
        cmcl modpack --install --source=mr --id=BYN9yKrV
   --info
      [--source=<下载源>]
      -n, --name=<整合包名称>|--id=<整合包ID>
      [--limit=<限制搜索结果数量>]
     通过整合包名称或ID搜索并显示整合包信息。通过整合包名称搜索时，结果数量默认为50，
     可以对其进行限制。若<下载源>为 CurseForge，限制数量最大为50。"""
        if (Constants.isDebug()) println("init SimplifiedChinese helpMap finish")
        zhHelp
    }
}
