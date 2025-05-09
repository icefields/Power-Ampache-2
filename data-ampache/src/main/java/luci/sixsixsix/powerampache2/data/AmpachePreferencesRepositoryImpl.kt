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
package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toAmpachePreference
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.domain.AmpachePreferencesRepository
import luci.sixsixsix.powerampache2.domain.errors.AmpachePreferenceException
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.ampache.AmpachePreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AmpachePreferencesRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    db: MusicDatabase,
    private val errorHandler: ErrorHandler,
): BaseAmpacheRepository(api, db, errorHandler), AmpachePreferencesRepository {

    override suspend fun getAmpacheUserPreferences(): Flow<Resource<List<AmpachePreference>>> = flow {
        emit(Resource.Loading(true))

        val response = api.getUserPreferences(authToken())
        response.error?.let { throw(MusicException(it.toError())) }

        val preferences = response.preferences!!
            .map { preferencesDto -> preferencesDto.toAmpachePreference() } // will throw exception if songs null
            .filter { pref -> pref.category != "interface" }
            .sortedBy { it.category }
        emit(Resource.Success(data = preferences, networkData = preferences))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getAmpacheUserPreferences()", e, this) }

    override suspend fun getAmpacheSystemPreferences(): Flow<Resource<List<AmpachePreference>>> = flow {
        emit(Resource.Loading(true))

        val response = api.getSystemPreferences(authToken())
        response.error?.let { throw(MusicException(it.toError())) }

        val preferences = response.preferences!!
            .map { preferencesDto -> preferencesDto.toAmpachePreference() } // will throw exception if songs null
            .filter { pref -> pref.category != "interface" }
            .sortedBy { it.category }
        emit(Resource.Success(data = preferences, networkData = preferences))
        emit(Resource.Loading(false))
    }.catch { e ->
        L.e("getAmpacheSystemPreferences(), only admin can get system preferences", e, this)
    }

    override suspend fun updateAmpachePreference(filter: String, value: String, applyToAll: Boolean) = flow {
        emit(Resource.Loading(true))

        val response = api.editSystemPreferences(
            authKey = authToken(),
            filter = filter,
            value = value,
            all = if (applyToAll) "1" else "0"
        ).toAmpachePreference()
        // response.error?.let { throw(MusicException(it.toError())) }

        emit(Resource.Success(data = response, networkData = response))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getAmpacheUserPreferences()", AmpachePreferenceException(e), this) }
}
