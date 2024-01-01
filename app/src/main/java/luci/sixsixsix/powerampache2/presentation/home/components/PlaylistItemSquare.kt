package luci.sixsixsix.powerampache2.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.domain.models.Playlist
import kotlin.random.Random

private val width = 150.dp
private val height = 75.dp

@Composable
fun PlaylistItemSquare(
    modifier: Modifier = Modifier,
    playlistColumn: PlaylistColumn,
    onPlaylistClick: (playlist: Playlist) -> Unit
) {
    Column {
        for(playlist in playlistColumn) {
            ColourPlaylistHomeItem(playlist, modifier, getRandomColour(), onPlaylistClick)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun ColourPlaylistHomeItem(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    colour: Color,
    onPlaylistClick: (playlist: Playlist) -> Unit
) {
    Card(
        border = BorderStroke((0.5).dp, Color.Black),
        modifier = modifier
            .width(width)
            .height(height)
            .padding(horizontal = 4.dp)
            .clickable {
                onPlaylistClick(playlist)
            },
        colors = CardDefaults.cardColors(
            containerColor = colour
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                text = playlist.name,
                fontSize = 14.sp,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                lineHeight = (16).sp
            )
        }
    }
}

@Composable
fun getRandomColour(): Color {
    if (remainingPlaylistBgColours.isEmpty()) {
        remainingPlaylistBgColours = ArrayList(playlistBgColours)
    }
    val randomIndex = Random.nextInt(remainingPlaylistBgColours.size)
    val randomColour = remainingPlaylistBgColours[randomIndex]
    remainingPlaylistBgColours.removeAt(randomIndex)
    return randomColour
}

var remainingPlaylistBgColours = mutableListOf<Color>()

val playlistBgColours
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