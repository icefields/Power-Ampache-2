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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.PodcastRepository
import luci.sixsixsix.powerampache2.domain.models.EpisodePosition
import luci.sixsixsix.powerampache2.domain.models.Podcast
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode
import luci.sixsixsix.powerampache2.presentation.models.PodcastEpisodeUI
import luci.sixsixsix.powerampache2.presentation.models.toPodcastEpisodeUI
import javax.inject.Inject

@HiltViewModel
class PodcastDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val podcastRepository: PodcastRepository,
) : ViewModel() {
    private val podcastId: String = savedStateHandle.get<String>("podcastId") ?: ""
    private val episodes = MutableStateFlow<List<PodcastEpisode>>(listOf())
    private val paging = EpisodePaging()
    private var loadJob: Job? = null

    var state by mutableStateOf(PodcastDetailState(podcast = savedStateHandle.get<Podcast>("podcast")))
        private set

    val episodesStateFlow: StateFlow<List<PodcastEpisodeUI>> =
        combine(episodes, podcastRepository.downloadedEpisodesFlow) { list, downloaded ->
            val downloadedIds = downloaded.map { it.id }.toSet()
            list.map { it.toPodcastEpisodeUI(isDownloaded = it.id in downloadedIds) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf())

    val positionsStateFlow: StateFlow<Map<String, EpisodePosition>> =
        podcastRepository.episodePositionsFlow
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), mapOf())

    init {
        // "go to podcast" from an episode does not pass the podcast: load it for the header
        if (state.podcast == null) loadPodcast()
        load(offset = 0)
    }

    private fun loadPodcast() = viewModelScope.launch {
        podcastRepository.getPodcast(podcastId)?.let { state = state.copy(podcast = it) }
    }

    fun refresh() {
        paging.reset()
        load(offset = 0)
    }

    fun onBottomReached() {
        if (state.isFetchingMore || state.isLoading || paging.isEndReached) return
        state = state.copy(isFetchingMore = true)
        load(offset = paging.nextOffset)
    }

    /** a new load replaces the running one, so an older page cannot overwrite the paging state */
    private fun load(offset: Int) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch { collectEpisodes(offset) }
    }

    private suspend fun collectEpisodes(offset: Int) {
        podcastRepository.getPodcastEpisodes(podcastId, offset = offset, fetchRemote = true).collect { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { episodes.value = it }
                    paging.onResult(offset, result.networkData, cachedCount = result.data?.size ?: 0)
                }
                is Resource.Error -> state = state.copy(isLoading = false, isFetchingMore = false)
                is Resource.Loading -> {
                    state = state.copy(isLoading = result.isLoading)
                    if (!result.isLoading) state = state.copy(isFetchingMore = false)
                }
            }
        }
    }
}
