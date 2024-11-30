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
package luci.sixsixsix.powerampache2.presentation.screens.settings.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.common.TextWithSubtitle

@Composable
fun PlayerSettingsView(
    modifier: Modifier,
    backBuffer: Int,
    minBuffer: Int,
    maxBuffer: Int,
    bufferForPlayback: Int,
    bufferForPlaybackAfterRebuffer: Int,
    onBackBufferChange: (newValue: Int) -> Unit,
    onMinBufferChange: (newValue: Int) -> Unit,
    onMaxBufferChange: (newValue: Int) -> Unit,
    onBufferForPlaybackChange: (newValue: Int) -> Unit,
    onBufferForPlaybackAfterRebufferChange: (newValue: Int) -> Unit,
    onResetValuesClick: () -> Unit
) {
    var showSettings by remember { mutableStateOf(false) }

    TextWithSubtitle(
        modifier = modifier,
        title = R.string.settings_playerAdvancedSettings_title,
        subtitle = R.string.settings_playerAdvancedSettings_subtitle,
        onClick = {
            showSettings = !showSettings
        }
    )

    if (showSettings) {
        val spacerHeight = 15.dp

        Card(
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(9.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
            ) {
                PlayerBufferSettingSlider(
                    R.string.settings_minBuffer_title,
                    R.string.settings_minBuffer_subtitle,
                    0, 100,
                    minBuffer,
                    onMinBufferChange
                )

                Spacer(Modifier.height(spacerHeight))

                PlayerBufferSettingSlider(
                    R.string.settings_maxBuffer_title,
                    R.string.settings_maxBuffer_subtitle,
                    10, 60 * 10,
                    maxBuffer,
                    onMaxBufferChange
                )

                Spacer(Modifier.height(spacerHeight))

                PlayerBufferSettingSlider(
                    R.string.settings_bufferForPlayback_title,
                    R.string.settings_bufferForPlayback_subtitle,
                    0, 100,
                    bufferForPlayback,
                    onBufferForPlaybackChange
                )

                Spacer(Modifier.height(spacerHeight))

                PlayerBufferSettingSlider(
                    R.string.settings_bufferForPlaybackAfterRebuffer_title,
                    R.string.settings_bufferForPlaybackAfterRebuffer_subtitle,
                    0, 100,
                    bufferForPlaybackAfterRebuffer,
                    onBufferForPlaybackAfterRebufferChange
                )

                Spacer(Modifier.height(spacerHeight))

                PlayerBufferSettingSlider(
                    R.string.settings_backBuffer_title,
                    R.string.settings_backBuffer_subtitle,
                    0, 100,
                    backBuffer,
                    onBackBufferChange
                )

                Spacer(Modifier.height(22.dp))

                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    shape = RoundedCornerShape(10.dp),
                    onClick = onResetValuesClick,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 9.dp),
                        text = "Reset Defaults",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(spacerHeight))

    }
}

@Composable
fun PlayerBufferSettingSlider(
    @StringRes title: Int,
    @StringRes subtitle: Int,
    min: Int,
    max: Int,
    sliderValue: Int,
    onValueChange: (newValue: Int) -> Unit,
) {
    Column {
        TextWithSubtitle(
            title = title,
            subtitle = subtitle,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "$sliderValue Seconds",
            //fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
        )
        Slider(
            value = sliderValue.toFloat(),
            onValueChange = { newValue ->
                onValueChange(newValue.toInt())
            },
            valueRange = min.toFloat()..max.toFloat(),
            //steps = 1, // Number of steps for discrete values (optional)
        )
    }
}

@Preview
@Composable
fun PlayerSettingsViewPreview() {
    PlayerSettingsView(
        modifier = Modifier,
        backBuffer = 30,
        minBuffer = 30,
        maxBuffer =  120000,
        bufferForPlayback = 30,
        bufferForPlaybackAfterRebuffer = 22,
        onBackBufferChange = { },
    onMinBufferChange = {},
    onMaxBufferChange = {},
    onBufferForPlaybackChange = {},
        onResetValuesClick = {},
    onBufferForPlaybackAfterRebufferChange = {},
    )
}
