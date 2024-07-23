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
package com.mrshiehx.cmcl.utils.console

/**
 * 控制台的普通进度（当前/总共）
 *
 * @author MrShiehX
 */
class TextProgress : PercentageTextProgress() {
    override var value: Int = 0
        set(value) {
            if (value == field) return
            val before = field
            field = value
            if (printed) {
                for (j in 0 until "$before/$maximum".length + 2) {
                    print("\b")
                }
            }
            print("($value/$maximum)")
            if (value == maximum) {
                println()
                done = true
            }
            printed = true
        }

}
