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
package luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel

import luci.sixsixsix.powerampache2.domain.models.Song

sealed class MainEvent {
    data class OnSearchQueryChange(val query: String): MainEvent()
    data object OnDismissUserMessage: MainEvent()
    data object OnEnableOfflineMode: MainEvent()
    data object OnLogout: MainEvent() // TODO move this to AuthViewModel
    data class AddSongsToQueueAndPlay(val song: Song, val songList: List<Song>): MainEvent()
    data class AddSongsToQueueAndPlayShuffled(val songList: List<Song>): MainEvent()
    data class PlaySongAddToQueueTop(val song: Song, val songList: List<Song>): MainEvent()
    data class PlaySongReplacePlaylist(val song: Song, val songList: List<Song>): MainEvent()
    data class PlaySong(val song: Song): MainEvent()
    data object PlayPauseCurrent: MainEvent()
    data object SkipNext: MainEvent()
    data object SkipPrevious: MainEvent()
    data object Forward: MainEvent()
    data object Backwards: MainEvent()
    data object Repeat: MainEvent()
    data class Shuffle(val shuffleOn: Boolean): MainEvent()
    data object Reset: MainEvent()
    data object FavouriteSong: MainEvent()
    data class UpdateProgress(val newProgress: Float): MainEvent()
    data class OnAddSongToQueue(val song: Song): MainEvent()
    data class OnAddSongToPlaylist(val song: Song): MainEvent()
    data class OnAddSongToQueueNext(val song: Song): MainEvent()
    data class OnShareSong(val song: Song): MainEvent()
    data class OnRateSong(val song: Song, val rate: Int): MainEvent()
    data class OnDownloadSong(val song: Song): MainEvent()
    data class OnDownloadSongs(val songs: List<Song>): MainEvent()
    data object OnStopDownloadSongs: MainEvent()
    data class OnDownloadedSongDelete(val song: Song): MainEvent()
    data class OnExportDownloadedSong(val song: Song): MainEvent()
    data object OnFabPress: MainEvent()
}
