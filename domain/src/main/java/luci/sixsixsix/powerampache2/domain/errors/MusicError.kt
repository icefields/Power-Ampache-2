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

import com.google.gson.Gson

data class MusicError(
    val errorAction: String,
    val errorCode: String,
    val errorMessage: String,
    val errorType: String
) {
    fun toJson(): String = Gson().toJson(this)

    override fun toString(): String = try {
        toJson()
    } catch (e: Exception) {
        super.toString()
    }

    fun getErrorType(): ErrorType {
        ErrorType.entries.forEach {
            if (it.code == errorCode)
                return it
        }
        return ErrorType.Other
    }

    fun isSessionExpiredError(): Boolean = errorCode == ERROR_CODE_SESSION_EXPIRED
    fun isEmptyResult(): Boolean = errorCode == ERROR_CODE_EMPTY
    fun isDuplicateResult(): Boolean = errorCode == ERROR_CODE_DUPLICATE
    fun isServerUrlNotInitialized(): Boolean = errorCode == ERROR_CODE_SERVER_URL_NOT_INITIALIZED
    fun isNotImplemented(): Boolean = errorCode == ERROR_CODE_NOT_IMPLEMENTED
}

enum class ErrorType(val code: String) {
    ACCOUNT(ERROR_CODE_SESSION_EXPIRED),
    EMPTY(ERROR_CODE_EMPTY),
    DUPLICATE(ERROR_CODE_DUPLICATE),
    SYSTEM(ERROR_CODE_SYSTEM),
    Other("_")
}

private const val ERROR_CODE_SESSION_EXPIRED = "4701"
private const val ERROR_CODE_EMPTY = "4704"
private const val ERROR_CODE_DUPLICATE = "4710"
private const val ERROR_CODE_SYSTEM = "4703"
private const val ERROR_CODE_NOT_IMPLEMENTED = "4705"
private const val ERROR_PLAYLIST_DELETE = "4742" // {"errorAction":"playlist_delete","errorCode":"4742","errorMessage":"Require: 100","errorType":"account"}

const val ERROR_TYPE_EMPTY = "empty"
const val ERROR_TYPE_ACCOUNT = "account"
const val ERROR_TYPE_DUPLICATE = "duplicate"
const val ERROR_TYPE_SYSTEM = "system"
