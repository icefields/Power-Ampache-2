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

package luci.sixsixsix.powerampache2.presentation.common.episodeitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.models.PodcastEpisodeUI
import luci.sixsixsix.powerampache2.presentation.models.totalTime
import java.text.DateFormat
import java.util.Date

@Composable
fun EpisodeItem(
    episode: PodcastEpisodeUI,
    onClick: () -> Unit,
    onEvent: (EpisodeItemEvent) -> Unit,
    modifier: Modifier = Modifier,
    progressFraction: Float = 0f,
    isCurrent: Boolean = false,
    isInQueue: Boolean = false,
    showGoToPodcast: Boolean = false,
) {
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isCurrent) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
            .clickable(enabled = episode.isPlayable, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .alpha(if (episode.isPlayable) 1f else 0.4f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = episode.imageUrl,
            contentDescription = episode.title,
            placeholder = painterResource(id = R.drawable.placeholder_album),
            error = painterResource(id = R.drawable.placeholder_album),
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = episode.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = if (episode.isPlayable) episodeInfoLine(episode)
                    else stringResource(id = R.string.podcast_episode_unavailable),
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (progressFraction > 0f) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }
        if (episode.isDownloaded) {
            Icon(
                imageVector = Icons.Default.DownloadDone,
                contentDescription = stringResource(id = R.string.podcast_episode_downloaded)
            )
        }
        if (episode.isPlayable) {
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(id = R.string.podcast_episode_more_actions)
                    )
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    EpisodeMenuItem(R.string.podcast_episode_play_next) {
                        menuOpen = false
                        onEvent(EpisodeItemEvent.PLAY_NEXT)
                    }
                    if (isInQueue) {
                        EpisodeMenuItem(R.string.podcast_episode_remove_from_queue) {
                            menuOpen = false
                            onEvent(EpisodeItemEvent.REMOVE_FROM_QUEUE)
                        }
                    } else {
                        EpisodeMenuItem(R.string.podcast_episode_add_to_queue) {
                            menuOpen = false
                            onEvent(EpisodeItemEvent.ADD_TO_QUEUE)
                        }
                    }
                    if (episode.isDownloaded) {
                        EpisodeMenuItem(R.string.podcast_episode_delete_download) {
                            menuOpen = false
                            onEvent(EpisodeItemEvent.DELETE_DOWNLOAD)
                        }
                    } else {
                        EpisodeMenuItem(R.string.podcast_episode_download) {
                            menuOpen = false
                            onEvent(EpisodeItemEvent.DOWNLOAD)
                        }
                    }
                    if (showGoToPodcast) {
                        EpisodeMenuItem(R.string.podcast_episode_go_to_podcast) {
                            menuOpen = false
                            onEvent(EpisodeItemEvent.GO_TO_PODCAST)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeMenuItem(titleRes: Int, onClick: () -> Unit) =
    DropdownMenuItem(text = { Text(stringResource(id = titleRes)) }, onClick = onClick)

private fun episodeInfoLine(episode: PodcastEpisodeUI): String {
    val pubDate = episode.episode.pubDateEpochSec
    val date = if (pubDate > 0) DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(pubDate * 1000L)) else ""
    return listOf(date, episode.totalTime()).filter { it.isNotBlank() }.joinToString(" · ")
}
