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
import luci.sixsixsix.powerampache2.domain.errors.MusicError

data class ErrorDto(
    @SerializedName("errorAction") // "errorAction":"handshake", "albums"
    val errorAction: String,

    @SerializedName("errorCode") // "errorCode":"4701" with handshake,"4704" with empty result
    val errorCode: String,

    @SerializedName("errorMessage")
    val errorMessage: String,

    @SerializedName("errorType") // "errorType":"account", "empty"
    val errorType: String
)

fun ErrorDto.toError() = MusicError(
    errorAction = errorAction,
    errorCode = errorCode,
    errorMessage = errorMessage,
    errorType = errorType
)
/*
"error": {
"errorCode": "4701",
"errorAction": ,
"errorType": "account",
"errorMessage": "Session Expired"
}

"errorAction":"albums","errorCode":"4704","errorMessage":"No Results","errorType":"empty"
 */