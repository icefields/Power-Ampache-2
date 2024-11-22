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
package luci.sixsixsix.powerampache2.data.remote.worker

import android.annotation.SuppressLint
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants.ERROR_STRING
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toDownloadedSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.toSong
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import java.net.HttpURLConnection.HTTP_OK
import java.time.Duration
import java.util.UUID

@HiltWorker
class SongDownloadWorker @AssistedInject constructor(
    private val api: MainNetwork,
    val db: MusicDatabase,
    private val storageManager: StorageManager,
    @Assisted val context: Context,
    @Assisted private val params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val authKey = params.inputData.getString(KEY_AUTH_TOKEN)
        val username = params.inputData.getString(KEY_USERNAME)
        val songId = params.inputData.getString(KEY_SONG) ?: ERROR_STRING // this will make the following query return null

        db.dao.getSongById(songId)?.toSong()?.let { song ->
            //val song = db.dao.getSongById(songId).toSong()

            val firstUpdate = workDataOf(KEY_PROGRESS to 0, KEY_SONG to "${song.artist.name} - ${song.name}")
            val lastUpdate = workDataOf(KEY_PROGRESS to 100, KEY_SONG to "${song.artist.name} - ${song.name}")

            setProgress(firstUpdate)

            api.downloadSong(
                authKey = authKey!!,
                songId = songId
            ).run {
                if (code() == HTTP_OK) {
                    // save file to disk and register in database
                    body()?.byteStream()?.let { inputStream ->
                        val filepath = storageManager.saveSong(song, inputStream)
                        db.dao.addDownloadedSong( // TODO fix double-bang!!
                            song.toDownloadedSongEntity(
                                filepath,
                                username!!.lowercase(),
                                serverUrl = db.dao.getCredentials()?.serverUrl!!/*.lowercase()*/
                            )
                        )
                        setProgress(lastUpdate)
                        //if ()
                        Result.success(workDataOf(KEY_RESULT_SONG to songId))
                    } ?: Result.failure(
                        workDataOf(
                            KEY_RESULT_ERROR to "cannot download/save file, " +
                                    "body or input stream NULL response code: ${code()}"))
                } else {
                    Result.failure(
                        workDataOf(
                            KEY_RESULT_ERROR to "cannot download/save file, " +
                                    "response code: ${code()}, response body: ${body().toString()}"))
                }
            }
        }?: run {
            Result.failure(
                workDataOf(
                    KEY_RESULT_ERROR to "Song.mediaId, or Song, is null!, songId:$songId"))
        }
    }

    companion object {
        private const val prefix = "luci.sixsixsix.powerampache2.worker."

        const val KEY_USERNAME = "${prefix}KEY_USERNAME"
        const val KEY_AUTH_TOKEN = "${prefix}KEY_AUTH_TOKEN"
        const val KEY_SONG = "${prefix}KEY_SONG"
        const val KEY_RESULT_SONG = "${prefix}KEY_RESULT_SONG"
        const val KEY_RESULT_ERROR = "${prefix}KEY_RESULT_ERROR"
        const val KEY_PROGRESS = "${prefix}KEY_PROGRESS"

        private const val KEY_WORKER_PREFERENCE = "${prefix}KEY_WORKER_PREFERENCE"
        private const val KEY_WORKER_PREFERENCE_ID = "${prefix}downloadWorkerId"

        suspend fun getDownloadWorkerId(context: Context):String = context
            .getSharedPreferences(KEY_WORKER_PREFERENCE, Context.MODE_PRIVATE)
            .getString(KEY_WORKER_PREFERENCE_ID, null) ?: run {
                // if not existent create one now
                L("resetting worker id")
                resetDownloadWorkerId(context)
            }

        suspend fun resetDownloadWorkerId(context: Context) =
            writeDownloadWorkerId(context, UUID.randomUUID().toString())

        @SuppressLint("ApplySharedPref")
        private suspend fun writeDownloadWorkerId(context: Context, newWorkerId: String): String = withContext(Dispatchers.IO) {
                val sharedPreferences = context.getSharedPreferences(KEY_WORKER_PREFERENCE, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.apply {
                    putString(KEY_WORKER_PREFERENCE_ID, newWorkerId)
                    commit()
                }
            return@withContext newWorkerId
        }


        suspend fun startSongDownloadWorker(
            context: Context,
            authToken: String,
            username: String,
            song: Song
        ): UUID {
            val request = OneTimeWorkRequestBuilder<SongDownloadWorker>()
                .setInputData(
                    workDataOf(
                        KEY_SONG to song.mediaId,
                        KEY_AUTH_TOKEN to authToken,
                        KEY_USERNAME to username)
                ).setConstraints(
                    Constraints(
                        requiresStorageNotLow = true,
                        requiredNetworkType = NetworkType.CONNECTED)
                ).setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.LINEAR,
                    Duration.ofSeconds(10L)
                ).build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(getDownloadWorkerId(context), ExistingWorkPolicy.APPEND, request)
            return request.id
        }

        suspend fun stopAllDownloads(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(getDownloadWorkerId(context))
            // change worker name otherwise cannot restart work
            resetDownloadWorkerId(context)
        }
    }
}
