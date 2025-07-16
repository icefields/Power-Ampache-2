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
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginArtistData
import luci.sixsixsix.powerampache2.presentation.dialogs.info.components.InfoDialogText
import luci.sixsixsix.powerampache2.presentation.dialogs.info.components.InfoDialogTextHorizontal

@Composable
fun InfoDialogArtist(artist: Artist, artistPlugin: PluginArtistData?, onDismissRequest: () -> Unit) {
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
                        model = artistPlugin?.imageUrl?.ifBlank { artist.artUrl } ?: artist.artUrl,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.placeholder_album),
                        error = painterResource(id = R.drawable.placeholder_album),
                        contentDescription = artist.name,
                    )
                }
                Spacer(Modifier.height(dimensionResource(R.dimen.separator_height_dialogInfoImage)))

                val tags: HashSet<String> = hashSetOf<String>().apply {
                    artist.genre.forEach {
                        add(it.name.capitalizeWords())
                    }
                    artistPlugin?.tags?.forEach { t ->
                        add(t.capitalizeWords())
                    }
                }
                val name = artistPlugin?.artistName?.ifBlank { artist.name } ?: artist.name
                val description = artistPlugin?.description?.ifBlank { artistPlugin.shortDescription.ifBlank { artist.summary } } ?: artist.summary ?: ""
                val playCount = artistPlugin?.playCount ?: 0
                val songCount = artist.songCount
                val listeners = artistPlugin?.listeners ?: 0
                val yearFormed = artist.yearFormed

                InfoDialogTextHorizontal(name, "")
                Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                if (artist.albumCount > 0)
                    InfoDialogTextHorizontal("Album Count", artist.albumCount.toString())
                if (playCount > 0)
                    InfoDialogTextHorizontal("Play Count", playCount.toString())
                if (listeners > 0)
                    InfoDialogTextHorizontal("Listeners", listeners.toString())
                if (songCount > 0)
                    InfoDialogTextHorizontal("Song Count", songCount.toString())
                if (yearFormed > 0)
                    InfoDialogTextHorizontal("Year Formed", yearFormed.toString())
                if (artist.placeFormed?.isNotBlank() == true && artist.placeFormed != "null") {
                    InfoDialogTextHorizontal("Place Formed", artist.placeFormed ?: "")
                }
                if (artistPlugin?.language?.isNotBlank() == true) {
                    InfoDialogText("Language", artistPlugin.language ?: "")
                }
                artistPlugin?.onTour?.let { onTour ->
                    if (onTour.isNotBlank() == true && onTour == "1") {
                        InfoDialogTextHorizontal("Currently On Tour", "Yes")
                    }
                }

                if (description.isNotBlank()) {
                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    InfoDialogText(null, description)
                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                }
                if (tags.isNotEmpty())
                    InfoDialogText("Tags", tags.joinToString(", "))

                if (artistPlugin?.similar?.isNotEmpty() == true) {
                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                    InfoDialogText("Similar", artistPlugin.similar.joinToString("\n") { it.name })
                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                }
            }
        }
    }
}
