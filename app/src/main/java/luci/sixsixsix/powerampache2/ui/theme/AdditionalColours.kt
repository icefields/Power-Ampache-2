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
package luci.sixsixsix.powerampache2.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import luci.sixsixsix.powerampache2.R

@Stable
class AdditionalColours(
    surfaceContainer: Color,
    surfaceDim: Color,
    surfaceBright: Color,
    surfaceContainerLowest: Color,
    surfaceContainerLow: Color,
    surfaceContainerHigh: Color,
    surfaceContainerHighest: Color,
    shadow: Color,
    queueHandle: Color
) {
    var surfaceContainer by mutableStateOf(surfaceContainer, structuralEqualityPolicy())
        internal set
    var surfaceDim by mutableStateOf(surfaceDim, structuralEqualityPolicy())
        internal set
    var surfaceBright by mutableStateOf(surfaceBright, structuralEqualityPolicy())
        internal set
    var surfaceContainerLowest by mutableStateOf(surfaceContainerLowest, structuralEqualityPolicy())
        internal set
    var surfaceContainerLow by mutableStateOf(surfaceContainerLow, structuralEqualityPolicy())
        internal set
    var surfaceContainerHigh by mutableStateOf(surfaceContainerHigh, structuralEqualityPolicy())
        internal set
    var surfaceContainerHighest by mutableStateOf(surfaceContainerHighest, structuralEqualityPolicy())
        internal set
    var shadow by mutableStateOf(shadow, structuralEqualityPolicy())
        internal set
    var queueHandle by mutableStateOf(queueHandle, structuralEqualityPolicy())
        internal set
}

object AdditionalColoursTheme {
    lateinit var colorScheme: AdditionalColours
}

var ColorScheme.additionalColours: AdditionalColours
    get() = AdditionalColoursTheme.colorScheme
    set(value) {
        AdditionalColoursTheme.colorScheme = value
    }

val AdditionalDarkColours
@Composable
get() = AdditionalColours(
        surfaceDim = colorResource(id = R.color.surfaceDark),
        surfaceBright = Color(0xFF3F4745),
        surfaceContainerLowest  = Color(0xFF161918),
        surfaceContainerLow = Color(0xFF1F2422),
        surfaceContainer = colorResource(id = R.color.surfaceContainerDark),
        surfaceContainerHigh = colorResource(id = R.color.surfaceContainerHighDark),
        surfaceContainerHighest = Color(0xFF38403D),
        shadow = Color(0xFF0B0D0C),
        queueHandle = colorResource(id = R.color.surfaceContainerHighDark)
    )

val AdditionalLightColours
    @Composable
    get() = AdditionalColours(
        surfaceDim = Color(0xFFD7DEDC),
        surfaceBright = colorResource(R.color.surfaceLight),
        surfaceContainerLowest  = Color(0xFFF8FFFD),
        surfaceContainerLow = Color(0xFFEDF5F2),
        surfaceContainer = colorResource(id = R.color.surfaceContainerLight),
        surfaceContainerHigh = colorResource(id = R.color.surfaceContainerHighLight),
        surfaceContainerHighest = Color(0xFFDFE5E3),
        shadow = Color(0xFF0B0D0C),
        queueHandle = colorResource(id = R.color.surfaceContainerHighLight)
    )
