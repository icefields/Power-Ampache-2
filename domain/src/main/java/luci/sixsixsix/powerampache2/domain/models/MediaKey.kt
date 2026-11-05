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

package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class MediaType { SONG, PODCAST_EPISODE }

/**
 * Identifies a playable item. The Ampache server keeps songs and podcast episodes in separate
 * tables with separate auto-increment ids, so the id alone is not unique: always carry the type.
 */
@Parcelize
data class MediaKey(val type: MediaType, val id: String) : Parcelable {
    /** id used for Media3 MediaItem.mediaId. Songs keep the plain id, episodes get a prefix */
    val playerId: String
        get() = when (type) {
            MediaType.SONG -> id
            MediaType.PODCAST_EPISODE -> "$EPISODE_PREFIX$id"
        }

    companion object {
        const val EPISODE_PREFIX = "pe-"

        fun fromPlayerId(playerId: String): MediaKey =
            if (playerId.startsWith(EPISODE_PREFIX)) {
                MediaKey(MediaType.PODCAST_EPISODE, playerId.removePrefix(EPISODE_PREFIX))
            } else {
                MediaKey(MediaType.SONG, playerId)
            }
    }
}
