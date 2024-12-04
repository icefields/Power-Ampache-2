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
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Patterns
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.graphics.ColorUtils
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.Constants.PLAY_STORE_URL
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

fun String.isIpAddress(): Boolean =
    //InetAddresses.isNumericAddress(this) // requires min Q
    Patterns.IP_ADDRESS.matcher(this).matches()
    //("(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])").toRegex().matches(this)

val Int.dpTextUnit: TextUnit
    @Composable
    get() = with(LocalDensity.current) { this@dpTextUnit.dp.toSp() }

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

fun Context.goToPlayStore() {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(PLAY_STORE_URL)
        setPackage("com.android.vending")
    }
    startActivity(intent)
}

fun Context.openLinkInBrowser(link: String) =
    startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_VIEW, Uri.parse(link)),
            "Open Link In Broser"
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        }
    )

fun Context.exportSong(song: Song, offlineUri: String) {
    val fileWithinAppDir = File(offlineUri)
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
                            (!valueStr.startsWith(Constants.ERROR_STRING) &&
                             !valueStr.startsWith(Constants.ERROR_TITLE) &&
                             !valueStr.startsWith(Constants.ERROR_INT.toString()) &&
                             !valueStr.startsWith(Constants.ERROR_FLOAT.toString())
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

fun getVersionInfoString(context: Context) = try {
    val pInfo: PackageInfo =
        context.packageManager.getPackageInfo(context.packageName, 0)
    "${pInfo.versionName} (${pInfo.longVersionCode})"
} catch (e: PackageManager.NameNotFoundException) {
    e.printStackTrace()
    ""
} + " - DB: ${Constants.DATABASE_VERSION}"

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

@Composable
@ReadOnlyComposable
fun fontDimensionResource(@DimenRes id: Int) = dimensionResource(id = id).value.sp

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
            else -> Constants.ERROR_FLOAT
        }
    } catch (e: Exception) {
        Constants.ERROR_FLOAT
    }
} ?: Constants.ERROR_FLOAT

/**
 * if hasArt flag is present (not null), check if there's any art with it.
 */
fun processArtUrl(hasArt: Any?, artUrl: String?) = hasArt?.let { hasArtAny ->
    if (artUrl != null && processFlag(hasArtAny) == 1) { artUrl } else { "" }
} ?: run {
    artUrl ?: ""
}

@ColorInt
fun String.toHslColor(saturation: Float = 0.5f, lightness: Float = 0.4f): Int {
    val hue = fold(0) { acc, char -> char.code + acc * 37 } % 360
    return ColorUtils.HSLToColor(floatArrayOf(hue.absoluteValue.toFloat(), saturation, lightness))
}

fun Modifier.shimmer(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(1000)),
        label = ""
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.onBackground,
                MaterialTheme.colorScheme.primary
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat()),

        )
    ).onGloballyPositioned {
        size = it.size
    }
}
