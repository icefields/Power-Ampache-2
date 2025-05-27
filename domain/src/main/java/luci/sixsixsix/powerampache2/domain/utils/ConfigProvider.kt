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
package luci.sixsixsix.powerampache2.domain.utils

import luci.sixsixsix.powerampache2.common.Pa2Config

interface ConfigProvider {
    val USE_INCREMENTAL_LIMIT_ALBUMS: Boolean
    val RESET_QUEUE_ON_NEW_SESSION: Boolean
    val PLAYLISTS_USER_FETCH: Boolean
    val SMARTLISTS_USER_FETCH: Boolean
    val PLAYLISTS_ADMIN_FETCH: Boolean
    val SMARTLISTS_ADMIN_FETCH: Boolean
    val PLAYLISTS_ALL_SERVER_FETCH: Boolean
    val DOGMAZIC_DEMO_USER: String
    val FORCE_LOGIN_DIALOG_ON_ALL_VERSIONS: Boolean
    val ENABLE_TOKEN_LOGIN: Boolean
    val DOGMAZIC_TOKEN: String
    val DOGMAZIC_URL: String
    val CONFIG_URL: String
    val AMPACHE_USER_AGENT: String
    val API_KEY: String
    val IS_DEBUG: Boolean
    val URL_ERROR_LOG: String
    val SHOW_EMPTY_PLAYLISTS: Boolean
    val VERSION_CODE: Int
    val ENABLE_ERROR_LOG: Boolean

    fun defaultPa2Config(): Pa2Config
}
