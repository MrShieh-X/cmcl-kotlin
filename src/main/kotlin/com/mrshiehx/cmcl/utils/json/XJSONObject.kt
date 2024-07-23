package com.mrshiehx.cmcl.utils.json

import org.json.*
import java.io.IOException
import java.io.Writer
import java.lang.reflect.Method
import java.util.regex.Pattern

/**
 * 按顺序的 JSONObject 工具，转字符串格式化时如果只有单个的项也会缩进（原版不会）
 */
class XJSONObject : JSONObject {
    constructor() {
        try {
            val f = JSONObject::class.java.getDeclaredField("map")
            f.setAccessible(true)
            f[this] = LinkedHashMap<Any, Any>()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
    }

    constructor(source: String) : this(XJSONTokener(source))
    constructor(m: Map<*, *>?) {
        try {
            val f = JSONObject::class.java.getDeclaredField("map")
            f.setAccessible(true)
            if (m == null) {
                f[this] = LinkedHashMap<Any, Any>()
            } else {
                f[this] = LinkedHashMap<Any, Any>(m.size)
                for (entry in m.entries) {
                    if (entry.key == null) {
                        throw NullPointerException("Null key.")
                    }
                    val value = entry.value
                    if (value != null) {
                        (f[this] as LinkedHashMap<String?, Any?>)[(entry as Map.Entry<*, *>).key.toString()] =
                            wrap(value)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    constructor(x: XJSONTokener) : this() {
        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'")
        } else {
            while (true) {
                var prev: Char
                var method: Method
                try {
                    method = JSONTokener::class.java.getDeclaredMethod("getPrevious")
                    method.setAccessible(true)
                    prev = method.invoke(x) as Char
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
                var c = x.nextClean()
                when (c) {
                    '\u0000' -> throw x.syntaxError("A JSONObject text must end with '}'")
                    '[', '{' -> {
                        if (prev == '{') {
                            throw x.syntaxError("A JSON Object can not directly nest another JSON Object or JSON Array.")
                        }
                        //default
                        x.back()
                        val key: String? = x.nextValue().toString()
                        c = x.nextClean()
                        if (c != ':') {
                            throw x.syntaxError("Expected a ':' after a key")
                        }
                        if (key != null) {
                            if (opt(key) != null) {
                                throw x.syntaxError("Duplicate key \"$key\"")
                            }
                            val value: Any? = x.nextValue()
                            if (value != null) {
                                this.put(key, value)
                            }
                        }
                        when (x.nextClean()) {
                            ',', ';' -> {
                                if (x.nextClean() == '}') {
                                    return
                                }
                                x.back()
                                continue
                            }

                            '}' -> return
                            else -> throw x.syntaxError("Expected a ',' or '}'")
                        }
                    }

                    '}' -> return
                    else -> {
                        x.back()
                        val key: String? = x.nextValue().toString()
                        c = x.nextClean()
                        if (c != ':') {
                            throw x.syntaxError("Expected a ':' after a key")
                        }
                        if (key != null) {
                            if (opt(key) != null) {
                                throw x.syntaxError("Duplicate key \"$key\"")
                            }
                            val value: Any? = x.nextValue()
                            if (value != null) {
                                this.put(key, value)
                            }
                        }
                        when (x.nextClean()) {
                            ',', ';' -> {
                                if (x.nextClean() == '}') {
                                    return
                                }
                                x.back()
                                continue
                            }

                            '}' -> return
                            else -> throw x.syntaxError("Expected a ',' or '}'")
                        }
                    }
                }
            }
        }
    }

    @Throws(JSONException::class)
    override fun write(writer: Writer, indentFactor: Int, indent: Int): Writer {
        return try {
            var needsComma = false
            val length = length()
            writer.write('{'.code)
            val newIndent = indent + indentFactor
            if (length == 1) {
                val (key, value) = entrySet().iterator().next()
                if (indentFactor > 0) {
                    writer.write('\n'.code)
                }

                //indent(writer, newIndent);
                for (i in 0 until newIndent) {
                    writer.write(' '.code)
                }
                writer.write(quote(key))
                writer.write(':'.code)
                if (indentFactor > 0) {
                    writer.write(' '.code)
                }
                try {
                    writeValue(writer, value, indentFactor, newIndent)
                } catch (var12: Exception) {
                    throw JSONException("Unable to write JSONObject value for key: $key", var12)
                }
                if (indentFactor > 0) {
                    writer.write('\n'.code)
                }

                //indent(writer, indent);
                for (i in 0 until indent) {
                    writer.write(' '.code)
                }
            } else if (length != 0) {
                val var15: Iterator<Map.Entry<String, Any>> = entrySet().iterator()
                while (var15.hasNext()) {
                    val (key, value) = var15.next()
                    if (needsComma) {
                        writer.write(','.code)
                    }
                    if (indentFactor > 0) {
                        writer.write('\n'.code)
                    }

                    //indent(writer, newIndent);
                    for (i in 0 until newIndent) {
                        writer.write(' '.code)
                    }
                    writer.write(quote(key))
                    writer.write(':'.code)
                    if (indentFactor > 0) {
                        writer.write(' '.code)
                    }
                    try {
                        writeValue(writer, value, indentFactor, newIndent)
                    } catch (var11: Exception) {
                        throw JSONException("Unable to write JSONObject value for key: $key", var11)
                    }
                    needsComma = true
                }
                if (indentFactor > 0) {
                    writer.write('\n'.code)
                }

                //indent(writer, indent);
                for (i in 0 until indent) {
                    writer.write(' '.code)
                }
            }
            writer.write('}'.code)
            writer
        } catch (var13: IOException) {
            throw JSONException(var13)
        }
    }

    companion object {
        val NUMBER_PATTERN: Pattern = Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?")

        @Throws(JSONException::class, IOException::class)
        fun writeValue(writer: Writer, value: Any?, indentFactor: Int, indent: Int): Writer {
            if (value !== null && value != null && !value.equals(null)) {
                val numberAsString: String?
                if (value is JSONString) {
                    numberAsString = try {
                        value.toJSONString()
                    } catch (var6: Exception) {
                        throw JSONException(var6)
                    }
                    writer.write(numberAsString ?: quote(value.toString()))
                } else if (value is Number) {
                    numberAsString = numberToString(value)
                    if (NUMBER_PATTERN.matcher(numberAsString!!).matches()) {
                        writer.write(numberAsString)
                    } else {
                        quote(numberAsString, writer)
                    }
                } else if (value is Boolean) {
                    writer.write(value.toString())
                } else if (value is Enum<*>) {
                    writer.write(quote(value.name))
                } else if (value is XJSONObject) {
                    value.write(writer, indentFactor, indent)
                } else if (value is JSONObject) {
                    XJSONObject(value.toMap()).write(writer, indentFactor, indent)
                } else if (value is JSONArray) {
                    val a: JSONArray = object : JSONArray(value as JSONArray?) {
                        @Throws(JSONException::class)
                        override fun write(writer: Writer, indentFactor: Int, indent: Int): Writer? {
                            return try {
                                var needsComma = false
                                val length = length()
                                writer.write('['.code)
                                val myArrayList: ArrayList<Any> = try {
                                    val f = JSONArray::class.java.getDeclaredField("myArrayList")
                                    f.setAccessible(true)
                                    f[this] as ArrayList<Any>
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    return null
                                }
                                if (length == 1) {
                                    val newIndent = indent + indentFactor
                                    if (indentFactor > 0) {
                                        writer.write('\n'.code)
                                    }
                                    //JSONObject.indent(writer, newIndent);
                                    run {
                                        var ix = 0
                                        while (ix < newIndent) {
                                            writer.write(' '.code)
                                            ix += 1
                                        }
                                    }
                                    try {
                                        writeValue(
                                            writer, myArrayList[0],
                                            indentFactor, newIndent
                                        )
                                    } catch (e: Exception) {
                                        throw JSONException("Unable to write JSONArray value at index: 0", e)
                                    }
                                    if (indentFactor > 0) {
                                        writer.write('\n'.code)
                                    }
                                    //JSONObject.indent(writer, indent);
                                    var ix = 0
                                    while (ix < indent) {
                                        writer.write(' '.code)
                                        ix += 1
                                    }
                                } else if (length != 0) {
                                    val newIndent = indent + indentFactor
                                    var i = 0
                                    while (i < length) {
                                        if (needsComma) {
                                            writer.write(','.code)
                                        }
                                        if (indentFactor > 0) {
                                            writer.write('\n'.code)
                                        }
                                        //JSONObject.indent(writer, newIndent);
                                        var ix = 0
                                        while (ix < newIndent) {
                                            writer.write(' '.code)
                                            ix += 1
                                        }
                                        try {
                                            writeValue(
                                                writer, myArrayList[i],
                                                indentFactor, newIndent
                                            )
                                        } catch (e: Exception) {
                                            throw JSONException("Unable to write JSONArray value at index: $i", e)
                                        }
                                        needsComma = true
                                        i += 1
                                    }
                                    if (indentFactor > 0) {
                                        writer.write('\n'.code)
                                    }
                                    //JSONObject.indent(writer, indent);
                                    var ix = 0
                                    while (ix < indent) {
                                        writer.write(' '.code)
                                        ix += 1
                                    }
                                }
                                writer.write(']'.code)
                                writer
                            } catch (e: IOException) {
                                throw JSONException(e)
                            }
                        }
                    }
                    a.write(writer, indentFactor, indent)
                } else if (value is Map<*, *>) {
                    XJSONObject(value).write(writer, indentFactor, indent)
                } else if (value is Collection<*>) {
                    JSONArray(value).write(writer, indentFactor, indent)
                } else if (value.javaClass.isArray) {
                    JSONArray(value).write(writer, indentFactor, indent)
                } else {
                    quote(value.toString(), writer)
                }
            } else {
                writer.write("null")
            }
            return writer
        }
    }
}
