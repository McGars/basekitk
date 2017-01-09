package com.mcgars.basekitk.tools.objBuilder

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONObject

/**
 * Created by gars on 09.01.2017.
 */

inline fun JSONObject.str(name: String): String? {
    return optString(name)?.run {
        return if (this != "null") this else null
    }
}

inline fun JsonObject.str(name: String): String? {
    return getElement(this, name)?.asString
}

inline fun JsonObject.char(name: String): Char? {
    return getElement(this, name)?.asCharacter
}

inline fun JsonObject.bool(name: String): Boolean {
    return getElement(this, name)?.asBoolean ?: false
}

inline fun JsonObject.byte(name: String): Byte? {
    return getElement(this, name)?.asByte
}

inline fun JsonObject.short(name: String): Short? {
    return getElement(this, name)?.asShort
}

inline fun JsonObject.int(name: String): Int {
    return getElement(this, name)?.asInt ?: 0
}

inline fun JsonObject.long(name: String): Long {
    return getElement(this, name)?.asLong ?: 0
}

inline fun JsonObject.float(name: String): Float {
    return getElement(this, name)?.asFloat ?: 0f
}

inline fun JsonObject.double(name: String): Double {
    return getElement(this, name)?.asDouble ?: 0.0
}

inline fun getElement(data: JsonObject?, name: String): JsonElement? {
    return data?.run { if (has(name)) get(name) else null }
}