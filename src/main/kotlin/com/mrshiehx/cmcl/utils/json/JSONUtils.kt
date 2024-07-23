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
package com.mrshiehx.cmcl.utils.json

import org.json.JSONArray
import org.json.JSONObject

object JSONUtils {
    /**
     * 解析 JSONObject
     *
     * @return 如果jsonObjectString是一个JSONObject，就返回被解析的JSONObject，否则返回null
     */
    fun parseJSONObject(jsonObjectString: String?): JSONObject? {
        if (jsonObjectString.isNullOrBlank()) return null
        try {
            return JSONObject(jsonObjectString)
        } catch (ignored: Throwable) {
        }
        return null
    }

    /**
     * 解析 JSONArray
     *
     * @return 如果jsonObjectString是一个JSONObject，就返回被解析的JSONObject，否则返回null
     */
    fun parseJSONArray(jsonArrayString: String?): JSONArray? {
        if (jsonArrayString.isNullOrBlank()) return null
        try {
            return JSONArray(jsonArrayString)
        } catch (ignored: Throwable) {
        }
        return null
    }

    /**
     * 对返回值操作将不会影响到原 JSONArray
     */
    @JvmOverloads
    fun jsonArrayToJSONObjectList(jsonArray: JSONArray?, filter: ((JSONObject) -> Boolean)? = null): List<JSONObject> {
        var jsonObjects = (jsonArray ?: JSONArray()).filterIsInstance<JSONObject>()
        if (filter != null) {
            jsonObjects = jsonObjects.filter(filter)
        }
        return jsonObjects
    }
}
