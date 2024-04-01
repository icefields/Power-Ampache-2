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
package luci.sixsixsix.powerampache2.data

import android.app.Application
import androidx.lifecycle.asFlow
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.media3.common.PlaybackException
import androidx.media3.datasource.HttpDataSource
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.common.getVersionInfoString
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.ErrorType
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.ScrobbleException
import luci.sixsixsix.powerampache2.domain.errors.ServerUrlNotInitializedException
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(DelicateCoroutinesApi::class)
@Singleton
class ErrorHandlerImpl @Inject constructor(
    private val playlistManager: MusicPlaylistManager,
    private val db: MusicDatabase,
    private val api: MainNetwork,
    private val applicationContext: Application
): ErrorHandler {

    private var isErrorHandlingEnabled = BuildConfig.ENABLE_ERROR_LOG

    init {
        GlobalScope.launch {
            db.dao.settingsLiveData()
                .map { it?.enableRemoteLogging }
                .distinctUntilChanged()
                .asFlow()
                .filterNotNull().collectLatest {
                    if (it != isErrorHandlingEnabled) {
                        isErrorHandlingEnabled = it
                        L.e("ERROR hand enabled? $isErrorHandlingEnabled")
                    }
                }
        }
    }

    override suspend fun <T> invoke(
        label: String,
        e: Throwable,
        fc: FlowCollector<Resource<T>>?,
        onError: (message: String, e: Throwable) -> Unit
    ) {
        // Blocking errors for server url not initialized
        if (e is MusicException && e.musicError.isServerUrlNotInitialized()) {
            L("ServerUrlNotInitializedException")
            fc?.emit(Resource.Loading(false))
            return
        }

        val exceptionString = try { Gson().toJson(e) } catch (jsonException: Exception) { "$e" }

        var readableMessage: String? = null
        StringBuilder(label)
            .append(if (label.isBlank()) "" else " - ")
            .append(
                when (e) {
                    is HttpDataSource.InvalidResponseCodeException -> {
                        readableMessage = "Problem connecting to the server or data source. \nPlay a different track or check your connection\nResponse code: ${e.responseCode}"
                        "HttpDataSource.InvalidResponseCodeException \n$label\n $exceptionString"
                    }

                    is HttpDataSource.HttpDataSourceException -> {
                        readableMessage = "Problem connecting to the server or data source. \nPlay a different track or check your connection"
                        "HttpDataSource.HttpDataSourceException \n$readableMessage\n $exceptionString"
                    }

                    is PlaybackException -> {
                        readableMessage = "This track cannot be played. The issue could be related to your connection, the file might be corrupt, or issues with your server.\nIf it's an offline track please try delete and re-download"
                        "PlaybackException \n$readableMessage\n $exceptionString"
                    }

                    is IOException -> {
                        readableMessage = applicationContext.getString(R.string.error_io_exception)
                        "cannot load data IOException $exceptionString"
                    }

                    is HttpException -> {
                        readableMessage = e.localizedMessage
                        "cannot load data HttpException $exceptionString"
                    }

                    is ServerUrlNotInitializedException ->
                        "ServerUrlNotInitializedException $exceptionString"
                    is ScrobbleException -> {
                        readableMessage = ""
                        ""
                    }
                    is MusicException -> {
                        when (e.musicError.getErrorType()) {
                            ErrorType.ACCOUNT -> {
                                // clear session and try to autologin using the saved credentials
                                //db.dao.clearCachedData()
                                //db.dao.clearPlaylists()
                                db.dao.clearSession()
                                readableMessage = e.musicError.errorMessage
                            }

                            ErrorType.EMPTY ->
                                readableMessage =
                                    applicationContext.getString(R.string.error_empty_result)

                            ErrorType.DUPLICATE ->
                                readableMessage =
                                    applicationContext.getString(R.string.error_duplicate)

                            ErrorType.Other ->
                                readableMessage = e.musicError.errorMessage

                            ErrorType.SYSTEM ->
                                readableMessage = e.musicError.errorMessage
                        }
                        e.musicError.toString()
                    }

                    else -> {
                        readableMessage = e.localizedMessage
                        "generic exception $exceptionString"
                    }
                }
            ).toString().apply {
                // check on error on the emitted data for detailed logging
                fc?.emit(Resource.Error(message = this, exception = e))
                // log and report error here
                logError(this)
                logError(e)
                playlistManager.updateErrorLogMessage(this)
                // readable message here
                readableMessage?.let {
                    // TODO find a better way to not show verbose info
                    //  ie. session expired for timestamp
                    if (e is HttpException || e is IOException) {
                        playlistManager.updateUserMessage(applicationContext.getString(R.string.error_offline))
                    } else if (!readableMessage.lowercase().contains("timestamp") &&
                        !readableMessage.lowercase().contains("expired") &&
                        !readableMessage.lowercase().contains("session")) {
                        playlistManager.updateUserMessage(readableMessage)
                    }
                }
                onError(this, e)
                L.e(readableMessage, e)
            }
    }

    override suspend fun logError(e: Throwable) = logError(message = e.stackTraceToString())

    override suspend fun logError(message: String) {
        try {
            if (isErrorHandlingEnabled && !BuildConfig.URL_ERROR_LOG.isNullOrBlank()) {
                api.sendErrorReport(body = "${getVersionInfoString(applicationContext)}\n$message")
            }
        } catch (e: Exception) {
            L.e(e)
        }
    }
}
