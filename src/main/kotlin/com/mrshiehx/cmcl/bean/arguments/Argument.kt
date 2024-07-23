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

import java.util.*

sealed class Argument(
    val originString: String,
    val originArray: Array<String>, //按照引号外空格划分，所以每一项内都允许有空格；不可为空，至少有一项
    val key: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val argument = other as Argument
        return key == argument.key
    }

    override fun hashCode(): Int {
        return Objects.hash(key)
    }

    fun equals(key: String): Boolean {
        return key == this.key
    }
}
