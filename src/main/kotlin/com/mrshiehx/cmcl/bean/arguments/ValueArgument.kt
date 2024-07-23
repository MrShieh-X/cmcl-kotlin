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
package com.mrshiehx.cmcl.bean.arguments

import java.util.*

class ValueArgument(originString: String, originArray: Array<String>, key: String, val value: String) :
    Argument(originString, originArray, key) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (!super.equals(other)) return false
        val that = other as ValueArgument
        return value == that.value
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), value)
    }

    fun equals(key: String, value: String?): Boolean {
        return key.equals(this.key, ignoreCase = true) && this.value == value
    }

    override fun toString(): String {
        return "ValueArgument: $key, value: $value"
    }
}