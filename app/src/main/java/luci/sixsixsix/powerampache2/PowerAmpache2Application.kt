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
package luci.sixsixsix.powerampache2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import dagger.hilt.android.HiltAndroidApp
import luci.sixsixsix.powerampache2.common.Constants.TIMEOUT_CONNECTION_S
import luci.sixsixsix.powerampache2.common.Constants.TIMEOUT_READ_S
import luci.sixsixsix.powerampache2.common.Constants.TIMEOUT_WRITE_S
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.worker.SongDownloadWorker
import luci.sixsixsix.powerampache2.di.AmpacheOkHttpClientBuilder
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class PowerAmpache2Application : Application(), ImageLoaderFactory, Configuration.Provider {

    @Inject
    lateinit var workerFactory: SongDownloadWorkerFactory

    @Inject
    lateinit var imageLoaderOkHttpClient: AmpacheOkHttpClientBuilder

    override fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            //each plugin you chose above can be configured in a block like this:
            mailSender {
                //required
                mailTo = BuildConfig.ERROR_REPORT_EMAIL
                //defaults to true
                reportAsFile = true
                //defaults to ACRA-report.stacktrace
                reportFileName = "Crash.txt"
                //defaults to "<applicationId> Crash Report"
                subject = getString(R.string.crash_mail_subject)
                //defaults to empty
                body = getString(R.string.crash_mail_body)
            }
        }
    }

    /**
     * TODO: move to appmodule and inject with hilt
     */
    override fun newImageLoader(): ImageLoader = ImageLoader(this).newBuilder()
        .crossfade(200)
        .placeholder(R.drawable.placeholder_album)
        .error(R.drawable.placeholder_album)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .okHttpClient(
            imageLoaderOkHttpClient(true)
                //.retryOnConnectionFailure(true)
                .connectTimeout(TIMEOUT_CONNECTION_S, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ_S, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_WRITE_S, TimeUnit.SECONDS)
                .build()
        )
        //.respectCacheHeaders(false)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.08)
                .strongReferencesEnabled(false)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .maxSizePercent(0.10)
                .directory(getDir("paimages", MODE_PRIVATE))
                .build()

        }
        //.logger(DebugLogger()) // TODO change in production
        .build()

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()

}

class SongDownloadWorkerFactory @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val storageManager: StorageManager
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = SongDownloadWorker(
        api,
        db,
        storageManager,
        appContext,
        workerParameters
    )
}
