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
package luci.sixsixsix.powerampache2.presentation.screens.search

import luci.sixsixsix.powerampache2.domain.models.Genre
import luci.sixsixsix.powerampache2.domain.models.Song

sealed class SearchViewEvent {
    data object Refresh: SearchViewEvent()
    data object FetchGenres: SearchViewEvent()
    data object Clear: SearchViewEvent()
    data class OnSearchQueryChange(val query: String): SearchViewEvent()
    data class OnGenreSelected(val genre: Genre?): SearchViewEvent()
    data class OnBottomListReached(val i: Int): SearchViewEvent()
    data class OnSongSelected(val song: Song): SearchViewEvent()
}
