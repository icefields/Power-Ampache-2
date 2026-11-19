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

/**
 * Paging state of the episode list of one podcast. The next offset counts the episodes that the
 * server sent, not the cached ones: a download or a single-episode lookup can add one episode of
 * a later page to the cache, and an offset from the cache size would then skip an episode.
 */
internal class EpisodePaging {
    var nextOffset = 0
        private set
    var isEndReached = false
        private set

    fun reset() {
        nextOffset = 0
        isEndReached = false
    }

    /**
     * A result for the page at [offset]. [networkData] is null for a cached or an offline result,
     * and [cachedCount] is the number of episodes that result shows.
     */
    fun onResult(offset: Int, networkData: List<*>?, cachedCount: Int) {
        if (networkData != null) {
            nextOffset = offset + networkData.size
            if (networkData.isEmpty()) isEndReached = true
        } else if (offset > 0) {
            // offline mode shows only the downloads and has no more pages
            isEndReached = true
        } else {
            // the cache of the first page, or the offline list: a network result replaces it
            nextOffset = cachedCount
        }
    }
}
