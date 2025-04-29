/**
 * Copyright (C) 2025  Antonio Tari
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
package luci.sixsixsix.powerampache2.domain.models.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val BITRATE_VERY_HIGH = 320
private const val BITRATE_HIGH = 256
private const val BITRATE_MEDIUM = 192
private const val BITRATE_MEDIUM_LOW = 128
private const val BITRATE_LOW = 96

val defaultBitrate =
    StreamingQuality.getStreamingQualityFromBitrate(LocalSettings.SETTINGS_DEFAULTS_STREAMING_QUALITY)

@Parcelize
sealed class StreamingQuality(val bitrate: Int): Parcelable {
    @Parcelize
    data object VERY_HIGH: StreamingQuality(bitrate = BITRATE_VERY_HIGH)
    @Parcelize
    data object HIGH: StreamingQuality(bitrate = BITRATE_HIGH)
    @Parcelize
    data object MEDIUM: StreamingQuality(bitrate = BITRATE_MEDIUM)
    @Parcelize
    data object MEDIUM_LOW: StreamingQuality(bitrate = BITRATE_MEDIUM_LOW)
    @Parcelize
    data object LOW: StreamingQuality(bitrate = BITRATE_LOW)
    override fun hashCode() = bitrate.hashCode()
    override fun toString() = "$bitrate"

    companion object {
        fun getStreamingQualityFromBitrate(bitrate: Int): StreamingQuality =
            when(bitrate) {
                BITRATE_VERY_HIGH -> VERY_HIGH
                BITRATE_HIGH -> HIGH
                BITRATE_MEDIUM -> MEDIUM
                BITRATE_MEDIUM_LOW -> MEDIUM_LOW
                BITRATE_LOW -> LOW
                else -> VERY_HIGH
            }
    }
}
