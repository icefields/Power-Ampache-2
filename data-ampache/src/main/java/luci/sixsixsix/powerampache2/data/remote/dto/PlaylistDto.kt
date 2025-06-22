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

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import luci.sixsixsix.powerampache2.domain.common.processArtUrl
import luci.sixsixsix.powerampache2.domain.common.processFlag
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.PlaylistSongItem
import luci.sixsixsix.powerampache2.domain.models.PlaylistType

data class PlaylistDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("owner")
    val owner: String? = null,
    @SerializedName("items")
    val items: JsonElement? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("art")
    val art: String? = null,
    @SerializedName("flag")
    val flag: Any? = null, // TODO this can be boolean or integer from the server, find a solution!
    @SerializedName("preciserating")
    val preciserating: Float = 0.0f,
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("averagerating")
    val averagerating: Float = 0.0f,
    @SerializedName("has_art")
    val hasArt: Any? = null
) : AmpacheBaseResponse()

data class PlaylistSongItemDto (
    @SerializedName("id")
    val id: String,
    @SerializedName("playlisttrack")
    val playlistTrack: Int? = null
)

fun PlaylistSongItemDto.toPlaylistSongItem() = PlaylistSongItem(
    songId = id,
    playlistTrack = playlistTrack ?: 0
)

data class PlaylistsResponse(
    @SerializedName("total_count") val totalCount: Int? = 0,
    @SerializedName("playlist") val playlist: List<PlaylistDto>?,
) : AmpacheBaseResponse()

fun PlaylistDto.toPlaylist() = Playlist(
    id = id,
    name = name ?: "ERROR no name",
    owner = owner ?: "ERROR no owner",
    artUrl = processArtUrl(hasArt, art),
    items = parsePlaylistItemCount(items),
    songRefs = parsePlaylistItems(items).map { it.toPlaylistSongItem() },
    type = fromStringToPlaylistType(type),
    flag = processFlag(flag),
    preciseRating = preciserating,
    rating = rating,
    averageRating = averagerating
)

private fun parsePlaylistItemCount(items: JsonElement?): Int = items?.let { item ->
    if (item.isJsonPrimitive && item.asJsonPrimitive.isNumber) {
        item.asInt
    } else parsePlaylistItems(items).size
} ?: 0

private fun parsePlaylistItems(items: JsonElement?): List<PlaylistSongItemDto> = items?.let { item ->
    when {
        item.isJsonArray ->
            Gson().fromJson(item, object : TypeToken<List<PlaylistSongItemDto>>() { }.type)
        item.isJsonPrimitive && item.asJsonPrimitive.isNumber ->
            listOf()
        else ->
            listOf()
    }
} ?: listOf()

fun fromStringToPlaylistType(type: String?): PlaylistType? = try {
    PlaylistType.valueOf(type ?: "")
} catch (e: IllegalArgumentException ) {
    null
}
