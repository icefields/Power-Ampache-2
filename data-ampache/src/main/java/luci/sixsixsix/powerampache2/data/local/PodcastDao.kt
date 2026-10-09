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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.data.local.entities.DownloadedPodcastEpisodeEntity
import luci.sixsixsix.powerampache2.data.local.entities.EpisodePositionEntity
import luci.sixsixsix.powerampache2.data.local.entities.PodcastEntity
import luci.sixsixsix.powerampache2.data.local.entities.PodcastEpisodeEntity

@Dao
interface PodcastDao {
// --- PODCASTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPodcasts(podcasts: List<PodcastEntity>)

    @Query("DELETE FROM podcastentity WHERE $multiUserCondition")
    suspend fun clearPodcasts()

    @Transaction
    suspend fun replacePodcasts(podcasts: List<PodcastEntity>) {
        clearPodcasts()
        insertPodcasts(podcasts)
    }

    @Query("SELECT * FROM podcastentity WHERE $multiUserCondition ORDER BY name COLLATE NOCASE")
    suspend fun getPodcasts(): List<PodcastEntity>

    @Query("SELECT * FROM podcastentity WHERE id == :podcastId AND $multiUserCondition")
    suspend fun getPodcast(podcastId: String): PodcastEntity?

    @Query("""SELECT * FROM podcastentity WHERE $multiUserCondition AND id IN
        (SELECT podcastId FROM downloadedpodcastepisodeentity WHERE $multiUserCondition)
        ORDER BY name COLLATE NOCASE""")
    suspend fun getPodcastsWithDownloads(): List<PodcastEntity>

// --- EPISODES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<PodcastEpisodeEntity>)

    @Query("DELETE FROM podcastepisodeentity WHERE podcastId == :podcastId AND $multiUserCondition")
    suspend fun clearEpisodes(podcastId: String)

    /** the first page replaces the cache, so episodes deleted on the server disappear */
    @Transaction
    suspend fun replaceEpisodes(podcastId: String, episodes: List<PodcastEpisodeEntity>) {
        clearEpisodes(podcastId)
        insertEpisodes(episodes)
    }

    @Query("SELECT * FROM podcastepisodeentity WHERE podcastId == :podcastId AND $multiUserCondition ORDER BY pubDateEpochSec DESC")
    suspend fun getEpisodes(podcastId: String): List<PodcastEpisodeEntity>

    @Query("SELECT * FROM podcastepisodeentity WHERE id == :episodeId AND $multiUserCondition")
    suspend fun getEpisode(episodeId: String): PodcastEpisodeEntity?

// --- DOWNLOADED EPISODES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDownloadedEpisode(episode: DownloadedPodcastEpisodeEntity)

    @Query("SELECT * FROM downloadedpodcastepisodeentity WHERE id == :episodeId AND $multiUserCondition")
    suspend fun getDownloadedEpisode(episodeId: String): DownloadedPodcastEpisodeEntity?

    @Query("SELECT * FROM downloadedpodcastepisodeentity WHERE podcastId == :podcastId AND $multiUserCondition ORDER BY pubDateEpochSec DESC")
    suspend fun getDownloadedEpisodes(podcastId: String): List<DownloadedPodcastEpisodeEntity>

    @Query("SELECT * FROM downloadedpodcastepisodeentity WHERE $multiUserCondition ORDER BY pubDateEpochSec DESC")
    fun downloadedEpisodesFlow(): Flow<List<DownloadedPodcastEpisodeEntity>>

    @Query("DELETE FROM downloadedpodcastepisodeentity WHERE id == :episodeId AND $multiUserCondition")
    suspend fun deleteDownloadedEpisode(episodeId: String)

    /** no user filter on purpose: "delete all downloads" removes the whole offline_music folder for every user */
    @Query("DELETE FROM downloadedpodcastepisodeentity")
    suspend fun deleteAllDownloadedEpisodes()

// --- RESUME POSITIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEpisodePosition(position: EpisodePositionEntity)

    @Query("SELECT * FROM episodepositionentity WHERE episodeId == :episodeId AND $multiUserCondition")
    suspend fun getEpisodePosition(episodeId: String): EpisodePositionEntity?

    @Query("DELETE FROM episodepositionentity WHERE episodeId == :episodeId AND $multiUserCondition")
    suspend fun clearEpisodePosition(episodeId: String)

    @Query("SELECT * FROM episodepositionentity WHERE $multiUserCondition")
    fun episodePositionsFlow(): Flow<List<EpisodePositionEntity>>
}
