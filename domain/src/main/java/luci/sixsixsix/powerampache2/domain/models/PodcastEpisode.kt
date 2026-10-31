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

@Parcelize
data class PodcastEpisode(
    override val id: String,
    val title: String,
    val podcast: MusicAttribute,
    val description: String = "",
    val author: String = "",
    val pubDateEpochSec: Long = 0L,
    val state: String = "",
    val time: Int = 0,
    val size: Long = 0L,
    val mime: String = "",
    val filename: String = "",
    val artUrl: String = "",
    val website: String = "",
) : AmpacheModel, Parcelable {
    val key: MediaKey
        get() = MediaKey(MediaType.PODCAST_EPISODE, id)

    /** the server has a file only for completed episodes, the others have an empty url */
    val isPlayable: Boolean
        get() = state == STATE_COMPLETED

    companion object {
        const val STATE_COMPLETED = "completed"
    }
}
