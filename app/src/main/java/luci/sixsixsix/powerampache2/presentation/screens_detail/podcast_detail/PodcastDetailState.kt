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
package luci.sixsixsix.powerampache2.presentation.screens_detail.podcast_detail

import luci.sixsixsix.powerampache2.domain.models.EpisodePosition
import luci.sixsixsix.powerampache2.domain.models.Podcast

data class PodcastDetailState(
    val podcast: Podcast? = null,
    val isLoading: Boolean = false,
    val isFetchingMore: Boolean = false,
)

/** fraction of the episode already played, for the progress bar of EpisodeItem */
fun episodeProgressFraction(position: EpisodePosition?, durationSec: Int): Float {
    position ?: return 0f
    val durationMs = if (position.durationMs > 0) position.durationMs else durationSec * 1000L
    if (durationMs <= 0) return 0f
    return (position.positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
}
