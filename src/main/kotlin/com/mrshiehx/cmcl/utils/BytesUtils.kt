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
package com.mrshiehx.cmcl.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

object BytesUtils {
    fun bytesToString(bytes: ByteArray): String {
        val builder = StringBuilder()
        for (aByte in bytes) {
            builder.append(String.format("%02X", aByte))
        }

        return builder.toString().uppercase(Locale.getDefault())
    }

    @Throws(NoSuchAlgorithmException::class)
    fun getBytesHashSHA256String(bytes: ByteArray): String {
        return bytesToString(getBytesHashSHA256(bytes)).lowercase(Locale.getDefault())
    }

    @Throws(NoSuchAlgorithmException::class)
    fun getBytesHashSHA256(bytes: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(bytes, 0, bytes.size)
        return digest.digest()
    }
}
