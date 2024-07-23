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
package com.mrshiehx.cmcl.bean.arguments

import kotlin.reflect.KClass

class ArgumentRequirement private constructor(val clazz: KClass<out Argument>, val key: String) {
    companion object {
        fun ofSingle(key: String): ArgumentRequirement {
            return ArgumentRequirement(SingleArgument::class, key)
        }

        fun ofValue(key: String): ArgumentRequirement {
            return ArgumentRequirement(ValueArgument::class, key)
        }
    }
}
