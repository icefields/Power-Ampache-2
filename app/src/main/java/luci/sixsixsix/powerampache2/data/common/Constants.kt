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
    const val DATABASE_VERSION = 83

    const val DB_EMPTY_ATTRIBUTE = "{\"attr\":[]}" // "{\"attr\":[{\"id\":\"209\",\"name\":\"Overkill\"}]}

    // NETWORK
    const val NETWORK_REQUEST_LIMIT_HOME = 40
    const val NETWORK_REQUEST_LIMIT_SONGS_BY_GENRE = 40
    const val QUICK_PLAY_MIN_SONGS = 50
    const val NETWORK_REQUEST_LIMIT_ARTISTS = 30
    const val NETWORK_REQUEST_LIMIT_SONGS = 40
    const val NETWORK_REQUEST_LIMIT_SONGS_SEARCH = 100
    const val NETWORK_REQUEST_LIMIT_ALBUMS = 140

    const val ADMIN_USERNAME = "PowerAmpache"

    // FLAGS
    const val CLEAR_TABLE_AFTER_FETCH = false

    // TODO: remove, this a copy of a const inside domain
    //  this will cause errors when moving files to modules?
    const val ERROR_INT = luci.sixsixsix.powerampache2.domain.common.Constants.ERROR_INT
    const val ERROR_STRING = luci.sixsixsix.powerampache2.domain.common.Constants.ERROR_STRING
}
