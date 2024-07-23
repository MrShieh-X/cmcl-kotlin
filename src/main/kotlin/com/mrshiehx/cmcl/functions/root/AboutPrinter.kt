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
package com.mrshiehx.cmcl.functions.root

import com.mrshiehx.cmcl.constants.Constants
import com.mrshiehx.cmcl.utils.Utils.getString

object AboutPrinter {
    fun execute() {
        println()
        println(
            """                         _____   __  __    _____   _
                        / ____| |  \/  |  / ____| | |
                       | |      | \  / | | |      | |
                       | |      | |\/| | | |      | |
                       | |____  | |  | | | |____  | |____
                        \_____| |_|  |_|  \_____| |______|
                                    
"""
        )
        println("=======================================================================================")
        println(
            """ - ${
                String.format(
                    getString("MESSAGE_ABOUT_DESCRIPTION_1"),
                    Constants.CMCL_VERSION_NAME
                )
            }: ${getString("MESSAGE_ABOUT_DESCRIPTION_2")}
 - ${Constants.COPYRIGHT}
 - ${getString("MESSAGE_ABOUT_DESCRIPTION_4")}${Constants.SOURCE_CODE}
=======================================================================================
 - ${getString("MESSAGE_ABOUT_DESCRIPTION_MAIN_DEVELOPERS")}
 --- MrShiehX
 ----- Github: https://github.com/MrShieh-X
 ----- Bilibili: https://space.bilibili.com/323674091
 --- Graetpro-X
 ----- Github: https://github.com/Graetpro
 ----- Bilibili: https://space.bilibili.com/122352984
=======================================================================================
 - ${getString("MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS")}
 --- yushijinhun...............: ${getString("MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_AUTHLIB_INJECTOR")}
 --- bangbang93................: ${getString("MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_BMCLAPI")}
 --- ${getString("MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_MCBBS_NAME")}: ${getString("MESSAGE_ABOUT_DESCRIPTION_SPECIAL_THANKS_MCBBS")}
=======================================================================================
 - ${getString("MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_TITLE")}
 --- ${getString("MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_CONTENT_1")}
 --- ${getString("MESSAGE_ABOUT_DESCRIPTION_DISCLAIMER_CONTENT_2")}
=======================================================================================
 - ${getString("MESSAGE_ABOUT_DESCRIPTION_6")}
 --- json
 ----- Copyright (c) 2002 JSON.org
 ----- Licensed under the JSON License.
 --- nanohttpd
 ----- Copyright (C) 2012 - 2016 nanohttpd
 ----- Licensed under the BSD-3-Clause License.
 --- jansi
 ----- Copyright (C) 2009-2021 the original author(s).
 ----- Licensed under the Apache-2.0 License.
 --- Constant Pool Scanner
 ----- Copyright 1997-2010 Oracle and/or its affiliates.
 ----- Licensed under the GPL 2 or the CDDL.
 --- JLine
 ----- Copyright (C) 2022 the original author(s).
 ----- Distributed under the BSD-3-Clause License.
 --- Java Native Access (JNA)
 ----- Copyright (c) 2007-2019 Timothy Wall,
       Wayne Meissner and Matthias Blasing
 ----- Licensed under the LGPL, version 2.1 or later, or (from
       version 4.0 onward) the Apache License, version 2.0.
======================================================================================="""
        )
    }
}
