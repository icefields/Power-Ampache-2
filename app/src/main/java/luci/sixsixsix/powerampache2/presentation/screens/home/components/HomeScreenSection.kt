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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.AmpacheModel
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.presentation.destinations.AlbumDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.destinations.PlaylistDetailScreenDestination
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.screens.artists.components.ArtistItem
import luci.sixsixsix.powerampache2.presentation.screens.home.HomeScreenRowItems
import luci.sixsixsix.powerampache2.presentation.screens.home.LoadingView

typealias PlaylistColumn = ArrayList<Playlist>

@Composable
fun HomeScreenSection(
    navigator: DestinationsNavigator,
    itemsRow: HomeScreenRowItems,
    text: String
) {
    if (itemsRow.isNotEmpty()) {
        Column {
            SectionTitle(text = text)
            SectionRow(navigator = navigator, albumsRowItems = itemsRow)
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.home_row_spacing)))
        }
    } else if ( (itemsRow is HomeScreenRowItems.Nothing) && itemsRow.isLoading) {
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
fun SectionRow(
    navigator: DestinationsNavigator,
    albumsRowItems: HomeScreenRowItems,
) {
    val itemsRow = albumsRowItems.items
    val itemModifier = getItemModifier(albumsRowItems = albumsRowItems)
    if(itemsRow.isNotEmpty()) {
        when (albumsRowItems) {
            is HomeScreenRowItems.Playlists -> {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    val elementsPerColumn = 2
                    val lists = ArrayList<PlaylistColumn>()
                    var currentColumn = PlaylistColumn()
                    for (el in itemsRow) {
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
                            modifier = itemModifier,
                            playlistColumn = column
                        ) {
                            navigator.navigate(PlaylistDetailScreenDestination(playlist = it))
                        }
                    }
                }
            }
            else -> {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(itemsRow) { item: AmpacheModel ->
                        when(item) {
                            is Album -> AlbumItemSquare(
                                modifier = itemModifier
                                    .clickable {
                                        navigator.navigate(
                                            AlbumDetailScreenDestination(
                                                item.id,
                                                item
                                            )
                                        )
                                    },
                                imageSize = if (albumsRowItems is HomeScreenRowItems.Recent)
                                    dimensionResource(id = R.dimen.home_album_item_image_size_recent)
                                else dimensionResource(id = R.dimen.home_album_item_image_size_default),
                                album = item
                            )
                            is Artist -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ArtistItem(
                                        modifier = Modifier
                                            .clickable {
                                                Ampache2NavGraphs.navigateToArtist(item.id, item)
                                            }
                                            .size(dimensionResource(id = R.dimen.home_album_item_image_size_default))
                                            .padding(6.dp),
                                        artist = item
                                    )
                                    HomeItemText(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        title = item.name,
                                        subtitle = stringResource(id = R.string.home_artist_subtitle_item)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getItemModifier(albumsRowItems: HomeScreenRowItems, modifier: Modifier = Modifier): Modifier = when(albumsRowItems) {
    //is HomeScreenRowItems.Recent -> modifier.size(100.dp)
    is HomeScreenRowItems.Playlists -> modifier.heightIn(max = 120.dp)
    else -> modifier.heightIn(max = 260.dp)
}



@Composable
fun SectionRowOld(
    navigator: DestinationsNavigator,
    albumsRow: List<Any>,
    itemModifier: Modifier
) {
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
                        modifier = itemModifier.heightIn(max = 120.dp),
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
                            modifier = itemModifier
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
                        is Artist -> ArtistItem(artist = album)
                    }
                }
            }
        }
    }
}