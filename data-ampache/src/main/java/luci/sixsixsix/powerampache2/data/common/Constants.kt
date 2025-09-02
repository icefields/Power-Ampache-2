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
package luci.sixsixsix.powerampache2.data.common

object Constants {
    // LOCAL DB
    const val DB_LOCAL_NAME = "musicdb.db"
    const val DATABASE_VERSION = 86

    const val DB_EMPTY_ATTRIBUTE = "{\"attr\":[]}" // "{\"attr\":[{\"id\":\"209\",\"name\":\"Overkill\"}]}

    // NETWORK
    const val LOCAL_CONFIG_PATH = "/powerampache/config.json"
    const val ALLOW_LOCAL_CONFIG = true
    const val NETWORK_REQUEST_LIMIT_HOME = 40
    const val NETWORK_REQUEST_LIMIT_SONGS_BY_GENRE = 40
    const val QUICK_PLAY_MIN_SONGS = 250
    const val NETWORK_REQUEST_LIMIT_ARTISTS = 30
    const val NETWORK_REQUEST_LIMIT_SIMILAR = 30
    const val NETWORK_REQUEST_LIMIT_SONGS = 40
    const val NETWORK_REQUEST_LIMIT_SONGS_SEARCH = 100

    const val ADMIN_USERNAME = "PowerAmpache"

    // FLAGS
    const val CLEAR_TABLE_AFTER_FETCH = false
}
