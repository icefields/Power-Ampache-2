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

import luci.sixsixsix.powerampache2.presentation.models.PodcastEpisodeUI
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainEvent
import luci.sixsixsix.powerampache2.presentation.screens.main.viewmodel.MainViewModel

/** the same menu actions in every list that shows episodes */
fun MainViewModel.onEpisodeItemEvent(
    episode: PodcastEpisodeUI,
    event: EpisodeItemEvent,
    onRemoveFromQueue: () -> Unit = {},
    onGoToPodcast: () -> Unit = {},
) = when (event) {
    EpisodeItemEvent.PLAY_NEXT -> onEvent(MainEvent.OnAddSongToQueueNext(episode))
    EpisodeItemEvent.ADD_TO_QUEUE -> onEvent(MainEvent.OnAddSongToQueue(episode))
    EpisodeItemEvent.DOWNLOAD -> onEvent(MainEvent.OnDownloadEpisode(episode))
    EpisodeItemEvent.DELETE_DOWNLOAD -> onEvent(MainEvent.OnDeleteDownloadedEpisode(episode))
    EpisodeItemEvent.REMOVE_FROM_QUEUE -> onRemoveFromQueue()
    EpisodeItemEvent.GO_TO_PODCAST -> onGoToPodcast()
}
