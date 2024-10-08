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

package com.mrshiehx.cmcl.constants.languages.chinese

import com.mrshiehx.cmcl.constants.languages.Language

object Cantonese : Language {
    //调用到getTextMap()或getHelpMap()再创建Map然后put，不要在初始化就put，
    //因为有些操作不需要获取用于向用户显示的文本，就不用花费这个时间了

    override val textMap: Map<String, String> by lazy {
        val cantonese: MutableMap<String, String> = SimplifiedChinese.textMap.toMutableMap()
        cantonese["MESSAGE_ABOUT_DESCRIPTION_2"] = "一个喺命令行上运行嘅 Minecraft Java 版启动器"
        cantonese["MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_CONTENT_1"] =
            "Minecraft 版权归 Mojang Studios 同 Microsoft 所有，使用CMCL产生嘅所有版权问题，软件制作方概不负责，请支持正版。"
        cantonese["MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_CONTENT_2"] =
            "用户因使用CMCL而产生嘅一切后果由用户自己承担，任何涉及CMCL嘅法律纠纷与冲突同开发者无关，CMCL同开发者将唔会承担任何责任。"
        cantonese["MESSAGE_UNABLE_TO_LOGIN_MICROSOFT"] = "暂时登录唔到，等阵再试啦。"
        cantonese["MESSAGE_OFFICIAL_LOGIN_FAILED_MESSAGE"] = "登录账号失败，可能系因为你嘅账号仲未拥有 Minecraft。"
        cantonese["MESSAGE_STARTING_GAME"] = "启动紧游戏..."
        cantonese["MESSAGE_FAILED_TO_CONNECT_TO_URL"] = "连接到 %s 失败，睇下你嘅网络连接有冇问题啦。"
        cantonese["MESSAGE_VERSIONS_LIST_IS_EMPTY"] = "游戏版本列表系空嘅。"
        cantonese["MESSAGE_INSTALL_INPUT_NAME"] = "请输入新版本嘅名："
        cantonese["MESSAGE_INSTALL_INPUT_NAME_EXISTS"] = "%s：呢个名已经被用咗啦，换个名啦。"
        cantonese["MESSAGE_FAILED_TO_CONTROL_VERSION_JSON_FILE"] = "下载或者解析目标版本嘅 JSON 文件失败：%s"
        cantonese["MESSAGE_INSTALL_NOT_FOUND_JAR_FILE_DOWNLOAD_INFO"] =
            "喺目标版本嘅JSON文件入面搵唔到客户端文件嘅下载信息。"
        cantonese["MESSAGE_INSTALL_JAR_FILE_DOWNLOAD_URL_EMPTY"] = "客户端文件嘅下载链接系空嘅。"
        cantonese["MESSAGE_INSTALL_DOWNLOADING_JAR_FILE"] = "下载紧客户端文件..."
        cantonese["MESSAGE_INSTALL_DOWNLOADING_ASSETS"] = "下载紧资源文件..."
        cantonese["MESSAGE_INSTALL_DOWNLOAD_ASSETS_NO_INDEX"] = "下载资源文件失败，搵唔到资源文件索引。"
        cantonese["MESSAGE_EXCEPTION_DETAIL_NOT_FOUND_URL"] = "搵唔到下载链接"
        cantonese["MESSAGE_INSTALL_DOWNLOADING_LIBRARIES"] = "下载紧依赖库文件..."
        cantonese["MESSAGE_INSTALL_LIBRARIES_LIST_EMPTY"] = "依赖库列表系空嘅"
        cantonese["MESSAGE_INSTALL_DECOMPRESSING_NATIVE_LIBRARIES"] = "解压紧原生依赖库文件..."
        cantonese["MESSAGE_DOWNLOADING_FILE"] = "下载紧 %s"
        cantonese["MESSAGE_DOWNLOADING_FILE_TO"] = "下载紧 %s 到 %s"
        cantonese["MESSAGE_COPYING_FILE"] = "复制紧 %s 到 %s"
        cantonese["MESSAGE_UNZIPPING_FILE"] = "解压紧 %s"
        cantonese["MESSAGE_GAME_CRASH_CAUSE_TIPS"] = "游戏崩溃可能嘅错误：%s"
        cantonese["MESSAGE_GAME_CRASH_CAUSE_URLCLASSLOADER"] =
            "旧版 Minecraft 出现呢个错误可能系因为 Java 版本过高，可以试下用 Java 8 或者更加低嘅版本嚟修复呢个问题。\n你可以用“version <版本> --config=javaPath <Java 路径>”同版本单独设置 Java 路径。"
        cantonese["MESSAGE_GAME_CRASH_CAUSE_LWJGL_FAILED_LOAD"] =
            "出现呢个错误可能系因为原生依赖库缺失或者损坏，请通过“version <版本> --complete=natives”重新下载依赖库文件嚟修复呢个问题。"
        cantonese["MESSAGE_GAME_CRASH_CAUSE_MEMORY_TOO_SMALL"] = "内存唔够，可尝试将内存改为一个更大嘅数。"
        cantonese["MESSAGE_NOT_FOUND_LIBRARY_DOWNLOAD_URL"] = "搵唔到依赖库 %s 嘅下载链接"
        cantonese["MESSAGE_INSTALL_NATIVES_EMPTY_JAR"] = "冇需要解压嘅原生依赖库文件"
        cantonese["MESSAGE_YGGDRASIL_LOGIN_SELECT_PROFILE"] = "拣一个角色(%d-%d)："
        cantonese["MESSAGE_INPUT_VERSION_NAME"] = "请输入要存储为嘅版本名："
        cantonese["MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON_WITH_URL"] =
            "下载文件失败：%s，文件链接为：%s，可自行下载并且储存到 %s 入面"
        cantonese["MESSAGE_FAILED_DOWNLOAD_FILE_WITH_REASON_WITH_URL_WITH_NAME"] =
            "下载文件失败：%s，文件链接为：%s，可自行下载并且储存到 %s 中，并将名改成“%s”"
        cantonese["MESSAGE_INSTALL_MODPACK_UNKNOWN_TYPE"] = "安装整合包失败：唔知整合包系咩类型。"
        cantonese["MESSAGE_INSTALL_MODPACK_NOT_FOUND_GAME_VERSION"] = "安装整合包失败：搵唔到要安装嘅游戏版本。"
        cantonese["MESSAGE_INSTALL_MODPACK_COEXIST"] = "安装整合包失败：%1\$s 同 %2\$s 唔容许同时安装。"
        cantonese["MESSAGE_INSTALL_MODPACK_NOT_SUPPORTED_NEOFORGE"] =
            "唔好意思，本启动器暂时唔支持安装 NeoForge，将无法安装此整合包。"
        cantonese["MESSAGE_COMPLETE_VERSION_IS_COMPLETE"] =
            "呢个版本系完整嘅版本，唔需要去补全。若经检查版本的确系唔完整，请你手动重新安装呢个版本。"
        cantonese["MESSAGE_SELECT_DOWNLOAD_SOURCE"] = "首次下载要拣个下载源(默认系%d，存储为配置 downloadSource)："
        cantonese["MESSAGE_SELECT_ACCOUNT"] = "请输入要拣嘅账号嘅序号(%d-%d)："
        cantonese["MESSAGE_SELECT_ACCOUNT_TYPE"] = "冇可用嘅账号，请选择新账号嘅账号类型(%d-%d)："
        cantonese["MESSAGE_CURRENT_IS_LATEST_VERSION"] = "当前已经系最新版本"
        cantonese["MESSAGE_BEFORE_LIST_VERSIONS"] = "目录 %s 中嘅游戏版本："
        cantonese["MESSAGE_AUTHLIB_ACCOUNT_INCOMPLETE"] = "外置登录账号唔完整，请将佢删咗之后再登录过。"
        cantonese["MESSAGE_NIDE8AUTH_ACCOUNT_INCOMPLETE"] = "统一通行证唔完整，请将佢删咗之后再登录过。"
        cantonese["MESSAGE_AUTHLIB_ACCOUNT_REFRESH_NO_CHARACTERS"] =
            "你嘅角色畀删咗，亦都冇可用嘅角色，请你前往外置登录嘅网站添加角色后重新刷新或登录。唔系嘅话游戏中嘅相关功能你可能用唔到。"
        cantonese["MESSAGE_NIDE8AUTH_ACCOUNT_REFRESH_NO_CHARACTERS"] =
            "你嘅角色畀删咗，亦都冇可用嘅角色，请你前往统一通行证嘅网站添加角色后重新刷新或登录。唔系嘅话游戏中嘅相关功能你可能用唔到。"
        cantonese["MESSAGE_YGGDRASIL_ACCOUNT_REFRESH_OLD_CHARACTER_DELETED"] = "你嘅角色畀删咗，请拣一个新嘅角色。"
        cantonese["MESSAGE_GAME_CRASH_CAUSE_JVM_UNRECOGNIZED_OPTION"] =
            "您加入咗错误嘅JVM参数。通过“jvmArgs --help”嚟获取相关信息。"
        cantonese["MESSAGE_TELL_USER_CHECK_ACCOUNT_CAN_BE_OFF"] =
            "如果你唔想启动之前检查账号可唔可用，可以用“config checkAccountBeforeStart false”或“version <版本> --config=checkAccountBeforeStart false”嚟关闭。"
        cantonese["MESSAGE_TO_SELECT_VERSION"] =
            "唔该用“-s <版本名称>”拣一个可启动嘅版本或者用“install <版本名称>”安装一个新嘅版本并选择。"
        cantonese["MESSAGE_PRINT_COMMAND_EXCEEDS_LENGTH_LIMIT"] =
            "提示：启动命令过长，你可能唔可以直接喺cmd中运行或保存到bat文件之后执行，推荐你使用“version [<版本>] --export-script-ps=<脚本文件>”导出为 PowerShell 脚本文件后使用。"
        cantonese["MESSAGE_EXPORT_COMMAND_EXCEEDS_LENGTH_LIMIT"] =
            "无法导出脚本文件：由于bat脚本文件嘅长度限制，启动命令过长，无法导出为bat文件，只能使用“version [<版本>] --export-script-ps=<以.ps1为后缀的脚本文件>”导出为 PowerShell 脚本文件。"
        cantonese["MESSAGE_CONFIGURATIONS_TIP"] =
            "注：类型为布尔值嘅配置，佢嘅值可以输入表示“是”嘅“true”，亦可以系表示“否”嘅“false”。"
        cantonese["MESSAGE_CONFIGURATIONS_TABLE_CONTENT"] = """
        accounts|JSON数组|账号（非直接修改，请通过“account -h”获得相关使用教程以进行修改）
        downloadSource|整数|下载源，0系官方，1系BMCLAPI
        language|文本|语言，zh系简体中文，en系英文，cantonese系粤语（简体）
        selectedVersion|文本|已选择嘅版本，可以直接用“cmcl”进行启动
        maxMemory|整数|[游戏相关]最大内存（单位：MB）
        gameDir|文本|[游戏相关]自定义游戏目录路径（或设置版本隔离），默认为.minecraft
        assetsDir|文本|[游戏相关]自定义assets资源目录路径，如果系空嘅话就系游戏目录入边嘅assets目录
        resourcesDir|文本|[游戏相关]自定义资源包目录路径，如果系空嘅话就系游戏目录入边嘅resourcepacks目录
        javaPath|文本|[游戏相关]Java 路径（如果系空嘅话会自动获得）
        windowSizeWidth|整数|[游戏相关]游戏窗口嘅宽
        windowSizeHeight|整数|[游戏相关]游戏窗口嘅高
        isFullscreen|布尔值|[游戏相关]游戏窗口系咪全屏
        exitWithMinecraft|布尔值|[游戏相关]运行游戏嗰阵，需唔需要退出启动器时顺便退出游戏
        printStartupInfo|布尔值|[游戏相关]开始游戏嘅时候，使唔使输出启动信息（Java 路径、最大内存等）
        checkAccountBeforeStart|布尔值|[游戏相关]开始游戏之前，使唔使检查账号系咪可用嘅
        jvmArgs|JSON数组|[游戏相关]自定义JVM参数（通过“jvmArgs -h”获得相关使用教程以进行修改）
        gameArgs|JSON对象|[游戏相关]自定义游戏参数（通过“gameArgs -h”获得相关使用教程以进行修改）
        qpLogFile|文本|[游戏相关]快速游玩（Quick Play，Minecraft 1.20 嘅新功能，设置下边三项配置即可启动游戏后分别直接进入存档、服务器、领域，只能够设置一项）嘅日志文件路径（相对于游戏目录），可选
        qpSaveName|文本|[游戏相关]快速游玩直接进入嘅存档名称
        qpServerAddress|文本|[游戏相关]快速游玩直接进入嘅服务器地址（包括端口），呢个配置都适用于1.20之前嘅版本
        qpRealmsID|文本|[游戏相关]快速游玩直接进入嘅领域ID
        proxyEnabled|布尔值|开唔开启网络代理
        proxyHost|文本|代理主机地址
        proxyPort|整数|代理端口
        proxyUsername|文本|代理验证嘅账户（代理可选）
        proxyPassword|文本|代理验证嘅密码（代理可选）
        modDownloadSource|文本|模组下载源，curseforge或modrinth
        modpackDownloadSource|文本|整合包下载源，curseforge或modrinth
        simplifyCommands|JSON对象|简化命令（通过“simplify -h”获得相关使用教程以进行修改）
        """.trimIndent()
        cantonese["EXCEPTION_VERSION_JSON_NOT_FOUND"] =
            "目标启动版本嘅JSON文件或JAR文件唔喺度，用“-s <版本名称>”拣一个可启动嘅版本或者用“install <版本名称>”安装一个新嘅版本并选择。"
        cantonese["EXCEPTION_VERSION_NOT_FOUND"] = "%s：游戏版本唔存在"
        cantonese["EXCEPTION_NATIVE_LIBRARIES_NOT_FOUND"] =
            "搵唔到原生依赖库（natives）目录或者系空嘅，你要用“version <版本名称> --complete=natives”下载原生依赖库文件先至可以启动游戏。"
        cantonese["EXCEPTION_MAX_MEMORY_TOO_BIG"] = "最大内存大于物理内存嘅总大小"
        cantonese["EXCEPTION_JAVA_VERSION_TOO_LOW"] =
            "呢个 Minecraft 版本嘅 Java 版本最低要求系 %d，你拣嘅 Java 版本系 %d，请拣一个达到要求嘅 Java 之后重试。"
        cantonese["EXCEPTION_WINDOW_SIZE_MUST_BE_GREATER_THAN_ZERO"] = "游戏窗口嘅宽同高必须大于零"
        cantonese["EXCEPTION_JAVA_NOT_FOUND"] = "无法启动游戏，搵唔到 Java 文件。"
        cantonese["EXCEPTION_INCOMPLETE_VERSION"] =
            "呢个版本唔完整，请通过“version <版本名称> --complete”将呢个版本补充完整之后再启动。"
        cantonese["EXCEPTION_NOT_FOUND_DOWNLOAD_LINK"] = "搵唔到文件嘅下载地址"
        cantonese["EXCEPTION_NOT_FOUND_DOWNLOAD_LINK_WITH_FILENAME"] = "搵唔到文件“%s”嘅下载地址"
        cantonese["EXCEPTION_VERSION_JAR_NOT_FOUND"] = "目标版本嘅JAR文件唔存在，请重新安装呢个版本。"
        cantonese["EXCEPTION_NIDE8AUTH_JAVA_VERSION_TOO_LOW"] =
            "Java 版本细于 8u101 无法使用统一通行证，请换一个符合要求嘅 Java 后重试。"
        cantonese["EXCEPTION_READ_CONFIG_FILE"] =
            "读取配置文件失败，请确保配置文件 cmcl.json 系可读取嘅文件并且内容无误：%s"
        cantonese["EXCEPTION_NETWORK_WRONG_PLEASE_CHECK_PROXY"] =
            "网络错误：如果唔系目标网站嘅问题，噉就可能系你嘅代理出现咗问题，请检查您嘅网络代理是否可用！"
        cantonese["ON_AUTHENTICATED_PAGE_TEXT"] = "已完成微软账户授权，请关闭呢个页面跟住返到启动器完成登录。"
        cantonese["CONSOLE_UNSUPPORTED_VALUE"] = "唔支持嘅值：%s"
        cantonese["CONSOLE_LOGIN_MICROSOFT_WAIT_FOR_RESPONSE"] =
            "请喺浏览器嗰度登录你嘅微软账户，如果冇自动跳转，唔该手动复制呢个链接到浏览器打开：\n%s\n登录成功嘅话，就返嚟呢度，等待完成登录。\n登录要一定嘅时间，唔该畀啲耐心。"
        cantonese["CONSOLE_FILE_EXISTS"] = "文件“%s”已经喺度"
        cantonese["CONSOLE_INCORRECT_JAVA"] =
            "请通过 “config javaPath <Java路径>”或“version <版本> --config=javaPath <Java 路径>” 改一个啱嘅 Java 路径"
        cantonese["CONSOLE_NO_SELECTED_VERSION"] =
            "请使用“-s <版本名称>”拣一个版本嚟启动，或通过“cmcl <版本名称>”写埋版本名嚟启动。"
        cantonese["CONSOLE_LACK_LIBRARIES_WHETHER_DOWNLOAD"] = "你缺少咗以上启动游戏嘅必要依赖库。使唔使下载佢哋？"
        cantonese["CONSOLE_INSTALL_SHOW_INCORRECT_TIME"] = "%s：唔啱嘅时间格式或第一个时间大于第二个时间。"
        cantonese["CONSOLE_REPLACE_LOGGED_ACCOUNT"] = "你已经登录咗呢个账号（序号系 %d），要唔要覆盖原来嘅账号？"
        cantonese["CONSOLE_ACCOUNT_UN_OPERABLE_NEED_UUID_AND_URL_AND_TOKEN"] =
            "如果系外置账号，必须有UUID、accessToken同埋外置登录服务器嘅地址先至能够进行呢个操作，可尝试通过“account --refresh”刷新账号或重新登录。"
        cantonese["CONSOLE_ACCOUNT_UN_OPERABLE_MISSING_INFO"] =
            "必须系正版账号、外置账号或统一通行证登录并且有 UUID 先至能够进行呢个操作。如果系外置账号仲要目标服务器地址，如果系统一通行证仲要目标服务器ID，可尝试通过“account --refresh”刷新账号或重新登录。"
        cantonese["CONSOLE_INPUT_INT_WRONG"] = "请输入一个啱嘅、喺范围内嘅数字。"
        cantonese["CONSOLE_INPUT_STRING_NOT_FOUND"] = "搵唔到“%s”。"
        cantonese["CONSOLE_ONLY_HELP"] = "请使用选项 -h 或 --help 嚟获取帮助文档。"
        cantonese["CONSOLE_IMMERSIVE_WRONG"] =
            "唔正确嘅命令：%s。请输入 help 嚟获取帮助文档并且细心阅读同沉浸模式相关嘅文字。"
        cantonese["CONSOLE_IMMERSIVE_NOT_FOUND"] = "%s：搵唔到命令。请输入 help 嚟获取帮助文档。"
        cantonese["CONSOLE_UNKNOWN_COMMAND_OR_MEANING"] =
            "%s：未知命令或意义唔明确。请使用选项 -h 或 --help 嚟获取帮助文档。"
        cantonese["CONSOLE_IMMERSIVE_MISSING_PARAMETER"] = "缺少参数。请输入 help 嚟获取帮助文档。"
        cantonese["CONSOLE_NOT_FOUND_VERSION_OR_OPTION"] =
            "搵唔到名系“%s”嘅可启动版本或选项。你可以借助由输入选项 -h 或 --help 嚟获取嘅帮助文档检查输入嘅信息有冇错误。"
        cantonese["CONSOLE_HELP_WRONG_WRITE"] = "正确嘅写法系 -h 或 --help，唔携带参数值，而唔系“%s”。"
        cantonese["CONSOLE_UNKNOWN_USAGE"] = "未知用法：%s。请使用选项 -h 或 --help 嚟获取帮助文档。"
        cantonese["CONSOLE_ARG_CHECKING_ONE"] =
            "%s：选项用法错误或唔应该喺呢度出现。请使用选项 -h 或 --help 嚟获取帮助文档"
        cantonese["CONSOLE_ARG_CHECKING_PLURAL"] =
            "下边嘅选项用法错误或唔应该喺呢度出现。请使用选项 -h 或 --help 嚟获取帮助文档。\n%s"
        cantonese["CONSOLE_ASK_EXIT_WITH_MC"] =
            "请问需唔需要退出启动器嘅时候顺便退出游戏（可通过“config exitWithMinecraft true/false”开启或关闭）？"
        cantonese["CONSOLE_ASK_PRINT_STARTUP_INFO"] =
            "请问需唔需要启动游戏嗰阵打印启动信息（如Java 路径、最大内存、登录嘅账号之类嘅，可以通过“config printStartupInfo true/false”开启或关闭）？"
        cantonese["CONSOLE_ASK_CHECK_ACCOUNT"] =
            "请问需唔需要喺启动游戏之前检查账号是否可用（启动前会使啲时间，可通过“config checkAccountBeforeStart true/false”开启或关闭）？"
        cantonese["ACCOUNT_NOT_EXISTS"] = "账号唔存在：%d"
        cantonese["ACCOUNT_LOGIN_UNKNOWN_LOGIN_METHOD"] = "未知登录方式：%s。请使用选项 -h 或 --help 嚟获取帮助文档。"
        cantonese["ACCOUNT_MICROSOFT_REFRESH_NOT_SAME"] = "你喺网站登录嘅账号好似唔系你喺本地登录嘅账号㖞。"
        cantonese["NOT_SELECTED_AN_ACCOUNT"] =
            "未选择账号。请登录咗你嘅账号之后，用“account -l”列出账号，记住你要选择嘅账号嘅序号，跟住用“account -s<序号>”选择账号；或者喺登录账号命令后面加埋“-s”嘅选项。"
        cantonese["FAILED_TO_LOGIN_OAA_NO_SELECTED_CHARACTER"] = "登录失败，请拣一个可用嘅角色之后重试。"
        cantonese["WARNING_SHOWING_PASSWORD"] = "警告：喺非控制台执行呢个操作，你嘅密码将唔会被隐藏！"
        cantonese["WARNING_VCFG_JAVA_INCORRECT"] = "警告：独立版本配置嘅 Java 路径唔存在或无效，已使用全局配置值！"
        cantonese["WARNING_VCFG_MAX_MEMORY_INCORRECT"] = "警告：独立版本配置嘅最大内存小于或等于零，已使用全局配置值！"
        cantonese["WARNING_VCFG_WINDOW_SIZE_WIDTH_INCORRECT"] =
            "警告：独立版本配置嘅窗口宽小于或等于零，已使用全局配置值！"
        cantonese["WARNING_VCFG_WINDOW_SIZE_HEIGHT_INCORRECT"] =
            "警告：独立版本配置嘅窗口高小于或等于零，已使用全局配置值！"
        cantonese["FILE_NOT_FOUND_OR_IS_A_DIRECTORY"] = "搵唔到目标文件或目标文件系个目录"
        cantonese["UNAVAILABLE_AUTHLIB_ACCOUNT"] = "外置账号唔可用，游戏将使用离线账号。"
        cantonese["UNAVAILABLE_NIDE8AUTH_ACCOUNT"] = "统一通行证唔可用，游戏将使用离线账号。"
        cantonese["UNAVAILABLE_CUSTOM_SKIN"] = "自定义皮肤唔可用"
        cantonese["PRINT_COMMAND_NOT_SUPPORT_OFFLINE_CUSTOM_SKIN"] =
            "注意：如果你系离线账号，使用命令启动游戏，自定义皮肤将唔可用。"
        cantonese["ONLY_OFFLINE"] = "呢个功能仅支持离线账号"
        cantonese["UPLOAD_SKIN_ONLY_OAS_OR_OFFLINE"] = "呢个功能微软账户同统一通行证唔可用。"
        cantonese["SKIN_TYPE_DEFAULT_OR_SLIM"] = "是否将皮肤模型设为苗条（Alex）？"
        cantonese["SKIN_STEVE_NOT_FOUND"] = "设置失败，搵唔到 Steve 皮肤文件！"
        cantonese["SKIN_ALEX_NOT_FOUND"] = "设置失败，搵唔到 Alex 皮肤文件！"
        cantonese["CAPE_FILE_NOT_FOUND"] = "搵唔到披风文件“%s”"
        cantonese["UNABLE_TO_START_OFFLINE_SKIN_SERVER"] = "离线账号自定义皮肤功能唔可用"
        cantonese["UNABLE_OFFLINE_CUSTOM_SKIN_STEVE_NOT_FOUND"] = "无法使用自定义皮肤：搵唔到 Steve 皮肤文件！"
        cantonese["UNABLE_OFFLINE_CUSTOM_SKIN_ALEX_NOT_FOUND"] = "无法使用自定义皮肤：搵唔到 Alex 皮肤文件！"
        cantonese["UNABLE_OFFLINE_CUSTOM_SKIN_FILE_NOT_FOUND"] = "无法使用自定义皮肤：搵唔到皮肤文件“%s”"
        cantonese["INSTALL_MODLOADER_FAILED_TO_GET_INSTALLABLE_VERSION"] = "无法安装 %s：获得可安装嘅版本失败。"
        cantonese["INSTALL_MODLOADER_NO_INSTALLABLE_VERSION"] =
            "无法安装 %1\$s：冇可安装嘅 %1\$s 版本，可能系因为 %1\$s 唔支持呢个游戏版本。"
        cantonese["INSTALL_MODLOADER_SELECT"] = "请输入你要安装嘅 %1\$s 版本(默认选择%2\$s)："
        cantonese["INSTALL_MODLOADER_SELECT_NOT_FOUND"] =
            "版本“%1\$s”搵唔到，请输入你要安装嘅 %2\$s 版本(默认选择%3\$s)："
        cantonese["INSTALL_MODLOADER_UNABLE_DO_YOU_WANT_TO_CONTINUE"] = "使唔使继续安装原版（无 %s）？"
        cantonese["INSTALL_MODLOADER_EMPTY_MC_VERSION"] = "无法安装 %s：无法获得目标版本嘅游戏版本。"
        cantonese["INSTALL_MODLOADER_ALREADY_INSTALL_ANOTHER_ONE"] =
            "无法安装 %1\$s：目标版本已安装 %2\$s，%2\$s 和 %1\$s 唔可以共存。"
        cantonese["INSTALL_MODLOADER_NO_INSTALLABLE_VERSION_2"] = "无法安装 %1\$s：冇可安装嘅 %1\$s 版本。"
        cantonese["INSTALL_MODLOADER_FAILED_MC_VERSION_MISMATCH"] =
            "无法安装 %1\$s：目标 %1\$s 嘅游戏版本同目标游戏版本唔匹配。"
        cantonese["INSTALL_MODLOADER_FAILED_NOT_FOUND_TARGET_VERSION"] = "\${NAME} 版本“%s”搵唔到。"
        cantonese["INSTALL_MODLOADER_SELECT_NOT_FOUND_GAME_OR_TARGET_EXTRA"] = "搵唔到目标游戏版本或 \${NAME} 版本。"
        cantonese["CF_SUPPORTED_GAME_VERSION"] = "%s 支持嘅游戏版本："
        cantonese["CF_INPUT_GAME_VERSION"] = "请输入你要下载嘅版本："
        cantonese["CF_INPUT_VERSION"] = "请拣下你要下载嘅\${NAME}版本(%d-%d，-1系取消下载)："
        cantonese["CF_STORAGE_FILE_EXISTS"] = "请输入一个存储呢个\${NAME}文件嘅目录："
        cantonese["CF_NO_VERSION_FOR_GAME_VERSION"] = "冇适用于呢个游戏版本嘅%s版本。"
        cantonese["CF_INFORMATION_NOTHING"] = "冇任何关于呢个%s嘅可展示信息"
        cantonese["CF_INFORMATION_LATEST_GAME_VERSION"] = "   最新支持嘅游戏版本："
        cantonese["CF_GET_BY_ID_FAILED"] =
            "无法获得目标\${NAME}：%s\n出现呢个错误可能嘅原因：\n1.目标\${NAME}唔存在\n2.网络异常\n3.服务器出现问题"
        cantonese["CF_GET_BY_ID_NOT_OF_MC"] = "目标游戏组件唔系 Minecraft 嘅\${NAME}，呢个组件嘅游戏ID为%d。"
        cantonese["CF_DEPENDENCIES_TIP"] =
            "呢个\${NAME}需要下面嘅前置\${NAME}先至能够正常运行，将会先安装以下前置\${NAME}。"
        cantonese["CF_GET_BY_ID_INCORRECT_CATEGORY"] = "目标游戏组件唔系一个\${NAME}，呢个组件嘅类别ID系%d。"
        cantonese["CF_GET_BY_ID_INCORRECT_CATEGORY_DETAIL"] =
            "目标游戏组件唔系一个\${NAME}，呢个游戏组件系一个\${TARGET}。"
        cantonese["CF_STORAGE_FILE_EXISTS_SELECT_OPERATION"] = "文件“%s”已存在，请选择您嘅操作(0-2)："
        cantonese["INSTALL_MODPACK_FAILED_DOWNLOAD_MOD"] = "下载 projectId 系 %d 嘅模组失败：%s"
        cantonese["INSTALL_MODPACK_EACH_MOD_GET_URL"] = "遍历获得紧各个模组（文件）嘅下载链接，请耐心等待"
        cantonese["INSTALL_OPTIFINE_INCOMPATIBLE_WITH_FORGE_17"] =
            "无法安装 OptiFine：当前游戏版本嘅 Forge 同低于 H1 Pre2 版本嘅 OptiFine 唔兼容，请换一个版本更新嘅 OptiFine 之后重试。"
        cantonese["INSTALL_SHOW_UNKNOWN_TYPE"] = "%s：未知版本类型。请使用选项 -h 或 --help 嚟获取帮助文档。"
        cantonese["INSTALL_COEXIST"] = "%1\$s 同 %2\$s 唔可用共存。请使用选项 -h 或 --help 嚟获取帮助文档。"
        cantonese["INSTALL_FABRIC_API_WITHOUT_FABRIC"] = "唔装 Fabric，点装 Fabric API 呢？"
        cantonese["VERSION_COMPLETE_LIBRARIES_NO_NEED_TO"] = "冇缺少嘅依赖库需要补全。"
        cantonese["MOD_UNKNOWN_SOURCE"] = "%s：未知下载源。用选项 -h 或 --help 嚟获取更多信息。"
        cantonese["MOD_CONTAINS_BOTH_NAME_AND_ID"] = "-n 或 --name 同 --id 唔可以同时存在。"
        cantonese["MOD_CONTAINS_BOTH_NOT_NAME_AND_ID"] =
            "必须指定 -n 或 --name 或 --id。用选项 -h 或 --help 嚟获取更多信息。"
        cantonese["MOD_SEARCH_LIMIT_GREATER_THAN_FIFTY"] = "如果下载源系 CurseForge，限制数量最大系50。"
        cantonese["MOD_ID_LIMIT_COEXIST"] = "你都唔用-n或者--name嘅搜索功能咯，仲点用--limit限制搜索结果呢？"
        cantonese["MODPACK_CONTAINS_TWO_OR_MORE"] = "--install、--info 或 --file 只能够存在一个。"
        cantonese["YES_SHORT"] = "系"
        cantonese["TABLE_ACCOUNTS_LIST_HEADER_SELECTED"] = "选择咗"
        cantonese
    }

    override val helpMap: Map<String, String> by lazy {
        SimplifiedChinese.helpMap
    }
}