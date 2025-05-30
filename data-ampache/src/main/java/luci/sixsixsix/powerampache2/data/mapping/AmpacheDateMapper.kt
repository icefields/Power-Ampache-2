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
package luci.sixsixsix.powerampache2.data.mapping

import androidx.room.TypeConverter
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AmpacheDateMapper: DateMapper {
    /**
     * The Ampache handshake method returns dates in ISO 8601
     * 2022-02-25T19:05:00+00:00
     * YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
     * val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssO")//("YYYY-MM-DD'T'hh:mm:ss'T'ZD")
     */
    /**
     * The Ampache handshake method returns dates in ISO 8601
     * 2022-02-25T19:05:00+00:00
     * YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
     * val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssO")//("YYYY-MM-DD'T'hh:mm:ss'T'ZD")
     */
    override fun isoStringToLocalDateTime(timestamp: String): LocalDateTime =
        LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_ZONED_DATE_TIME)


    // ----- DATABASE CONVERTERS

    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}
