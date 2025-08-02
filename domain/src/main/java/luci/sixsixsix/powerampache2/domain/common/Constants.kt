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
package luci.sixsixsix.powerampache2.domain.common

import luci.sixsixsix.powerampache2.common.Pa2Config

object Constants {
    const val TIMEOUT_CONNECTION_S = 15L
    const val TIMEOUT_READ_S = 60L
    const val TIMEOUT_WRITE_S = 60L

    const val USER_ACCESS_DEFAULT = 25
    const val USER_EMAIL_DEFAULT = ""
    const val USER_FULL_NAME_PUBLIC_DEFAULT = 0

    const val MAX_QUEUE_SIZE = 666

    // PLAYER BUFFER CONSTANTS
    // default values, in milliseconds
    const val BACK_BUFFER_MS = 30000
    const val BUFFER_MIN_MS = 60000
    const val BUFFER_MAX_MS = 120000
    const val BUFFER_FOR_PLAYBACK_MS = 30000
    const val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 35000
    // max values, in seconds
    const val MIN_BUFFER_MAX = 500
    const val MAX_BUFFER_MAX = 700
    const val BACK_BUFFER_MAX = 200
    const val PLAYBACK_BUFFER_MAX = 200
    const val PLAYBACK_REBUFFER_MAX = 300

    const val PLAYER_CACHE_SIZE_MB = 100
    const val PLAYER_CACHE_SIZE_MB_MAX = 2000
    const val PLAYER_CACHE_SIZE_MB_MIN = 20

    const val REQUEST_LIMIT_ALBUMS = 140

    // ERROR CONSTANTS
    const val NOT_IMPLEMENTED_USER_ID = "666"
    const val ERROR_INT = -1
    const val ERROR_FLOAT = ERROR_INT.toFloat()
    const val ERROR_STRING = "ERROR"
    const val LOADING_STRING = "LOADING"
    const val USER_ID_ERROR = ERROR_INT
    const val DEFAULT_NO_IMAGE = ""

    const val ALWAYS_FETCH_ALL_PLAYLISTS = true

    const val DOGMAZIC_FAKE_EMAIL = "floss.social/@PowerAmpache"
    const val DOGMAZIC_FAKE_NAME = "Draven Wilhelmine"
    const val DOGMAZIC_FAKE_USERNAME = "PowerAmpache"
    const val DOGMAZIC_FAKE_STATE = "Ehime"
    const val DOGMAZIC_FAKE_CITY = "Aoshima"
    const val USER_DEFAULT_MASTODON_URL = "https://floss.social/@powerampache"

    // PLUGINS - LYRICS
    const val PLUGIN_LYRICS_ID = "luci.sixsixsix.powerampache2.lyricsplugin"
    const val PLUGIN_LYRICS_SERVICE_ID = "luci.sixsixsix.powerampache2.lyricsplugin.LyricsFetcherService"
    const val PLUGIN_LYRICS_ACTIVITY_ID = "luci.sixsixsix.powerampache2.lyricsplugin.MainActivity"

    // PLUGINS - METADATA
    const val PLUGIN_INFO_ID = "luci.sixsixsix.powerampache2.infoplugin"
    const val PLUGIN_INFO_SERVICE_ID = "${PLUGIN_INFO_ID}.InfoFetcherService"
    const val PLUGIN_INFO_ACTIVITY_ID = "${PLUGIN_INFO_ID}.MainActivity"

    lateinit var config: Pa2Config
}
