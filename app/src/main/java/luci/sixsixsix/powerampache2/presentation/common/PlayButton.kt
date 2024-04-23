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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PlayButton(
    modifier: Modifier = Modifier
        .height(60.dp)
        .widthIn(min = 60.dp, max = 90.dp),
    isBuffering: Boolean,
    isPlaying: Boolean,
    showLoadingWhileBuffering: Boolean = false,
    enabled: Boolean = true,
    iconTint: Color = MaterialTheme.colorScheme.inverseOnSurface,
    background: Color = MaterialTheme.colorScheme.inverseSurface,
    onEvent: () -> Unit
) {
    IconButton(
        modifier = modifier.alpha(if (enabled) 1.0f else 0.2f),
        onClick = {
            if (!isBuffering)
                onEvent()
        },
        enabled = enabled && !isBuffering
    ) {
        Card(
            modifier = modifier.padding(0.dp),
            shape = RoundedCornerShape(40.dp),
            colors = CardDefaults.cardColors(containerColor = background)
        ) {
            Box(
                modifier = Modifier.wrapContentSize(),
                contentAlignment = Alignment.Center
            ) {
                if (!showLoadingWhileBuffering || !isBuffering) {
                    // if showLoadingWhileBuffering is false always go in this block
                    Icon(
                        tint = iconTint,
                        modifier = Modifier
                            .aspectRatio(1f / 1f)
                            .padding(13.dp),
                        imageVector = if ((!isPlaying/* && !isBuffering*/)
                            // if not enable always show play icon
                            || !enabled) {
                            Icons.Filled.PlayArrow
                        } else {
                            Icons.Filled.Pause
                        },
                        contentDescription = "Play"
                    )
                } else {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize().padding(6.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun PlayButtonPreview() {
    PlayButton(
        showLoadingWhileBuffering = false,
        isBuffering = true, isPlaying = true) {
    }
}