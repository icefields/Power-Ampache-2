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

package luci.sixsixsix.powerampache2.domain

import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.EpisodePosition
import luci.sixsixsix.powerampache2.domain.models.Podcast
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode

interface PodcastRepository {
    val downloadedEpisodesFlow: Flow<List<PodcastEpisode>>
    val episodePositionsFlow: Flow<Map<String, EpisodePosition>>

    suspend fun getPodcasts(fetchRemote: Boolean = true): Flow<Resource<List<Podcast>>>
    /** cached podcast, fetched from the server on a cache miss. Null if not found or on error */
    suspend fun getPodcast(podcastId: String): Podcast?
    suspend fun getPodcastEpisodes(
        podcastId: String,
        offset: Int = 0,
        fetchRemote: Boolean = true
    ): Flow<Resource<List<PodcastEpisode>>>

    /** local file path if downloaded, stream url otherwise */
    suspend fun getEpisodeUri(episode: PodcastEpisode): String

    suspend fun isEpisodeDownloaded(episodeId: String): Boolean
    /** starts the download worker, does nothing for a downloaded or not playable episode */
    suspend fun downloadEpisode(episode: PodcastEpisode)
    /** called by the download worker */
    suspend fun downloadEpisodeAndAddToDb(episodeId: String): PodcastEpisode?
    suspend fun deleteDownloadedEpisode(episode: PodcastEpisode)

    suspend fun getEpisodePosition(episodeId: String): EpisodePosition?
    suspend fun saveEpisodePosition(position: EpisodePosition)
    suspend fun clearEpisodePosition(episodeId: String)
}
