/**
 * Copyright (C) 2024  Antonio Tari
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
package luci.sixsixsix.powerampache2.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.graphics.ColorUtils
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Song
import java.io.File
import java.lang.ref.WeakReference
import java.security.MessageDigest
import kotlin.math.absoluteValue


typealias WeakContext = WeakReference<Context>

fun String.md5(): String {
    return hashString(this, "MD5")
}

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}

fun Context.shareLink(link: String) =
    startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, link)
                type ="text/plain"
            }, null
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        }
    )

fun Context.openLinkInBrowser(link: String) =
    startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_VIEW, Uri.parse(link)),
            "Open Link In Broser"
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        }
    )

fun Context.exportSong(song: Song) {
    val fileWithinAppDir = File(song.songUrl)
    val fileUri = FileProvider.getUriForFile(this,
        getString(R.string.sharing_provider_authority),
        fileWithinAppDir
    )

    if (fileWithinAppDir.exists()) {
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = song.mime
                    setDataAndType(fileUri, contentResolver.getType(fileUri))
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }, "Export Song"
            ).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            }
        )
    }
}


/**
 * TODO DEBUG
 * this function uses reflection to quickly turn any of the music object into a String to quickly
 * print and visualize data
 */
fun Any.toDebugString(): String {
    val album = this
    val sb = StringBuilder()
    for (field in album.javaClass.declaredFields) {
        field.isAccessible = true

        field.get(album)?.let {
            if(
                !field.name.lowercase().contains("url") &&
                !field.name.lowercase().contains("artist") &&
                !field.name.lowercase().contains("CREATOR") &&
                !field.name.lowercase().contains("\$stable") &&
                "$it".isNotBlank() &&
                "$it" != "0" &&
                !"$it".contains("CREATOR") &&
                !"$it".contains("\$stable") &&
                "$it" != "[]"
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
                    sb.append("\n")


                }
                else if (it is MusicAttribute) {
                    sb.append(field.name)
                        .append(": ")
                        .append("${it.name}")
                        .append("\n")
                } else {
                    sb.append(field.name)
                        .append(": ")
                        .append("${field.get(album)}")
                        .append("\n")
                }
            }
        }
    }
    // remove variables that are auto generate by the parcelable
    return sb.toString().split("CREATOR")[0]
}

@Composable
@ReadOnlyComposable
fun fontDimensionResource(@DimenRes id: Int) = dimensionResource(id = id).value.sp

fun processFlag(flag: Any?): Int = flag?.let {
    if (it is Boolean) {
        if (it == true) { 1 } else { 0 }
        }
    else if (it is Int) { it } else 0
} ?: 0

@ColorInt
fun String.toHslColor(saturation: Float = 0.5f, lightness: Float = 0.4f): Int {
    val hue = fold(0) { acc, char -> char.code + acc * 37 } % 360
    return ColorUtils.HSLToColor(floatArrayOf(hue.absoluteValue.toFloat(), saturation, lightness))
}
