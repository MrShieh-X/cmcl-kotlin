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
package com.mrshiehx.cmcl.constants.languages

import com.mrshiehx.cmcl.constants.languages.chinese.Cantonese
import com.mrshiehx.cmcl.constants.languages.chinese.SimplifiedChinese
import java.util.*

enum class LanguageEnum(val codes: Set<String>, val language: Language, val locale: Locale) : Language by language {
    SIMPLIFIED_CHINESE(setOf("zh"), SimplifiedChinese, Locale.SIMPLIFIED_CHINESE),
    ENGLISH(setOf("en"), English, Locale.ENGLISH),
    CANTONESE(setOf("cantonese"), Cantonese, Locale.CHINA);

    /*var textMap: Map<String, String>? = null
        get() {
            if (field == null) {
                field = language.getTextMap()
            }
            return field
        }
        private set
    var helpMap: Map<String, String>? = null
        get() {
            if (field == null) {
                field = language.getHelpMap()
            }
            return field
        }
        private set*/

    companion object {
        fun overriddenValueOf(code: String): LanguageEnum {
            for (languageEnum in entries) {
                if (languageEnum.codes.contains(code)) return languageEnum
            }
            return ENGLISH
        }
    }
}
