/**
 * Copyright (C) 2025  Antonio Tari
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
package luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.usecase.ServerInfoStateFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.artists.RecommendedArtistsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.songs.SongFromIdUseCase
import javax.inject.Inject

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val serverInfoStateFlowUseCase: ServerInfoStateFlowUseCase,
    private val songFromIdUseCase: SongFromIdUseCase,
    private val recommendedArtistsUseCase: RecommendedArtistsUseCase
) : ViewModel() {
    private val _recommendedArtistsStateFlow = MutableStateFlow<List<Artist>>(listOf())
    val recommendedArtistsStateFlow = _recommendedArtistsStateFlow.asStateFlow()
    private val _lyrics = MutableStateFlow("")
    val lyrics = _lyrics.asStateFlow()
    private var songId: String = ""

    fun getRecommendedArtists(song: Song) {
        viewModelScope.launch {
            recommendedArtistsUseCase(song.artist.id).collectLatest { result ->
                when(result) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        result.data?.let { artists ->
                            _recommendedArtistsStateFlow.value = artists
                        }
                    }
                }

            }
        }
    }

    fun getSongLyrics(song: Song) {
        // if we already have lyrics, for a song with the same id there is no need to fetch again
        if (lyrics.value.isNotBlank() && song.id == songId) return
        songId = song.id
        viewModelScope.launch {
            // if the lyrics from the Song object are blank, and we're using a Nextcloud backend, the lyrics need to be fetched
            if (song.lyrics.isBlank() && serverInfoStateFlowUseCase().first().isNextcloud == true) {
                songFromIdUseCase(songId)?.let {
                    //if (it.lyrics != "") {
                        _lyrics.value = it.lyrics.replace("\r\n", "<br>")
                    //}
                }
            } else {
                // If lyrics are already attached to the song object, there is no need to fetch.
                _lyrics.value = song.lyrics
            }
        }
    }
}
