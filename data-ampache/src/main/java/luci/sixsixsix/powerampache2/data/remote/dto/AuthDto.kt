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
package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.Session
import java.time.LocalDateTime

data class AuthDto(
    @SerializedName("add")
    val add: String,
    @SerializedName("albums")
    val albums: Int?,
    @SerializedName("api")
    val api: String?,
    @SerializedName("artists")
    val artists: Int?,
    @SerializedName("auth")
    val auth: String?,
    @SerializedName("catalogs")
    val catalogs: Int?,
    @SerializedName("clean")
    val clean: String?,
    @SerializedName("genres")
    val genres: Int?,
    @SerializedName("labels")
    val labels: Int?,
    @SerializedName("licenses")
    val licenses: Int?,
    @SerializedName("live_streams")
    val liveStreams: Int?,
    @SerializedName("playlists")
    val playlists: Int?,
    @SerializedName("playlists_searches")
    val playlistsSearches: Int?,
    @SerializedName("podcast_episodes")
    val podcastEpisodes: Int?,
    @SerializedName("podcasts")
    val podcasts: Int?,
    @SerializedName("searches")
    val searches: Int?,
    @SerializedName("session_expire")
    val sessionExpire: String?,
    @SerializedName("shares")
    val shares: Int?,
    @SerializedName("songs")
    val songs: Int?,
    @SerializedName("update")
    val update: String?,
    @SerializedName("users")
    val users: Int?,
    @SerializedName("videos")
    val videos: Int?,
    @SerializedName("server")
    var server: String? = null,
    @SerializedName("version")
    var version: String? = null,
    @SerializedName("compatible")
    var compatible: String? = null,
    @SerializedName("username")
    var username: String? = null,
): AmpacheBaseResponse()

fun AuthDto.toServerInfo(): ServerInfo = ServerInfo(
    server = server,
    version = version,
    compatible = compatible,
    isNextcloud = server?.lowercase()?.contains("nextcloud music") == true ||
            server?.lowercase()?.contains("owncloud music") == true
)

fun AuthDto.toSession(dateMapper: DateMapper): Session = Session(
    add = dateMapper(add),
    albums = albums ?: 0,
    api = api ?: "",
    artists = artists ?: 0,
    auth = auth ?: "",
    catalogs = catalogs ?: 0,
    clean = if (clean != null && clean != "0") dateMapper(clean) else LocalDateTime.MAX,
    genres = genres ?: 0,
    labels = labels ?: 0,
    licenses = licenses ?: 0,
    liveStreams = liveStreams ?: 0,
    playlists = playlists ?: 0,
    playlistsSearches = playlistsSearches ?: 0,
    podcastEpisodes = podcastEpisodes ?: 0,
    podcasts = podcasts ?: 0,
    searches = searches ?: 0,
    sessionExpire = if (sessionExpire != null && sessionExpire != "0") dateMapper(sessionExpire) else LocalDateTime.MIN,
    shares = shares ?: 0,
    songs = songs ?: 0,
    update = if (update != null && update != "0") dateMapper(update) else LocalDateTime.MIN,
    users = users ?: 0,
    videos = videos ?: 0,
)
