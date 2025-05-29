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
package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.data.local.multiuserDbKey
import luci.sixsixsix.powerampache2.domain.models.Session
import java.time.LocalDateTime

@Entity
data class MultiUserSessionEntity(
    val add: LocalDateTime,
    val albums: Int,
    val api: String,
    val artists: Int,
    val auth: String,
    val catalogs: Int,
    val clean: LocalDateTime,
    val genres: Int,
    val labels: Int,
    val licenses: Int,
    val liveStreams: Int,
    val playlists: Int,
    val playlistsSearches: Int,
    val podcastEpisodes: Int,
    val podcasts: Int,
    val searches: Int,
    val sessionExpire: LocalDateTime,
    val shares: Int,
    val songs: Int,
    val update: LocalDateTime,
    val users: Int,
    val videos: Int,
    val username: String,
    val serverUrl: String,
    @PrimaryKey val primaryKey: String = multiuserDbKey(username, serverUrl)
)

fun MultiUserSessionEntity.toSession(): Session = Session(
    add = add,
    albums = albums ?: 0,
    api = api ?: "",
    artists = artists ?: 0,
    auth = auth ?: "",
    catalogs = catalogs,
    clean = clean,
    genres = genres,
    labels = labels,
    licenses = licenses,
    liveStreams = liveStreams,
    playlists = playlists,
    playlistsSearches = playlistsSearches,
    podcastEpisodes = podcastEpisodes,
    podcasts = podcasts,
    searches = searches,
    sessionExpire = sessionExpire,
    shares = shares,
    songs = songs,
    update = update,
    users = users,
    videos = videos
)

fun Session.toMultiUserSessionEntity(serverUrl: String, username: String) = MultiUserSessionEntity(
    add = add,
    albums = albums,
    api = api,
    artists = artists,
    auth = auth,
    catalogs = catalogs,
    clean = clean,
    genres = genres,
    labels = labels,
    licenses = licenses,
    liveStreams = liveStreams,
    playlists = playlists,
    playlistsSearches = playlistsSearches,
    podcastEpisodes = podcastEpisodes,
    podcasts = podcasts,
    searches = searches,
    sessionExpire = sessionExpire,
    shares = shares,
    songs = songs,
    update = update,
    users = users,
    videos = videos,
    serverUrl = serverUrl,
    username = username
)
