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

package com.mrshiehx.cmcl.enums

import com.mrshiehx.cmcl.CMCL.getString

enum class GameCrashError(val cause: String) {
    URLClassLoader(getString("MESSAGE_GAME_CRASH_CAUSE_URLCLASSLOADER")),
    LWJGLFailedLoad(getString("MESSAGE_GAME_CRASH_CAUSE_LWJGL_FAILED_LOAD")),
    MemoryTooSmall(getString("MESSAGE_GAME_CRASH_CAUSE_MEMORY_TOO_SMALL")),
    JvmUnrecognizedOption(getString("MESSAGE_GAME_CRASH_CAUSE_JVM_UNRECOGNIZED_OPTION"));
}