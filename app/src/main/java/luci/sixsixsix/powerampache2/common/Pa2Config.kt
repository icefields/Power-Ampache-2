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

const val PLAYLIST_FETCH_LIMIT = 100
const val PLAYLIST_ADD_NEW_ENABLE = true
const val RESET_QUEUE_ON_NEW_SESSION = BuildConfig.RESET_QUEUE_ON_NEW_SESSION
const val DOGMAZIC_USER = BuildConfig.DOGMAZIC_USER

data class Pa2Config(
    // use new fast method for adding albums and playlists to playlist
    val playlistAddNewEnable: Boolean = PLAYLIST_ADD_NEW_ENABLE,

    // reset queue on new session
    val queueResetOnNewSession: Boolean = RESET_QUEUE_ON_NEW_SESSION,

    val dogmazicDemoUser: String = DOGMAZIC_USER,

    // limit of songs to fetch for playlists
    // - bigger number results in faster fetching
    // - smaller number will result in data becoming visible to the user faster, but it will
    //   take longer to completely fetch big playlists
    val playlistSongsFetchLimit: Int = PLAYLIST_FETCH_LIMIT,
)
