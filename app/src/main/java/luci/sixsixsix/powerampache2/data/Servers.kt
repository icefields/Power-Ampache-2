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

import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.BuildConfig.DEBUG_LOCAL_DEVELOPMENT_URL
import luci.sixsixsix.powerampache2.BuildConfig.DEBUG_LOCAL_STABLE_URL
import luci.sixsixsix.powerampache2.BuildConfig.LOCAL_NEXTCLOUD_PASSWORD
import luci.sixsixsix.powerampache2.BuildConfig.LOCAL_NEXTCLOUD_URL
import luci.sixsixsix.powerampache2.BuildConfig.LOCAL_NEXTCLOUD_USER
import luci.sixsixsix.powerampache2.domain.common.Constants

const val DEBUG_USER = BuildConfig.AMPACHE_USER
const val DEBUG_PASSWORD = BuildConfig.AMPACHE_PASSWORD

const val DEBUG_DEV_USER = BuildConfig.LOCAL_DEV_USER
const val DEBUG_DEV_PASSWORD = BuildConfig.LOCAL_DEV_PASSWORD

// right now this is the exact same user, just accessed from local url
const val DEBUG_REMOTE_USER = DEBUG_USER
const val DEBUG_REMOTE_PASSWORD = DEBUG_PASSWORD

const val DOGMAZIC_PASSWORD = BuildConfig.DOGMAZIC_PASSWORD

const val DEBUG_REMOTE_DEMO_URL = BuildConfig.AMPACHE_URL
const val AMPACHE_DEMO_URL = "demo.ampache.dev"
const val AMPACHE_DEMO_APIKEY = "demodemo"

sealed class Servers(
    val url: String,
    val user: String = "",
    val password: String = "",
    val apiKey: String = ""
) {
    /**
     * local-ip server for testing the latest development branch
     */
    data object LocalDev: Servers(
        url = DEBUG_LOCAL_DEVELOPMENT_URL,
        user = DEBUG_DEV_USER,
        password = DEBUG_DEV_PASSWORD
    )

    /**
     * remote ip server for testing 6.2
     */
    data object RemoteDebug: Servers(
        url = DEBUG_REMOTE_DEMO_URL,
        user = DEBUG_REMOTE_USER,
        password = DEBUG_PASSWORD
    )

    /**
     * official ampache demo server
     */
    data object AmpacheDemo: Servers(
        url = AMPACHE_DEMO_URL,
        apiKey = AMPACHE_DEMO_APIKEY
    )

    /**
     * dogmazic server
     */
    data object Dogmazic: Servers(
        url = Constants.config.dogmazicDemoUrl,
        user = Constants.config.dogmazicDemoUser,
        password = DOGMAZIC_PASSWORD,
        apiKey = Constants.config.dogmazicDemoToken
    )

    /**
     * dogmazic server
     */
    data object NextcloudLocal: Servers(
        url = LOCAL_NEXTCLOUD_URL,
        user = LOCAL_NEXTCLOUD_USER,
        password = LOCAL_NEXTCLOUD_PASSWORD
    )

    /**
     * local server on stable branch
     */
    data object LocalStable: Servers(
        url = DEBUG_LOCAL_STABLE_URL,
        user = DEBUG_USER,
        password = DEBUG_REMOTE_PASSWORD
    )
}
