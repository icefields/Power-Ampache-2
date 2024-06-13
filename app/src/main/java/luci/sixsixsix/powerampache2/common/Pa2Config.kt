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
package luci.sixsixsix.powerampache2.common

import luci.sixsixsix.powerampache2.BuildConfig

const val PLAYLIST_SONGS_FETCH_LIMIT = 100
const val PLAYBACK_ERRORS_RETRIES = 16
const val PLAYLIST_FETCH_LIMIT = 0
const val PLAYLIST_ADD_NEW_ENABLE = true
const val RESET_QUEUE_ON_NEW_SESSION = BuildConfig.RESET_QUEUE_ON_NEW_SESSION

data class Pa2Config(
    // use new fast method for adding albums and playlists to playlist
    val playlistAddNewEnable: Boolean = PLAYLIST_ADD_NEW_ENABLE,

    // number of playlist to fetch at once
    val playlistFetchLimit: Int = PLAYLIST_FETCH_LIMIT,

    // reset queue on new session
    val queueResetOnNewSession: Boolean = RESET_QUEUE_ON_NEW_SESSION,

    val dogmazicDemoUser: String = BuildConfig.DOGMAZIC_USER,

    // limit of songs to fetch for playlists
    // - bigger number results in faster fetching
    // - smaller number will result in data becoming visible to the user faster, but it will
    //   take longer to completely fetch big playlists
    val playlistSongsFetchLimit: Int = PLAYLIST_SONGS_FETCH_LIMIT,

    // force login dialog instead of bottom drawer for all versions until google fixes copy paste issue
    val forceLoginDialogsOnAllVersions: Boolean = BuildConfig.FORCE_LOGIN_DIALOG_ON_ALL_VERSIONS,

    // custom text to show on the login form
    val loginWarning: String = "",

    // number of retries in case of playback errors
    val playbackErrorRetries: Int = PLAYBACK_ERRORS_RETRIES,

    // Enable login via token along with username/password
    val enableTokenLogin: Boolean = BuildConfig.ENABLE_TOKEN_LOGIN,

    val dogmazicDemoToken: String = BuildConfig.DOGMAZIC_TOKEN,
    val dogmazicDemoUrl: String = BuildConfig.DOGMAZIC_URL,
)
