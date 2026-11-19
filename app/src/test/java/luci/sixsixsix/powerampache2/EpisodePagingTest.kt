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
package luci.sixsixsix.powerampache2

import luci.sixsixsix.powerampache2.presentation.screens_detail.podcast_detail.EpisodePaging
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EpisodePagingTest {
    private val page = List(50) { it }

    @Test
    fun nextOffsetCountsTheServerEpisodesNotTheCachedOnes() {
        val paging = EpisodePaging()
        // the cache holds the first page plus one downloaded episode of a later page
        paging.onResult(offset = 0, networkData = null, cachedCount = 51)
        paging.onResult(offset = 0, networkData = page, cachedCount = 51)
        assertEquals(50, paging.nextOffset)
        paging.onResult(offset = 50, networkData = page, cachedCount = 101)
        assertEquals(100, paging.nextOffset)
        assertFalse(paging.isEndReached)
    }

    @Test
    fun anEmptyPageIsTheEnd() {
        val paging = EpisodePaging()
        paging.onResult(offset = 0, networkData = page, cachedCount = 50)
        paging.onResult(offset = 50, networkData = listOf<Any>(), cachedCount = 50)
        assertTrue(paging.isEndReached)
        assertEquals(50, paging.nextOffset)
    }

    @Test
    fun aPodcastWithoutEpisodesIsTheEnd() {
        val paging = EpisodePaging()
        paging.onResult(offset = 0, networkData = listOf<Any>(), cachedCount = 0)
        assertTrue(paging.isEndReached)
    }

    @Test
    fun offlineModeHasNoMorePages() {
        val paging = EpisodePaging()
        paging.onResult(offset = 0, networkData = null, cachedCount = 3)
        assertEquals(3, paging.nextOffset)
        assertFalse(paging.isEndReached)
        paging.onResult(offset = 3, networkData = null, cachedCount = 3)
        assertTrue(paging.isEndReached)
    }

    @Test
    fun resetStartsAgainFromTheFirstPage() {
        val paging = EpisodePaging()
        paging.onResult(offset = 0, networkData = listOf<Any>(), cachedCount = 0)
        paging.reset()
        assertEquals(0, paging.nextOffset)
        assertFalse(paging.isEndReached)
    }
}
