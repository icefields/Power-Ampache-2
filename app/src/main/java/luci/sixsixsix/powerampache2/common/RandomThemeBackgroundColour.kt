package luci.sixsixsix.powerampache2.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.random.Random

object RandomThemeBackgroundColour {
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
        // not using remainingPlaylistBgColours here, but cycle through colours anyway
        if (remainingPlaylistBgColours.isEmpty()) {
            remainingPlaylistBgColours = ArrayList(playlistBgColours)
        }

        val randomIndex = abs(hash) % playlistBgColours.size
        val randomColour = playlistBgColours[randomIndex]

        // not using remainingPlaylistBgColours here, but cycle through colours anyway
        remainingPlaylistBgColours.remove(randomColour)

        return randomColour
    }

    @Composable
    operator fun invoke(obj: Any): Color {
        val hash = abs(obj.hashCode())
        // not using remainingPlaylistBgColours here, but cycle through colours anyway
        if (remainingPlaylistBgColours.isEmpty()) {
            remainingPlaylistBgColours = ArrayList(playlistBgColours)
        }

        val randomIndex = hash % playlistBgColours.size
        val randomColour = playlistBgColours[randomIndex]

        // not using remainingPlaylistBgColours here, but cycle through colours anyway
        remainingPlaylistBgColours.remove(randomColour)

        return randomColour
    }

    private var remainingPlaylistBgColours = mutableListOf<Color>()

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
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.inverseSurface
            )
}