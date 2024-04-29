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
package luci.sixsixsix.powerampache2.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.DownloadForOffline
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ButtonDownload(
    modifier: Modifier = Modifier,
    isDownloading: Boolean,
    isDownloaded: Boolean = false,
    background: Color = Color.Transparent,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onStartDownloadClick: () -> Unit,
    onStopDownloadClick: () -> Unit
) = ButtonWithLoadingIndicator(
    modifier = modifier,
    imageVector = if (!isDownloaded) (if (!isDownloading) Icons.Outlined.DownloadForOffline else Icons.Outlined.Close) else Icons.Outlined.DownloadDone,
    imageContentDescription= "Download",
    iconTint = iconTint,
    background = background,
    isLoading = isDownloading,
    showBoth = true,
    onClick = if (!isDownloading) onStartDownloadClick else onStopDownloadClick
)


@Composable @Preview
fun PreviewButtonDownload() {
    ButtonDownload(isDownloading = false, isDownloaded = false, onStartDownloadClick = {}) { }
}
