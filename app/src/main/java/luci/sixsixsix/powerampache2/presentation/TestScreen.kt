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
package luci.sixsixsix.powerampache2.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun TestScreen(
    navigator: DestinationsNavigator,
    ) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        ColouredRow(name = "primary", colour = MaterialTheme.colorScheme.primary)
        ColouredRow(name = "onPrimary", colour = MaterialTheme.colorScheme.onPrimary)

        ColouredRow(name = "primaryContainer", colour = MaterialTheme.colorScheme.primaryContainer)
        ColouredRow(name = "onPrimaryContainer", colour = MaterialTheme.colorScheme.onPrimaryContainer)

        ColouredRow(name = "secondary", colour = MaterialTheme.colorScheme.secondary)
        ColouredRow(name = "onSecondary", colour = MaterialTheme.colorScheme.onSecondary)

        ColouredRow(name = "secondaryContainer", colour = MaterialTheme.colorScheme.secondaryContainer)
        ColouredRow(name = "onSecondaryContainer", colour = MaterialTheme.colorScheme.onSecondaryContainer)

        ColouredRow(name = "tertiary", colour = MaterialTheme.colorScheme.tertiary)
        ColouredRow(name = "onTertiary", colour = MaterialTheme.colorScheme.onTertiary)

        ColouredRow(name = "tertiaryContainer", colour = MaterialTheme.colorScheme.tertiaryContainer)
        ColouredRow(name = "onTertiaryContainer", colour = MaterialTheme.colorScheme.onTertiaryContainer)

        ColouredRow(name = "error", colour = MaterialTheme.colorScheme.error)
        ColouredRow(name = "errorContainer", colour = MaterialTheme.colorScheme.errorContainer)
        ColouredRow(name = "onError", colour = MaterialTheme.colorScheme.onError)
        ColouredRow(name = "onErrorContainer", colour = MaterialTheme.colorScheme.onErrorContainer)

        ColouredRow(name = "background", colour = MaterialTheme.colorScheme.background)
        ColouredRow(name = "onBackground", colour = MaterialTheme.colorScheme.onBackground)

        ColouredRow(name = "surface", colour = MaterialTheme.colorScheme.surface)
        ColouredRow(name = "onSurface", colour = MaterialTheme.colorScheme.onSurface)

        ColouredRow(name = "surfaceVariant", colour = MaterialTheme.colorScheme.surfaceVariant)
        ColouredRow(name = "onSurfaceVariant", colour = MaterialTheme.colorScheme.onSurfaceVariant)

        ColouredRow(name = "inverseSurface", colour = MaterialTheme.colorScheme.inverseSurface)
        ColouredRow(name = "inverseOnSurface", colour = MaterialTheme.colorScheme.inverseOnSurface)

        ColouredRow(name = "inversePrimary", colour = MaterialTheme.colorScheme.inversePrimary)
        ColouredRow(name = "surfaceTint", colour = MaterialTheme.colorScheme.surfaceTint)
        ColouredRow(name = "outline", colour = MaterialTheme.colorScheme.outline)
        ColouredRow(name = "outlineVariant", colour = MaterialTheme.colorScheme.outlineVariant)
        ColouredRow(name = "scrim", colour = MaterialTheme.colorScheme.scrim)
    }
}

@Composable fun ColouredRow(name: String, colour: Color) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(colour)
        .padding(10.dp), ) {
        Text(text = "$name  R ${colour.red} G ${colour.green} B ${colour.blue} A ${colour.alpha} ")
    }
}