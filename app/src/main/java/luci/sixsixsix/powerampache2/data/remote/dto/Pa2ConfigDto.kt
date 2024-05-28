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
package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.common.DOGMAZIC_USER
import luci.sixsixsix.powerampache2.common.PLAYLIST_ADD_NEW_ENABLE
import luci.sixsixsix.powerampache2.common.PLAYLIST_FETCH_LIMIT

import luci.sixsixsix.powerampache2.common.Pa2Config
import luci.sixsixsix.powerampache2.common.RESET_QUEUE_ON_NEW_SESSION


data class Pa2ConfigDto(
    @SerializedName("playlistAddNew_enable")
    val playlistAddNewEnable: Boolean? = null,

    @SerializedName("queueResetOnNewSession")
    val queueResetOnNewSession: Boolean? = null,

    @SerializedName("dogmazicDemoUser")
    val dogmazicDemoUser: String? = null,

    @SerializedName("playlistSongsFetchLimit")
    val playlistSongsFetchLimit: Int? = null,

    @SerializedName("forceLoginDialogsOnAllVersions")
    val forceLoginDialogsOnAllVersions: Boolean? = null,

    @SerializedName("loginWarning")
    val loginWarning: String? = null,

)

fun Pa2ConfigDto.toPa2Config() = Pa2Config(
    playlistAddNewEnable = playlistAddNewEnable ?: PLAYLIST_ADD_NEW_ENABLE,
    queueResetOnNewSession = queueResetOnNewSession ?: RESET_QUEUE_ON_NEW_SESSION,
    dogmazicDemoUser = dogmazicDemoUser ?: DOGMAZIC_USER,
    playlistSongsFetchLimit = playlistSongsFetchLimit ?: PLAYLIST_FETCH_LIMIT,
    forceLoginDialogsOnAllVersions = forceLoginDialogsOnAllVersions ?: BuildConfig.FORCE_LOGIN_DIALOG_ON_ALL_VERSIONS,
    loginWarning = loginWarning ?: ""
)
