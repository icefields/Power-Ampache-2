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
import luci.sixsixsix.powerampache2.common.Constants.PLAY_STORE_URL
import luci.sixsixsix.powerampache2.domain.models.Song
import java.io.File
import kotlin.math.absoluteValue

val Int.dpTextUnit: TextUnit
    @Composable
    get() = with(LocalDensity.current) { this@dpTextUnit.dp.toSp() }

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
            getString(R.string.share_open_link_browser)
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
                }, getString(R.string.share_export_song)
            ).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            }
        )
    }
}


fun getVersionInfoString(context: Context) = try {
    val pInfo: PackageInfo =
        context.packageManager.getPackageInfo(context.packageName, 0)
    "${pInfo.versionName} (${pInfo.longVersionCode})"
} catch (e: PackageManager.NameNotFoundException) {
    e.printStackTrace()
    ""
}// + " - DB: ${Constants.DATABASE_VERSION}"

@Composable
@ReadOnlyComposable
fun fontDimensionResource(@DimenRes id: Int) = dimensionResource(id = id).value.sp


@ColorInt
fun String.toHslColor(saturation: Float = 0.5f, lightness: Float = 0.4f): Int {
    val hue = fold(0) { acc, char -> char.code + acc * 37 } % 360
    return ColorUtils.HSLToColor(floatArrayOf(hue.absoluteValue.toFloat(), saturation, lightness))
}

fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
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
