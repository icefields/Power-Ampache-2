package luci.sixsixsix.powerampache2.presentation.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.common.RandomThemeBackgroundColour
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.screens.home.LoadingView

@Composable
fun HomeScreenSection(
    navigator: DestinationsNavigator,
    albumsRow: List<Any>?,
    text: String
) {
    if (!albumsRow.isNullOrEmpty()) {
        Column {
            SectionTitle(text = text)
            SectionRow(navigator = navigator, albumsRow = albumsRow)
            Spacer(modifier = Modifier.height(24.dp))
        }
    } else if (text == "loading" && albumsRow == null) {
        // TODO this is a hac: passing "loading" as identifier to visualize a loading progress
        //  at the bottom while data is loading. A null list in this case means isLoading = true,
        //  and empty list means isLoading = false.
        //  Do this properly!
        LoadingView()
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
    )
}

typealias PlaylistColumn = ArrayList<Playlist>

@Composable
fun SectionRow(navigator: DestinationsNavigator, albumsRow: List<Any>) {
    if(albumsRow.isNotEmpty()) {
        if (albumsRow[0] is Playlist) {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                val elementsPerColumn = 2
                val lists = ArrayList<PlaylistColumn>()
                var currentColumn = PlaylistColumn()
                for (el in albumsRow) {
                    if (currentColumn.size == elementsPerColumn) {
                        lists.add(currentColumn)
                        currentColumn = PlaylistColumn() // reset
                    }
                    currentColumn.add(el as Playlist)
                }
                // add the last one
                if (currentColumn.size <= elementsPerColumn) {
                    lists.add(currentColumn)
                }

                items(lists) { column ->
                    PlaylistItemSquare(
                        modifier = Modifier.heightIn(max = 120.dp),
                        playlistColumn = column
                    ) {
                        navigator.navigate(PlaylistDetailScreenDestination(playlist = it))
                    }
                }
            }
        } else {
            // Mixed mode (playlist + album items)
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(albumsRow) { album: Any ->
                    when(album) {
                        is Album -> AlbumItemSquare(
                            modifier = Modifier.heightIn(max = 260.dp).clickable {
                                navigator.navigate(AlbumDetailScreenDestination(album.id, album))
                            },
                            album = album
                        )
                    }
                }
            }
        }
    }
}
