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
package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toDownloadedPodcastEpisodeEntity
import luci.sixsixsix.powerampache2.data.local.entities.toEpisodePosition
import luci.sixsixsix.powerampache2.data.local.entities.toEpisodePositionEntity
import luci.sixsixsix.powerampache2.data.local.entities.toPodcast
import luci.sixsixsix.powerampache2.data.local.entities.toPodcastEntity
import luci.sixsixsix.powerampache2.data.local.entities.toPodcastEpisode
import luci.sixsixsix.powerampache2.data.local.entities.toPodcastEpisodeEntity
import luci.sixsixsix.powerampache2.data.local.models.SongUrl
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toEpisodeList
import luci.sixsixsix.powerampache2.data.remote.dto.toPodcastEpisode
import luci.sixsixsix.powerampache2.data.remote.dto.toPodcastList
import luci.sixsixsix.powerampache2.domain.PodcastRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.models.EpisodePosition
import luci.sixsixsix.powerampache2.domain.models.Podcast
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import luci.sixsixsix.powerampache2.domain.utils.WorkerHelper
import java.net.HttpURLConnection.HTTP_OK
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Podcasts and episodes managed by the Ampache server. The app does not subscribe to feeds.
 * Same single-source-of-truth approach as AlbumsRepositoryImpl.
 */
@Singleton
class PodcastRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    db: MusicDatabase,
    private val errorHandler: ErrorHandler,
    private val storageManager: StorageManager,
    private val workerHelper: WorkerHelper,
) : BaseAmpacheRepository(api, db, errorHandler), PodcastRepository {
    private val podcastDao = db.podcastDao

    override val downloadedEpisodesFlow: Flow<List<PodcastEpisode>> =
        podcastDao.downloadedEpisodesFlow().map { list -> list.map { it.toPodcastEpisode() } }

    override val episodePositionsFlow: Flow<Map<String, EpisodePosition>> =
        podcastDao.episodePositionsFlow().map { list ->
            list.associate { it.episodeId to it.toEpisodePosition() }
        }

    override suspend fun getPodcasts(fetchRemote: Boolean): Flow<Resource<List<Podcast>>> = flow {
        emit(Resource.Loading(true))
        if (isOfflineModeEnabled()) {
            emit(Resource.Success(data = podcastDao.getPodcastsWithDownloads().map { it.toPodcast() }))
            emit(Resource.Loading(false))
            return@flow
        }

        val cached = podcastDao.getPodcasts().map { it.toPodcast() }
        if (!checkEmitCacheData(cached, fetchRemote, this)) return@flow

        val podcasts = api.getPodcasts(authToken()).toPodcastList()
        podcastDao.replacePodcasts(podcasts.map { it.toPodcastEntity(getCurrentCredentials().multiUserId) })
        emit(Resource.Success(data = podcasts, networkData = podcasts))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getPodcasts()", e, this) }

    override suspend fun getPodcast(podcastId: String): Podcast? = try {
        podcastDao.getPodcast(podcastId)?.toPodcast()
            ?: if (isOfflineModeEnabled()) {
                null
            } else {
                val podcasts = api.getPodcasts(authToken()).toPodcastList()
                podcastDao.replacePodcasts(podcasts.map { it.toPodcastEntity(getCurrentCredentials().multiUserId) })
                podcasts.firstOrNull { it.id == podcastId }
            }
    } catch (e: Exception) {
        errorHandler.logError(e)
        null
    }

    override suspend fun getPodcastEpisodes(
        podcastId: String,
        offset: Int,
        fetchRemote: Boolean
    ): Flow<Resource<List<PodcastEpisode>>> = flow {
        emit(Resource.Loading(true))
        if (isOfflineModeEnabled()) {
            emit(Resource.Success(data = podcastDao.getDownloadedEpisodes(podcastId).map { it.toPodcastEpisode() }))
            emit(Resource.Loading(false))
            return@flow
        }

        if (offset == 0) {
            val cached = podcastDao.getEpisodes(podcastId).map { it.toPodcastEpisode() }
            if (!checkEmitCacheData(cached, fetchRemote, this)) return@flow
        }

        val episodes = api.getPodcastEpisodes(authToken(), podcastId = podcastId, offset = offset).toEpisodeList()
        val entities = episodes.map { it.toPodcastEpisodeEntity(getCurrentCredentials().multiUserId) }
        if (offset == 0) {
            podcastDao.replaceEpisodes(podcastId, entities)
        } else {
            podcastDao.insertEpisodes(entities)
        }
        // networkData empty means the end of the list was reached
        emit(Resource.Success(
            data = podcastDao.getEpisodes(podcastId).map { it.toPodcastEpisode() },
            networkData = episodes
        ))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getPodcastEpisodes()", e, this) }

    override suspend fun getEpisodeUri(episode: PodcastEpisode): String =
        podcastDao.getDownloadedEpisode(episode.id)?.localPath
            ?: dao.getSongUrlData().first()?.getEpisodeUrl(episode.id)
            ?: SongUrl(authToken = authToken(), serverUrl = getCredentials()!!.serverUrl)
                .getEpisodeUrl(episode.id)

    override suspend fun isEpisodeDownloaded(episodeId: String): Boolean =
        podcastDao.getDownloadedEpisode(episodeId) != null

    override suspend fun downloadEpisode(episode: PodcastEpisode) {
        if (!episode.isPlayable || isEpisodeDownloaded(episode.id)) return
        // the worker reads the episode from the cache, which a page 0 refresh can clear
        podcastDao.insertEpisodes(listOf(episode.toPodcastEpisodeEntity(getCurrentCredentials().multiUserId)))
        workerHelper.startEpisodeDownloadWorker(
            authToken = authToken(),
            username = getCurrentCredentials().username,
            episodeId = episode.id
        ).also { requestId -> L("Episode download worker requestId: $requestId") }
    }

    @Throws(Exception::class)
    override suspend fun downloadEpisodeAndAddToDb(episodeId: String): PodcastEpisode? {
        val episode = podcastDao.getEpisode(episodeId)?.toPodcastEpisode()
            ?: fetchEpisodeAndCache(episodeId)
            ?: return null
        if (!episode.isPlayable) return null
        if (isEpisodeDownloaded(episodeId)) return episode

        val response = api.downloadSong(
            authKey = authToken(),
            songId = episodeId,
            type = MainNetwork.Type.podcast_episode
        )
        if (response.code() != HTTP_OK) {
            throw Exception("Cannot download episode $episodeId, received code ${response.code()}")
        }
        val inputStream = response.body()?.byteStream()
            ?: throw Exception("downloadEpisode, byteStream null")
        val filePath = storageManager.saveEpisode(episode, inputStream)

        val imagePath = try {
            val artResponse = api.getArt(
                authKey = authToken(),
                songId = episode.podcast.id,
                type = MainNetwork.Type.podcast
            )
            artResponse.body()?.byteStream()?.let { storageManager.saveEpisodeImage(episode, it) }
                ?: episode.artUrl
        } catch (e: Exception) {
            // in case of error downloading, fallback to the remote art url
            episode.artUrl
        }

        podcastDao.addDownloadedEpisode(
            episode.toDownloadedPodcastEpisodeEntity(
                multiUserId = getCurrentCredentials().multiUserId,
                localPath = filePath,
                localImagePath = imagePath
            )
        )
        return episode
    }

    /** cache miss in the download worker: get the episode from the server. Null if not found or on error */
    private suspend fun fetchEpisodeAndCache(episodeId: String): PodcastEpisode? = try {
        api.getPodcastEpisode(authToken(), episodeId = episodeId)
            // an error response parses to a dto with a blank (or, through gson, null) id
            .takeUnless { it.id.isNullOrBlank() }
            ?.toPodcastEpisode()
            ?.also { podcastDao.insertEpisodes(listOf(it.toPodcastEpisodeEntity(getCurrentCredentials().multiUserId))) }
    } catch (e: Exception) {
        errorHandler.logError(e)
        null
    }

    override suspend fun deleteDownloadedEpisode(episode: PodcastEpisode) {
        val downloaded = podcastDao.getDownloadedEpisode(episode.id) ?: return
        podcastDao.deleteDownloadedEpisode(episode.id)
        try {
            storageManager.deleteEpisodeFile(downloaded.localPath)
        } catch (e: Exception) {
            errorHandler.logError(e)
        }
    }

    override suspend fun getEpisodePosition(episodeId: String): EpisodePosition? =
        podcastDao.getEpisodePosition(episodeId)?.toEpisodePosition()

    override suspend fun saveEpisodePosition(position: EpisodePosition) =
        podcastDao.saveEpisodePosition(position.toEpisodePositionEntity(getCurrentCredentials().multiUserId))

    override suspend fun clearEpisodePosition(episodeId: String) =
        podcastDao.clearEpisodePosition(episodeId)
}
