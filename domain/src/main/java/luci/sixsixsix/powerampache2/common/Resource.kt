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

sealed class Resource<T>(
    val data: T? = null,
    val exception: Throwable? = null,
    val message: String? = null
) {
    /**
     * if networkData is null means we fetched only from cache/database. If network data is empty
     * and data is not it might the end of the list if an offset present in the request
     */
    class Success<T>(data: T, val networkData: T? = null) : Resource<T>(data)
    class Error<T>(
        data: T? = null,
        exception: Throwable,
        message: String = exception.localizedMessage ?: ""
    ) : Resource<T>(data = data, message = message, exception = exception)

    class Loading<T>(val isLoading: Boolean = true) : Resource<T>()
}
