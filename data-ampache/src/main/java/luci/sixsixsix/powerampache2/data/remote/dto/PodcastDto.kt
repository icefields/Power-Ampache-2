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
import luci.sixsixsix.powerampache2.domain.errors.ErrorType
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.PodcastsDisabledException
import luci.sixsixsix.powerampache2.domain.models.Podcast

data class PodcastDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("website")
    val website: String? = "",
    @SerializedName("art")
    val art: String? = "",
    @SerializedName("has_art")
    val hasArt: Any? = null,
    @SerializedName("sync_date")
    val syncDate: String? = "",
)

data class PodcastsResponse(
    @SerializedName("podcast") val podcasts: List<PodcastDto>?,
) : AmpacheBaseResponse()

fun PodcastDto.toPodcast() = Podcast(
    id = id,
    name = name ?: "",
    description = description ?: "",
    website = website ?: "",
    artUrl = processArtUrl(hasArt, art),
    syncDate = syncDate ?: "",
)

/**
 * 4704 (empty) is not an error for podcasts. The podcasts methods return 4703 (access denied) only
 * when the "podcast" option is disabled on the server.
 */
@Throws(Exception::class)
internal fun ErrorDto.throwPodcastErrorOrReturnEmpty() {
    val error = toError()
    if (error.isEmptyResult()) return
    if (error.getErrorType() == ErrorType.SYSTEM) throw PodcastsDisabledException()
    throw MusicException(error)
}

@Throws(Exception::class)
fun PodcastsResponse.toPodcastList(): List<Podcast> {
    error?.let {
        it.throwPodcastErrorOrReturnEmpty()
        return listOf()
    }
    return podcasts?.map { it.toPodcast() } ?: listOf()
}
