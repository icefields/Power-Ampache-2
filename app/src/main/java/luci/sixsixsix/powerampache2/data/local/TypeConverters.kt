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
package luci.sixsixsix.powerampache2.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.settings.SortMode
import luci.sixsixsix.powerampache2.domain.models.settings.StreamingQuality
import java.time.LocalDateTime

object Converters {
    @TypeConverter
    fun stringToMusicAttribute(artist: String?): MusicAttribute? =
        try {
            Gson().fromJson(artist, MusicAttribute::class.java)
        } catch (e: Exception) {
            MusicAttribute.emptyInstance()
        }

    @TypeConverter
    fun stringToMusicAttributeList(artists: String?): List<MusicAttribute>? =
        try {
            Gson().fromJson(artists, MusicAttributesContainer::class.java).attr
        } catch (e: Exception) {
            listOf()
        }

    @TypeConverter
    fun musicAttributeListToString(artists: List<MusicAttribute>?): String? =
        try {
            Gson().toJson(MusicAttributesContainer(artists!!))
        } catch (e: Exception) {
            "{}"
        }

    @TypeConverter
    fun musicAttributeToString(artist: MusicAttribute): String? =
        try {
            Gson().toJson(artist)
        } catch (e: Exception) {
            "{}"
        }

    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun localDateTimeToString(date: LocalDateTime?): String? = date?.toString()

    @TypeConverter
    fun bitrateToStreamingQuality(bitrate: Int) = StreamingQuality.getStreamingQualityFromBitrate(bitrate)

    @TypeConverter
    fun streamingQualityToBitrate(streamingQuality: StreamingQuality) = streamingQuality.bitrate

    @TypeConverter
    fun stringToSortMode(mode: String) = SortMode.valueOf(mode)

    @TypeConverter
    fun sortModeToString(sortMode: SortMode) = sortMode.name
}

data class MusicAttributesContainer(
    val attr: List<MusicAttribute>?
)
