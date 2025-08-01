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

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.common.toDebugString
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.Artist
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.plugin.info.PluginSongData
import luci.sixsixsix.powerampache2.domain.plugin.lyrics.getAvailableLyrics
import luci.sixsixsix.powerampache2.domain.usecase.ServerInfoStateFlowUseCase
import luci.sixsixsix.powerampache2.domain.usecase.artists.RecommendedArtistsUseCase
import luci.sixsixsix.powerampache2.domain.usecase.plugin.AlbumDataFromPluginUseCase
import luci.sixsixsix.powerampache2.domain.usecase.plugin.IsInfoPluginInstalled
import luci.sixsixsix.powerampache2.domain.usecase.plugin.IsLyricsPluginInstalledUseCase
import luci.sixsixsix.powerampache2.domain.usecase.plugin.LyricsFromPluginUseCase
import luci.sixsixsix.powerampache2.domain.usecase.plugin.SongDataFromPluginUseCase
import luci.sixsixsix.powerampache2.domain.usecase.songs.SongFromIdUseCase
import javax.inject.Inject

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val serverInfoStateFlowUseCase: ServerInfoStateFlowUseCase,
    private val songFromIdUseCase: SongFromIdUseCase,
    private val recommendedArtistsUseCase: RecommendedArtistsUseCase,
    private val isLyricsPluginInstalledUseCase: IsLyricsPluginInstalledUseCase,
    private val getLyricsUrlFromPluginUseCase: LyricsFromPluginUseCase,
    private val isInfoPluginInstalled: IsInfoPluginInstalled,
    private val getSongInfoPluginUseCase: SongDataFromPluginUseCase,
) : ViewModel() {
    private val _recommendedArtistsStateFlow = MutableStateFlow<List<Artist>>(listOf())
    val recommendedArtistsStateFlow = _recommendedArtistsStateFlow.asStateFlow()

    private val _pluginInfo = MutableStateFlow<PluginSongData?>(null)
    val pluginInfo = _pluginInfo.asStateFlow()

    private val _lyrics = MutableStateFlow("")
    val lyrics = _lyrics.asStateFlow()

    private val _pluginLyrics = MutableStateFlow("")
    val pluginLyrics = _pluginLyrics.asStateFlow()

    private var songId: String = ""
    private var lyricsJob: Job? = null
    private var songInfoJob: Job? = null

    private suspend fun getRecommendedArtists(song: Song) {
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

    fun onNewSong(song: Song) {
        lyricsJob?.cancel()
        lyricsJob = viewModelScope.launch {
            getSongLyrics(song)
            getSongLyricsFromPlugin(song)
        }

        songInfoJob?.cancel()
        songInfoJob = viewModelScope.launch {
            getSongInfoFromPlugin(song)
        }

        viewModelScope.launch {
            getRecommendedArtists(song)
        }
    }

    private suspend fun getSongLyricsFromPlugin(song: Song) {
        _pluginLyrics.value = ""
        // only fetch if no lyrics already present
        if (lyrics.value.isBlank() && isLyricsPluginInstalledUseCase()) {
            _pluginLyrics.value = getLyricsUrlFromPluginUseCase(
                songTitle = song.title,
                albumTitle = song.album.name,
                artistName = song.artist.name
            ).getAvailableLyrics()
        }
    }


    private suspend fun getSongLyrics(song: Song) {
        // if we already have lyrics, for a song with the same id there is no need to fetch again
        if (lyrics.value.isNotBlank() && song.id == songId) return
        songId = song.id
        // if the lyrics from the Song object are blank, and we're using a Nextcloud backend, the lyrics need to be fetched
        if (song.lyrics.isBlank() && serverInfoStateFlowUseCase().first().isNextcloud == true) {
            songFromIdUseCase(songId)?.let {
                _lyrics.value = it.lyrics.replace("\r\n", "<br>")
            }
        } else {
            // If lyrics are already attached to the song object, there is no need to fetch.
            _lyrics.value = song.lyrics
        }
    }

    private suspend fun getSongInfoFromPlugin(song: Song) {
        // only fetch if no lyrics already present
        if (isInfoPluginInstalled()) {
            _pluginInfo.value = getSongInfoPluginUseCase(song)
        }
    }
}
