package luci.sixsixsix.powerampache2.presentation.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginAlbumData

/*
    val language: String,
    val url: String,
    val imageArtist: String,
    val urlArtist: String,
    val tracks: List<AlbumTrack>

    val tracks: List<Song> = listOf(),
    val songCount: Int = 0,
 */
@Composable
fun InfoDialogAlbum(album: Album, albumPlugin: PluginAlbumData?, onDismissRequest: () -> Unit) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = { onDismissRequest() }
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                Card(
                    //border = BorderStroke((0.5).dp, MaterialTheme.colorScheme.background),
                    modifier = Modifier.background(Color.Transparent),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(0.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .aspectRatio(1f/1f),
                        model = albumPlugin?.imageUrl?.ifBlank { album.artUrl } ?: album.artUrl,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.placeholder_album),
                        error = painterResource(id = R.drawable.placeholder_album),
                        contentDescription = album.name,
                    )
                }

                val tags: HashSet<String> = hashSetOf<String>().apply {
                    album.genre.forEach {
                        add(it.name)
                    }
                    albumPlugin?.tags?.let { tagList ->
                        addAll(tagList)
                    }
                }
                val name = albumPlugin?.albumName?.ifBlank { album.name } ?: album.name
                val artist = albumPlugin?.artistName?.ifBlank { album.artist.name } ?: album.artist.name
                val duration = albumPlugin?.duration?.let { dur ->
                    if (dur > 0) dur else album.time
                } ?: album.time
                val description = albumPlugin?.description?.ifBlank { albumPlugin.shortDescription } ?: albumPlugin?.shortDescription ?: ""
                val playCount = albumPlugin?.playCount ?: 0
                val listeners = albumPlugin?.listeners ?: 0
                val rank = albumPlugin?.rank ?: 0

                InfoDialogTextHorizontal("$artist - $name", "")
                Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                if (album.artists.isNotEmpty())
                    InfoDialogText("Artists", album.artists.joinToString(", ") { it.name })
                InfoDialogTextHorizontal("Duration", duration.toString())
                InfoDialogTextHorizontal("Year", albumPlugin?.year?.ifBlank { album.year.toString() } ?: album.year.toString())
                if (album.diskCount > 0)
                    InfoDialogTextHorizontal("Disc Count", album.diskCount.toString())
                if (playCount > 0)
                    InfoDialogTextHorizontal("Play Count", playCount.toString())
                if (listeners > 0)
                    InfoDialogTextHorizontal("Listeners", listeners.toString())
                if (rank > 0)
                    InfoDialogTextHorizontal("Rank", rank.toString())
                if (description.isNotBlank()) {
                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    InfoDialogText(null, description)
                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                }
                if (tags.isNotEmpty())
                    InfoDialogText("Tags", tags.joinToString(", "))

            }
        }
    }
}

@Composable
fun InfoDialogTextHorizontal(key: String? = null, text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(vertical = 4.dp, horizontal = 4.dp)) {
        key?.let {
            Text(
                text = it,
                modifier = Modifier
                    .wrapContentSize(Alignment.CenterStart),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = text,
            modifier = Modifier
                .wrapContentSize(Alignment.CenterStart),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

@Composable
fun InfoDialogText(key: String? = null, text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 4.dp, horizontal = 6.dp)) {
        key?.let {
            Text(
                text = it,
                modifier = Modifier
                    .wrapContentSize(Alignment.CenterStart),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = text,
            modifier = Modifier
                .wrapContentSize(Alignment.CenterStart),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}