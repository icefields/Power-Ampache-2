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

object Constants {
    // NETWORK
    const val TIMEOUT_CONNECTION_S = 20L
    const val TIMEOUT_READ_S = 120L
    const val TIMEOUT_WRITE_S = 120L

    const val NETWORK_REQUEST_LIMIT_HOME = 40

    const val ERROR_INT = -1
    const val ERROR_FLOAT = -1f
    const val ERROR_STRING = "ERROR"
    const val LOADING_STRING = "LOADING"

    const val RESET_QUEUE_ON_NEW_SESSION = false
    const val CLEAR_TABLE_AFTER_FETCH = false

    const val QUICK_PLAY_MIN_SONGS = 50

    // LOCAL DB
    const val DB_LOCAL_NAME = "musicdb.db"

    // DONATION LINKS
    const val DONATION_BITCOIN_ADDRESS = "bc1qm9dvdrukgrqpg5f7466u4cy7tfvwcsc8pqshl4"
    const val DONATION_BITCOIN_URI = "bitcoin:$DONATION_BITCOIN_ADDRESS"
    const val DONATION_PAYPAL_URI = "https://paypal.me/powerampache"

    // DEBUG
    const val NETWORK_REQUEST_LIMIT_DEBUG = 20
    const val ERROR_TITLE = ERROR_STRING
}
