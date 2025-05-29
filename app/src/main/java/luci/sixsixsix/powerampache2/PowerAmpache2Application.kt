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
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.utils.ConfigProvider
import luci.sixsixsix.powerampache2.domain.utils.ImageLoaderProvider
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import javax.inject.Inject

@HiltAndroidApp
class PowerAmpache2Application : Application(), ImageLoaderFactory, Configuration.Provider {

    @Inject
    lateinit var workerFactoryConfiguration: Configuration

    @Inject
    lateinit var imageLoaderBuilder: ImageLoader.Builder

    @Inject
    lateinit var configProvider: ConfigProvider

    override fun onCreate() {
        super.onCreate()
        // initialize the default values for the config, new values will be fetched by a network call
        Constants.config = configProvider.defaultPa2Config()
    }

    override fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            //buildConfigClass = BuildConfig::class.java

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

    override fun newImageLoader(): ImageLoader = imageLoaderBuilder
        .placeholder(R.drawable.placeholder_album)
        .error(R.drawable.placeholder_album)
        .build()

    override val workManagerConfiguration: Configuration
        get() = workerFactoryConfiguration
}
