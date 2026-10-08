/**
 * Copyright (C) 2025  Antonio Tari
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

import androidx.room.ColumnInfo
import androidx.room.Entity
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode

@Entity(primaryKeys = ["id", "multiUserId"])
data class DownloadedPodcastEpisodeEntity(
    val id: String,
    val title: String,
    val podcastId: String,
    val podcastName: String,
    val description: String,
    val author: String,
    val pubDateEpochSec: Long,
    val state: String,
    val time: Int,
    val size: Long,
    val mime: String,
    val filename: String,
    val website: String,
    /** absolute file path or SAF content uri */
    val localPath: String,
    /** local file path of the art, or the remote art url if the art download failed */
    val localImagePath: String,
    @ColumnInfo(name = "multiUserId", defaultValue = "")
    val multiUserId: String,
)

fun DownloadedPodcastEpisodeEntity.toPodcastEpisode() = PodcastEpisode(
    id = id,
    title = title,
    podcast = MusicAttribute(id = podcastId, name = podcastName),
    description = description,
    author = author,
    pubDateEpochSec = pubDateEpochSec,
    state = state,
    time = time,
    size = size,
    mime = mime,
    filename = filename,
    artUrl = localImagePath,
    website = website,
)

fun PodcastEpisode.toDownloadedPodcastEpisodeEntity(
    multiUserId: String,
    localPath: String,
    localImagePath: String
) = DownloadedPodcastEpisodeEntity(
    id = id,
    title = title,
    podcastId = podcast.id,
    podcastName = podcast.name,
    description = description,
    author = author,
    pubDateEpochSec = pubDateEpochSec,
    state = state,
    time = time,
    size = size,
    mime = mime,
    filename = filename,
    website = website,
    localPath = localPath,
    localImagePath = localImagePath,
    multiUserId = multiUserId,
)
