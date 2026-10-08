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
import luci.sixsixsix.powerampache2.domain.common.processArtUrl
import luci.sixsixsix.powerampache2.domain.common.processNumberToInt
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode
import java.time.OffsetDateTime

data class PodcastEpisodeDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("podcast")
    val podcast: MusicAttributeDto?,
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("author")
    val author: String? = "",
    @SerializedName("website")
    val website: String? = "",
    @SerializedName("pubdate")
    val pubdate: String? = "",
    @SerializedName("state")
    val state: String? = "",
    @SerializedName("time")
    val time: Any? = 0,
    @SerializedName("size")
    val size: Any? = 0,
    @SerializedName("mime")
    val mime: String? = "",
    @SerializedName("filename")
    val filename: String? = "",
    @SerializedName("art")
    val art: String? = "",
    @SerializedName("has_art")
    val hasArt: Any? = null,
)

data class PodcastEpisodesResponse(
    @SerializedName("podcast_episode") val episodes: List<PodcastEpisodeDto>?,
) : AmpacheBaseResponse()

/** pubdate is an ATOM date, ie. 2026-10-08T03:54:56+00:00. Returns 0 if missing or invalid */
internal fun parsePubDate(pubdate: String?): Long = try {
    if (pubdate.isNullOrBlank()) 0L else OffsetDateTime.parse(pubdate).toEpochSecond()
} catch (e: Exception) {
    0L
}

/** gson parses untyped numbers as Double, some servers send strings */
private fun Any?.toLongOrZero(): Long = when (this) {
    is Number -> toLong()
    is String -> toLongOrNull() ?: 0L
    else -> 0L
}

fun PodcastEpisodeDto.toPodcastEpisode() = PodcastEpisode(
    id = id,
    title = title?.ifBlank { null } ?: name ?: "",
    podcast = podcast?.toMusicAttribute() ?: MusicAttribute.emptyInstance(),
    description = description ?: "",
    author = author ?: "",
    pubDateEpochSec = parsePubDate(pubdate),
    state = state ?: "",
    // processNumberToInt returns ERROR_INT for a missing value
    time = processNumberToInt(time).coerceAtLeast(0),
    size = size.toLongOrZero(),
    mime = mime ?: "",
    filename = filename ?: "",
    artUrl = processArtUrl(hasArt, art),
    website = website ?: "",
)

@Throws(Exception::class)
fun PodcastEpisodesResponse.toEpisodeList(): List<PodcastEpisode> {
    error?.let {
        it.throwPodcastErrorOrReturnEmpty()
        return listOf()
    }
    return episodes?.map { it.toPodcastEpisode() } ?: listOf()
}
