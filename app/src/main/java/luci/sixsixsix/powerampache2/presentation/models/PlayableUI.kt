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

package luci.sixsixsix.powerampache2.presentation.models

import android.os.Parcelable
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.models.MediaKey

/**
 * Anything that can sit in the queue and be played. Compare items with [key], never with
 * data-class equality: the same song can have different field values over time, and a song and
 * an episode can share a server id.
 */
sealed interface PlayableUI : Parcelable {
    val key: MediaKey
    val title: String
    val subtitle: String
    val imageUrl: String
    val durationSec: Int
    val isDownloaded: Boolean
}

fun PlayableUI.totalTime(): String {
    val minutes = durationSec / 60
    val seconds = durationSec % 60
    return "$minutes:${if (seconds < 10) { "0" } else { "" } }${seconds}"
}

fun <T : PlayableUI> List<T>.reduceList(): List<T> = if (size > Constants.config.queueSizeLimit) {
    subList(0, Constants.config.queueSizeLimit) } else this

/** keeps the first occurrence of each key, like the LinkedHashSet the queue used before */
fun List<PlayableUI>.distinctByKey(): List<PlayableUI> = distinctBy { it.key }
