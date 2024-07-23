package com.mrshiehx.cmcl.utils.json

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.io.Writer

class XJSONTokener(s: String) : JSONTokener(s) {
    @Throws(JSONException::class)
    override fun nextValue(): Any {
        var c = nextClean()
        return when (c) {
            '"', '\'' -> nextString(c)
            '[' -> {
                back()
                return try {
                    object : JSONArray(this) {
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
                                        XJSONObject.writeValue(
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
                                            XJSONObject.writeValue(
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
                } catch (var5: StackOverflowError) {
                    throw JSONException("JSON Array or Object depth too large to process.", var5)
                }
            }

            '{' -> {
                back()
                return try {
                    XJSONObject(this)
                } catch (var4: StackOverflowError) {
                    throw JSONException("JSON Array or Object depth too large to process.", var4)
                }
            }

            else -> {
                val sb: StringBuilder = StringBuilder()
                while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
                    sb.append(c)
                    c = this.next()
                }
                try {
                    val f = JSONTokener::class.java.getDeclaredField("eof")
                    f.setAccessible(true)
                    if (!f.getBoolean(this)) {
                        back()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val string = sb.toString().trim { it <= ' ' }
                if ("" == string) {
                    throw this.syntaxError("Missing value")
                } else {
                    JSONObject.stringToValue(string)
                }
            }
        }
    }
}
