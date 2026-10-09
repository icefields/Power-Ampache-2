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
package luci.sixsixsix.powerampache2.presentation.screens.offline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.common.EmptyListView
import luci.sixsixsix.powerampache2.presentation.common.episodeitem.EpisodeItem
import luci.sixsixsix.powerampache2.presentation.common.episodeitem.onEpisodeItemEvent
import luci.sixsixsix.powerampache2.presentation.models.toPodcastEpisodeUI
import luci.sixsixsix.powerampache2.presentation.navigation.Ampache2NavGraphs
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel

@Composable
@Destination
fun DownloadedEpisodesScreen(
    navigator: DestinationsNavigator,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val downloaded by mainViewModel.podcastRepository.downloadedEpisodesFlow.collectAsState(initial = listOf())
    val currentItem by mainViewModel.currentItemStateFlow().collectAsState()
    val episodes = downloaded.map { it.toPodcastEpisodeUI(isDownloaded = true) }

    Box(modifier = modifier.fillMaxSize()) {
        if (episodes.isEmpty()) {
            EmptyListView(title = stringResource(id = R.string.offline_noData_warning))
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(episodes, key = { it.key.playerId }) { episode ->
                EpisodeItem(
                    episode = episode,
                    isCurrent = episode.key == currentItem?.key,
                    showGoToPodcast = true,
                    onClick = { mainViewModel.onEvent(MainEvent.PlaySongAddToQueueTop(episode, listOf())) },
                    onEvent = { event ->
                        mainViewModel.onEpisodeItemEvent(
                            episode = episode,
                            event = event,
                            onGoToPodcast = {
                                Ampache2NavGraphs.navigateToPodcast(podcastId = episode.episode.podcast.id)
                            }
                        )
                    }
                )
            }
        }
    }
}
