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
package luci.sixsixsix.powerampache2.domain.errors

// this is an internal error, not coming from the server
const val ERROR_CODE_SERVER_URL_NOT_INITIALIZED = "666000666"
const val ERROR_MESSAGE_SERVER_URL_NOT_INITIALIZED = "url not initialized by the user"
class ServerUrlNotInitializedException: MusicException(serverUrlNotInitError)

val serverUrlNotInitError = MusicError(
    errorAction = "authorize",
    errorCode = ERROR_CODE_SERVER_URL_NOT_INITIALIZED,
    errorMessage = ERROR_MESSAGE_SERVER_URL_NOT_INITIALIZED,
    errorType = ERROR_TYPE_ACCOUNT
)