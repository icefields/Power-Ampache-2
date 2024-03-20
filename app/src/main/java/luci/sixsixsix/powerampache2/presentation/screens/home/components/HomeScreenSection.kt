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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.screens.home.LoadingView

typealias PlaylistColumn = ArrayList<Playlist>

const val HOME_LOADING_VIEW_IDENTIFIER = "luci.sixsixsix.powerampache2.presentation.screens.home.loading"

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
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.home_row_spacing)))
        }
    } else if (text == HOME_LOADING_VIEW_IDENTIFIER && albumsRow == null) {
        // TODO this is a hack: passing  a const string as identifier to visualize a loading progress
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
                            modifier = Modifier
                                .heightIn(max = 260.dp)
                                .clickable {
                                    navigator.navigate(
                                        AlbumDetailScreenDestination(
                                            album.id,
                                            album
                                        )
                                    )
                                },
                            album = album
                        )
                    }
                }
            }
        }
    }
}
