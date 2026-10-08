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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.Podcast
import luci.sixsixsix.powerampache2.presentation.common.EmptyListView
import luci.sixsixsix.powerampache2.presentation.common.LoadingScreen
import luci.sixsixsix.powerampache2.presentation.common.episodeitem.EpisodeItem
import luci.sixsixsix.powerampache2.presentation.common.episodeitem.onEpisodeItemEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel

@Composable
@Destination
fun PodcastDetailScreen(
    navigator: DestinationsNavigator,
    podcastId: String,
    podcast: Podcast? = null,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    viewModel: PodcastDetailViewModel = hiltViewModel(),
) {
    val episodes by viewModel.episodesStateFlow.collectAsState()
    val positions by viewModel.positionsStateFlow.collectAsState()
    val currentItem by mainViewModel.currentItemStateFlow().collectAsState()
    val state = viewModel.state
    val listState = rememberLazyListState()

    val isAtBottom by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= listState.layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(isAtBottom, episodes.size) {
        if (isAtBottom && episodes.isNotEmpty()) viewModel.onBottomReached()
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            item { PodcastHeader(podcast = state.podcast ?: podcast) }
            items(episodes, key = { it.key.playerId }) { episode ->
                EpisodeItem(
                    episode = episode,
                    progressFraction = episodeProgressFraction(positions[episode.episode.id], episode.durationSec),
                    isCurrent = episode.key == currentItem?.key,
                    // play this episode now, keep the rest of the queue
                    onClick = { mainViewModel.onEvent(MainEvent.PlaySongAddToQueueTop(episode, listOf())) },
                    onEvent = { event -> mainViewModel.onEpisodeItemEvent(episode, event) }
                )
            }
        }
        if (episodes.isEmpty()) {
            if (state.isLoading) LoadingScreen()
            else EmptyListView(title = stringResource(id = R.string.podcast_episodes_empty))
        }
    }
}

@Composable
private fun PodcastHeader(podcast: Podcast?) {
    podcast ?: return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = podcast.artUrl,
            contentDescription = podcast.name,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.placeholder_album),
            modifier = Modifier.size(160.dp)
        )
        Text(
            text = podcast.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 12.dp)
        )
        if (podcast.website.isNotBlank()) {
            Text(text = podcast.website, style = MaterialTheme.typography.bodySmall)
        }
        if (podcast.description.isNotBlank()) {
            Text(
                text = podcastDescriptionText(podcast.description),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/** the server sends HTML with escaped entities, ie. &lt;p&gt;: decode the entities, then drop the tags */
private fun podcastDescriptionText(description: String): String =
    HtmlCompat.fromHtml(
        HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
        HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toString().trim()
