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

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.random.Random

object RandomThemeBackgroundColour {

    private val hashColourMap = HashMap<Int, Color>()
    private var remainingPlaylistBgColours = mutableListOf<Color>()

    @Composable
    operator fun invoke(): Color {
        if (remainingPlaylistBgColours.isEmpty()) {
            remainingPlaylistBgColours = ArrayList(playlistBgColours)
        }
        val randomIndex = Random.nextInt(remainingPlaylistBgColours.size)
        val randomColour = remainingPlaylistBgColours[randomIndex]
        remainingPlaylistBgColours.removeAt(randomIndex)
        return randomColour
    }

    @Composable
    operator fun invoke(hash: Int): Color {
        return hashColourMap[hash] ?: run {
            if (remainingPlaylistBgColours.isEmpty()) {
                remainingPlaylistBgColours = ArrayList(playlistBgColours)
            }
            val randomIndex = abs(hash) % remainingPlaylistBgColours.size
            val randomColour = remainingPlaylistBgColours[randomIndex]
            hashColourMap[hash] = randomColour
            remainingPlaylistBgColours.removeAt(randomIndex)
            randomColour
        }
    }

    @Composable
    operator fun invoke(obj: Any) = invoke(hash = abs(obj.hashCode()))


    private val playlistBgColours
        @Composable
        get() =
            listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceVariant
            )
}