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

const val ALBUM_HIGHEST_FETCH_LIMIT = 500
const val PLAYLIST_SONGS_FETCH_LIMIT = 100
const val PLAYBACK_ERRORS_RETRIES = 16
const val PLAYLIST_FETCH_LIMIT = 0
const val PLAYLIST_ADD_NEW_ENABLE = true
const val API_RECORD_PLAY_ENABLE = true
const val SETTINGS_IS_DOWNLOAD_SDCARD = true
const val FORCE_SKIP_NETWORK_ERROR = false

const val CLEAR_LIBRARY_ON_CATALOG_CLEAN = true
const val FETCH_ALBUMS_WITH_ARTISTS = true
const val INTRO_MESSAGE_LOCAL_ASSET = "local::asset"
const val INTRO_MESSAGE_REMOTE_VERSION = "remote::version"
const val INTRO_MESSAGE_DEFAULT = ""

// limit the amount of songs fetched for stats
const val SONGS_DEFAULT_LIMIT_FETCH = 5000
const val SONGS_HIGHEST_LIMIT_FETCH = 5000
const val SONGS_FLAGGED_LIMIT_FETCH = 5000
const val SONGS_FREQUENT_LIMIT_FETCH = 5000
const val SONGS_RECENT_LIMIT_FETCH = 666

data class Pa2Config(
    // use new fast method for adding albums and playlists to playlist
    val playlistAddNewEnable: Boolean = PLAYLIST_ADD_NEW_ENABLE,

    // number of playlist to fetch at once
    val playlistFetchLimit: Int = PLAYLIST_FETCH_LIMIT,

    // reset queue on new session
    val queueResetOnNewSession: Boolean,// = RESET_QUEUE_ON_NEW_SESSION,

    val dogmazicDemoUser: String,// = DOGMAZIC_DEMO_USER,

    // limit of songs to fetch for playlists
    // - bigger number results in faster fetching
    // - smaller number will result in data becoming visible to the user faster, but it will
    //   take longer to completely fetch big playlists
    val playlistSongsFetchLimit: Int = PLAYLIST_SONGS_FETCH_LIMIT,

    // force login dialog instead of bottom drawer for all versions until google fixes copy paste issue
    val forceLoginDialogsOnAllVersions: Boolean,// = FORCE_LOGIN_DIALOG_ON_ALL_VERSIONS,

    // custom text to show on the login form
    val loginWarning: String = "",

    // number of retries in case of playback errors
    val playbackErrorRetries: Int = PLAYBACK_ERRORS_RETRIES,

    // Enable login via token along with username/password
    val enableTokenLogin: Boolean,// = ENABLE_TOKEN_LOGIN,

    val dogmazicDemoToken: String,// = DOGMAZIC_TOKEN,
    val dogmazicDemoUrl: String,// = DOGMAZIC_URL,

    // fetch user playlists before fetching the bulk of all server playlists
    val playlistsUserFetch: Boolean,// = PLAYLISTS_USER_FETCH,
    // fetch user smartlists before fetching the bulk of all server playlists
    val smartlistsUserFetch: Boolean,// = SMARTLISTS_USER_FETCH,
    // fetch playlists owned by a defined admin user
    val playlistsAdminFetch: Boolean,// = PLAYLISTS_ADMIN_FETCH,
    // fetch smartlists owned by a defined admin user
    val smartlistsAdminFetch: Boolean,// = SMARTLISTS_ADMIN_FETCH,
    // fetch all playlists from server
    val playlistsServerAllFetch: Boolean,// = PLAYLISTS_ALL_SERVER_FETCH,
    // clear the library when a newer "clean" date in the handshake response
    val clearLibraryOnCatalogClean: Boolean = CLEAR_LIBRARY_ON_CATALOG_CLEAN,
    // message to show at login, if any present
    val introMessage: String = INTRO_MESSAGE_DEFAULT,
    val isIntroMessageLocal: Boolean = introMessage == INTRO_MESSAGE_LOCAL_ASSET,
    val shouldShowIntroMessage: Boolean = introMessage != "",
    // enable or disable the option in settings to download music to device sd card
    val isDownloadsSdCardOptionEnabled: Boolean = SETTINGS_IS_DOWNLOAD_SDCARD,
    // enable record_play call for every played song
    val isRecordPlayApiEnabled: Boolean = API_RECORD_PLAY_ENABLE,
    // force skip track on network errors
    val forceSkipOnNetworkError: Boolean = FORCE_SKIP_NETWORK_ERROR,
    // when fetching artists also fetch their albums
    val fetchAlbumsWithArtist: Boolean = FETCH_ALBUMS_WITH_ARTISTS,
    // fetch limit for the top rated albums
    val albumHighestFetchLimit: Int = ALBUM_HIGHEST_FETCH_LIMIT,

    // amount of songs fetched for stats requests
    val songsHighestFetchLimit: Int = SONGS_HIGHEST_LIMIT_FETCH,
    val songsFlaggedFetchLimit: Int = SONGS_FLAGGED_LIMIT_FETCH,
    val songsFrequentFetchLimit: Int = SONGS_FREQUENT_LIMIT_FETCH,
    val songsRecentFetchLimit: Int = SONGS_RECENT_LIMIT_FETCH,

    // override the limit with the size of the local data, expensive on network on the long term
    val useIncrementalLimitForAlbums: Boolean,// = USE_INCREMENTAL_LIMIT_ALBUMS
)
