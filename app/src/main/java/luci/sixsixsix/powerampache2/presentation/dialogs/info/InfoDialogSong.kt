package luci.sixsixsix.powerampache2.presentation.dialogs.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.capitalizeWords
import luci.sixsixsix.powerampache2.domain.common.toDebugMap
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginSongData
import luci.sixsixsix.powerampache2.presentation.dialogs.info.components.InfoDialogText
import luci.sixsixsix.powerampache2.presentation.dialogs.info.components.InfoDialogTextHorizontal

@Composable
fun InfoDialogSong(song: Song, songPlugin: PluginSongData?, onDismissRequest: () -> Unit) {
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
                        model = songPlugin?.imageUrl?.ifBlank { song.imageUrl } ?: song.imageUrl,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.placeholder_album),
                        error = painterResource(id = R.drawable.placeholder_album),
                        contentDescription = song.title,
                    )
                }
                Spacer(Modifier.height(dimensionResource(R.dimen.separator_height_dialogInfoImage)))

                val tags: HashSet<String> = hashSetOf<String>().apply {
                    song.genre.forEach {
                        add(it.name.capitalizeWords())
                    }
                    songPlugin?.topTags?.forEach { t ->
                        add(t.capitalizeWords())
                    }
                }
                val name = songPlugin?.title?.ifBlank { song.title } ?: song.title
                val albumName = songPlugin?.albumName?.ifBlank { song.album.name } ?: song.album.name
                val artistAlbum = songPlugin?.artistAlbum?.ifBlank { song.albumArtist.name } ?: song.albumArtist.name
                val artistName = songPlugin?.artistName?.ifBlank { song.artist.name } ?: song.artist.name
                val description = songPlugin?.description?.ifBlank { songPlugin.shortDescription } ?: ""
                val playCount = songPlugin?.playCount ?: 0
                val listeners = songPlugin?.listeners ?: 0
                val duration = songPlugin?.duration ?: 0
                val year = try { (songPlugin?.year ?: "0").toInt() } catch (e: Exception) { 0 }

                InfoDialogTextHorizontal(name, "")
                Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                if (albumName.isNotBlank()) InfoDialogTextHorizontal("Album", albumName)
                if (artistName.isNotBlank()) InfoDialogTextHorizontal("Artist", artistName)
                if (playCount > 0) InfoDialogTextHorizontal("Play Count", playCount.toString())
                if (listeners > 0) InfoDialogTextHorizontal("Listeners", listeners.toString())
                if (duration > 0) InfoDialogTextHorizontal("Duration (seconds)", duration.toString())
                if (year > 0) InfoDialogTextHorizontal("Year", year.toString())
                if (songPlugin?.language?.isNotBlank() == true) InfoDialogText("Language", songPlugin.language)
                if (artistAlbum.isNotBlank()) InfoDialogTextHorizontal("Album Artist", artistAlbum)
                if (description.isNotBlank()) {
                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    InfoDialogText(null, description)
                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                }
                if (tags.isNotEmpty()) InfoDialogText("Tags", tags.joinToString("\n"))

                Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))

                for (entry in song.toDebugMap()) {
                    if (entry.key != "album") { // album already included earlier
                        if (entry.key != "filename")
                            InfoDialogTextHorizontal(entry.key, entry.value)
                        else
                            InfoDialogText(entry.key, entry.value)
                    }
                }
                //InfoContent(song.toDebugMap(), onDismissRequest)
            }
        }
    }
}
/*
    val songUrl: String = "",
    val imageUrl: String = "",
    val bitrate: Int = Constants.ERROR_INT,
    val streamBitrate: Int = Constants.ERROR_INT,
    val catalog: Int = Constants.ERROR_INT,
    val channels: Int = Constants.ERROR_INT,
    val composer: String = "",
    val filename: String = "",
    val genre: List<MusicAttribute> = listOf(),
    val mime: String? = null,
    val playCount: Int = Constants.ERROR_INT,
    val playlistTrackNumber: Int = Constants.ERROR_INT,
    val rateHz: Int = Constants.ERROR_INT,
    val size: Int = Constants.ERROR_INT,
    val time: Int = Constants.ERROR_INT,
    val trackNumber: Int = Constants.ERROR_INT,
    val year: Int = Constants.ERROR_INT,
    val name: String = "",
    val mode: String? = null,
    val artists: List<MusicAttribute> = listOf(),
    val flag: Int = 0,
    val streamFormat: String? = null,
    val format: String? = null,
    val streamMime: String? = null,
    val publisher: String? = null,
    val replayGainTrackGain: Float? = null,
    val replayGainTrackPeak: Float? = null,
    val disk: Int = Constants.ERROR_INT,
    val diskSubtitle: String = "",
    val mbId: String = "",
    val comment: String = "",
    val language: String = "",
    val lyrics: String = "",
    val albumMbId: String = "",
    val artistMbId: String = "",
    val albumArtistMbId: String = "",
    val averageRating: Float,
    val preciseRating: Float,
    val rating: Float,

    val imageAlbum: String,
    val imageArtist: String,
    val position: Int
 */