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

import com.mrshiehx.cmcl.utils.Utils.isEmpty
import com.mrshiehx.cmcl.utils.Utils.removeDuplicate
import com.mrshiehx.cmcl.utils.console.CommandUtils
import java.util.*
import java.util.stream.Collectors
import kotlin.reflect.KClass

class Arguments(args: List<String>, removeDuplicate: Boolean) {
    private val arguments: MutableList<Argument>
    val size: Int
        get() = this.arguments.size

    constructor(args: String, removeDuplicate: Boolean) :
            this(CommandUtils.splitCommand(CommandUtils.clearRedundantSpaces(args)), removeDuplicate)

    companion object {
        fun valueOf(strings: List<String>, isForCMCL: Boolean): Arguments =
            Arguments(strings, removeDuplicate = isForCMCL)
    }

    init {
        arguments = LinkedList()
        val length = args.size
        var i = 0
        while (i < length) {
            val item = args[i]
            if (item.startsWith("--") && item.length > 2) {
                val rear = item.substring(2)
                val index = rear.indexOf('=')
                if (index == -1) {
                    var value: String? = null
                    if (i + 1 != length) {
                        val next = args[i + 1]
                        if (!next.startsWith("-")) {
                            value = next
                            i++
                        }
                    }
                    arguments.add(
                        if (value != null) ValueArgument(
                            "$item $value",
                            arrayOf(item, value),
                            rear,
                            value
                        ) else SingleArgument(item, arrayOf(item), rear)
                    )
                } else {
                    //如果index为0或大于0的处理方法相同
                    val key = rear.substring(0, index)
                    val value = rear.substring(index + 1)
                    arguments.add(ValueArgument(item, arrayOf(item), key, value))
                }
            } else if (item.startsWith("-") && item.length > 1) {
                if (item.length == 2) {
                    val key = item.substring(1)
                    var value: String? = null
                    if (i + 1 != length) {
                        val next = args[i + 1]
                        if (!next.startsWith("-")) {
                            value = next
                            i++
                        }
                    }
                    arguments.add(
                        if (value != null) ValueArgument(
                            "$item $value",
                            arrayOf(item, value),
                            key,
                            value
                        ) else SingleArgument(item, arrayOf(item), key)
                    )
                } else {
                    arguments.add(ValueArgument(item, arrayOf(item), item.substring(1, 2), item.substring(2)))
                }
            } else {
                arguments.add(TextArgument(item))
            }
            i++

        }
        if (removeDuplicate)
            removeDuplicate(arguments)

    }

    fun removeDuplicate(): Arguments {
        removeDuplicate(arguments)
        return this
    }

    fun equals(index: Int, target: String): Boolean {
        if (index in 0..<size) {
            return arguments[index].equals(target)
        }
        return false
    }

    fun optArgument(name: String): Argument? {
        return arguments.find { it.equals(name) }
    }

    fun optArguments(name: String): List<Argument> {
        return arguments.filter { it.equals(name) }
    }

    fun opt(name: String): String? {
        return opt(name, null)
    }

    fun opt(name: String, def: String?): String? {
        val argument = optArgument(name)
        return if (argument is ValueArgument) argument.value else def
    }

    fun optInt(name: String): Int? {
        return optInt(name, null)
    }

    fun optInt(name: String, defaultValue: Int?): Int? {
        val s = opt(name, null)
        if (s != null) {
            try {
                return s.toInt()
            } catch (ignore: Throwable) {
            }
        }
        return defaultValue
    }

    fun optBoolean(name: String): Boolean? {
        return optBoolean(name, null)
    }

    fun optBoolean(name: String, defaultValue: Boolean?): Boolean? {
        val s = opt(name, null)
        if (s != null) {
            try {
                return s.toBoolean()
            } catch (ignore: Throwable) {
            }
        }
        return defaultValue
    }

    fun optDouble(name: String): Double? {
        return optDouble(name, null)
    }

    fun optDouble(name: String, defaultValue: Double?): Double? {
        val s = opt(name, null)
        if (s != null) {
            try {
                return s.toDouble()
            } catch (ignore: Throwable) {
            }
        }
        return defaultValue
    }

    operator fun contains(target: String): Boolean {
        return arguments.any { it.equals(target) }
    }

    operator fun contains(target: Argument): Boolean {
        return arguments.any { it == target }
    }

    fun optArgument(i: Int): Argument? {
        return if (i in 0..<size) {
            arguments[i]
        } else null
    }

    fun getArguments(): List<Argument> {
        return arguments.toList()
    }

    override fun toString(): String {
        return toString("--")
    }

    fun toString(originalArgKeyStart: String): String {
        var argKeyStart = originalArgKeyStart
        if (isEmpty(argKeyStart)) argKeyStart = "-"
        val sb = StringBuilder()
        val size = arguments.size
        for (i in 0 until size) {
            when (val argument = arguments[i]) {
                is SingleArgument -> sb.append(argument.key)
                is TextArgument -> sb.append(argKeyStart).append(argument.key)
                is ValueArgument -> {
                    var value = argument.value
                    if (value.contains(" ")) {
                        value = "\"" + value + "\""
                    }
                    sb.append(argKeyStart)
                        .append(argument.key)
                        .append(' ')
                        .append(value)
                }
            }
            if (i + 1 < size) {
                sb.append(' ')
            }
        }
        return sb.toString()
    }

    fun merge(arguments: Arguments): Arguments {
        if (arguments.arguments.size == 0) return this
        for (argument in arguments.arguments) {
            if (!this.arguments.contains(argument)) {
                this.arguments.add(argument)
            }
        }
        return this
    }

    /**
     * In order to prevent the user from inputting wrong options,
     * or entering parameters that should not exist,
     * the parameters that do not exist or have a wrong type in `arguments` will be filtered out,
     * and the original `Arguments` object is not changed.
     * Will compare the type and the key of the arguments.
     *
     * @return the filtered results, will not be empty
     */
    fun exclude(argumentRequirements: List<ArgumentRequirement>, offsetForOrigin: Int): List<Argument> {
        if (argumentRequirements.isEmpty()) return this.arguments.toList()
        if (size == 0) return emptyList()
        val map: MutableMap<String, MutableList<KClass<out Argument>>> = HashMap()
        for (argReq in argumentRequirements) {
            val temp = map[argReq.key]
            if (temp != null) {
                temp.add(argReq.clazz)
            } else {
                val list = LinkedList<KClass<out Argument>>()
                list.add(argReq.clazz)
                map[argReq.key] = list
            }
        }
        return this.arguments.subList(offsetForOrigin, this.arguments.size).stream()
            .filter { argument: Argument ->
                argument !is TextArgument && (map[argument.key] == null || !map[argument.key]!!.contains(argument::class))
            }
            .collect(Collectors.toList())
    }

}