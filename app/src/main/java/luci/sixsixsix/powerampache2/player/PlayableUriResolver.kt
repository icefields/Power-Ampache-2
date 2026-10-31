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

package luci.sixsixsix.powerampache2.player

import luci.sixsixsix.powerampache2.domain.PodcastRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.presentation.models.PlayableUI
import luci.sixsixsix.powerampache2.presentation.models.PodcastEpisodeUI
import luci.sixsixsix.powerampache2.presentation.models.SongUI
import luci.sixsixsix.powerampache2.presentation.models.toSong
import javax.inject.Inject

/** each item type goes to its own repository, so the server always gets the right type and id */
class PlayableUriResolver @Inject constructor(
    private val songsRepository: SongsRepository,
    private val podcastRepository: PodcastRepository,
) {
    suspend operator fun invoke(item: PlayableUI): String = when (item) {
        is SongUI -> songsRepository.getSongUri(item.toSong())
        is PodcastEpisodeUI -> podcastRepository.getEpisodeUri(item.episode)
    }
}
