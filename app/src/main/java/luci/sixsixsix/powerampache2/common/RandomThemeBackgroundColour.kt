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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import luci.sixsixsix.powerampache2.R
import kotlin.math.abs
import kotlin.random.Random

object RandomThemeBackgroundColour {
    var isDarkTheme = true
    private val hashColourMap = HashMap<Int, Color>()
    private var remainingPlaylistBgColours = mutableListOf<Color>()

    @Composable
    operator fun invoke(): Color {
        if (remainingPlaylistBgColours.isEmpty()) {
            remainingPlaylistBgColours = ArrayList(playlistBgColours())
        }
        val randomIndex = Random.nextInt(remainingPlaylistBgColours.size)
        val randomColour = remainingPlaylistBgColours[randomIndex]
        remainingPlaylistBgColours.removeAt(randomIndex)
        return randomColour
    }

    @Composable
    operator fun invoke(hash: Int): Color = hashColourMap[hash] ?: run {
        if (remainingPlaylistBgColours.isEmpty()) {
            remainingPlaylistBgColours = ArrayList(playlistBgColours())
        }
        val randomIndex = abs(hash) % remainingPlaylistBgColours.size
        val randomColour = remainingPlaylistBgColours[randomIndex]
        hashColourMap[hash] = randomColour
        remainingPlaylistBgColours.removeAt(randomIndex)
        randomColour
    }

    fun reset() {
        hashColourMap.clear()
        remainingPlaylistBgColours.clear()
    }

    @Composable
    operator fun invoke(obj: Any) = invoke(hash = abs(obj.hashCode()))

    @Composable
    fun getBgEqImage(obj: Any) = abs(obj.hashCode()).let {
        val i = it % equalizerCardBgs.size
        equalizerCardBgs[i]
    }

    fun resetColours() {
        hashColourMap.clear()
        remainingPlaylistBgColours.clear()
    }

    private val equalizerCardBgs
        @Composable
        get() = listOf(
            painterResource(id = R.drawable.bg_equalizer_1),
            painterResource(id = R.drawable.bg_equalizer_2),
            painterResource(id = R.drawable.bg_equalizer_3),
            painterResource(id = R.drawable.bg_equalizer_4)
        )

    @Composable
    private fun playlistBgColours() =
        if (isDarkTheme) darkThemeColours else lightThemeColours

    private val lightThemeColours = listOf(
        Color(0xFFCBD4DD),
        Color(0xFFC5E5DB),
        Color(0xFFDDEBD2),
        Color(0xFFFDEEC8),
        Color(0xFFFDDEB9),
        Color(0xFFFBD3BE),
        Color(0xFFFDC4C5)
    )


    private val darkThemeColours = listOf(
        Color(0xFF3E5366),
        Color(0xFF307963),
        Color(0xFF66874D),
        Color(0xFFB18D38),
        Color(0xFFB06B15),
        Color(0xFFAD511F),
        Color(0xFFB12E30)
    )
}