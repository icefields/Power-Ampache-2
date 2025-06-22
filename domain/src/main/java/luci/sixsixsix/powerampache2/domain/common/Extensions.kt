/**
 * Copyright (C) 2025  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.domain.common

import android.content.Context
import android.util.Patterns
import luci.sixsixsix.powerampache2.domain.common.Constants.ERROR_FLOAT
import luci.sixsixsix.powerampache2.domain.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.domain.common.Constants.ERROR_STRING
import luci.sixsixsix.powerampache2.domain.common.Constants.MAX_QUEUE_SIZE
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Song
import java.lang.ref.WeakReference
import java.security.MessageDigest
import java.text.Normalizer

typealias WeakContext = WeakReference<Context>

fun String.md5(): String {
    return hashString(this, "MD5")
}

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

fun String.isIpAddress(): Boolean =
    //InetAddresses.isNumericAddress(this) // requires min Q
    Patterns.IP_ADDRESS.matcher(this).matches()
//("(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])").toRegex().matches(this)

fun String.normalizeForSearch(): String = if (this.isNotBlank())
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "") // strip accents
        .replace("[\\p{Punct}]".toRegex(), " ") // replace punctuation like ()[]{}!?. with space
        .replace("\\s+".toRegex(), " ") // collapse multiple spaces
        .trim()
    else ""

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}

fun processFlag(flag: Any?): Int = flag?.let {
    if (it is Boolean) {
        if (it == true) 1 else 0
    } else if (it is Int) it
    else 0
} ?: 0

/**
 * different versions of ampache seem to return different types that are making gson crash
 */
fun processNumberToInt(num: Any?): Int = num?.let {
    try {
        when(it) {
            is Double -> it.toInt()
            is Float -> it.toInt()
            is Int -> it.toInt()
            is String -> it.toInt()
            else -> ERROR_INT
        }
    } catch (e: Exception) { ERROR_INT }
} ?: ERROR_INT

/**
 * different versions of ampache seem to return different types that are making gson crash
 */
fun processNumberToFloat(num: Any?): Float = num?.let {
    try {
        when(it) {
            is Double -> it.toFloat()
            is Float -> it.toFloat()
            is Int -> it.toFloat()
            is String -> it.toFloat()
            else -> ERROR_FLOAT
        }
    } catch (e: Exception) {
        ERROR_FLOAT
    }
} ?: ERROR_FLOAT

/**
 * if hasArt flag is present (not null), check if there's any art with it.
 */
fun processArtUrl(hasArt: Any?, artUrl: String?) = hasArt?.let { hasArtAny ->
    if (artUrl != null && processFlag(hasArtAny) == 1) { artUrl } else { "" }
} ?: run {
    artUrl ?: ""
}


/**
 * TODO DEBUG
 * this function uses reflection to quickly turn any of the music object into a String to quickly
 * print and visualize data
 */
fun Any.toDebugString(
    excludeErrorValues: Boolean = false,
    excludeLyrics: Boolean = false,
    excludeLists: Boolean = false,
    separator: String = "\n"
): String {
    val obj = this
    val sb = StringBuilder()
    for (field in obj.javaClass.declaredFields) {
        field.isAccessible = true

        field.get(obj)?.let {
            if(
                !field.name.lowercase().contains("url") &&
                !field.name.lowercase().contains("token") &&
                !field.name.lowercase().contains("artist") &&
                !field.name.lowercase().contains("CREATOR") &&
                !field.name.lowercase().contains("\$stable") &&
                "$it".isNotBlank() &&
                "$it" != "0" &&
                !"$it".contains("CREATOR") &&
                !"$it".contains("\$stable") &&
                "$it" != "[]" &&
                (!excludeLyrics || !field.name.lowercase().contains("lyrics")) &&
                (!excludeLists || it !is List<*>)
            ) {
                if (it is List<*>) {
                    if (field.name != "genre") {
                        sb.append(field.name)
                            .append(": ")
                    } else {
                        sb.append(" | ")
                    }

                    it.forEach { listElem ->
                        listElem?.let {
                            if (listElem is MusicAttribute) {
                                sb.append(listElem.name)
                                sb.append(" | ")
                            }
                        }
                    }
                    sb.append(separator)
                } else if (it is MusicAttribute) {
                    sb.append(field.name)
                        .append(": ")
                        .append("${it.name}")
                        .append(separator)
                } else {
                    val valueStr = "${field.get(obj)}"
                    if (!excludeErrorValues ||
                        (!valueStr.startsWith(ERROR_STRING) &&
                                !valueStr.startsWith(ERROR_STRING) &&
                                !valueStr.startsWith(ERROR_INT.toString()) &&
                                !valueStr.startsWith(ERROR_FLOAT.toString())
                                )
                    ) {
                        sb.append(field.name)
                            .append(": ")
                            .append(valueStr)
                            .append(separator)
                    }
                }
            }
        }
    }
    // remove variables that are auto generate by the parcelable
    return sb.toString().split("CREATOR")[0]
}

fun Any.toDebugMap() = LinkedHashMap<String, String>().also { map ->
    val separator = "---666---"
    val debugStr = toDebugString(excludeLists = true, excludeLyrics = true, excludeErrorValues = true, separator = separator)
    debugStr.split(separator).forEach {
        if (!it.startsWith("Companion")) {
            val keyValue = it.split(": ")
            if (keyValue.size == 2) {
                map[keyValue[0]] = keyValue[1]
            }
        }
    }
}

/**
 * Limit queue size to MAX_QUEUE_SIZE, to avoid overflows with the media player
 */
fun List<Song>.reduceList() = if (size > MAX_QUEUE_SIZE) { subList(0, MAX_QUEUE_SIZE) } else this
