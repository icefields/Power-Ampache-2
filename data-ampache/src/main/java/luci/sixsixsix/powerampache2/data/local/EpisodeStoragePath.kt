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
package luci.sixsixsix.powerampache2.data.local

import luci.sixsixsix.powerampache2.domain.models.PodcastEpisode

/** first-level directory under offline_music/{user}/ that holds the downloaded episodes */
internal const val PODCASTS_DIR = "podcasts"
private const val DEFAULT_EXTENSION = "mp3"
// most file systems allow 255 bytes per file name, keep room for the extension
private const val MAX_FILE_NAME_BYTES = 200
private val ILLEGAL_CHARS = Regex("""[\\/:*?"<>|\u0000-\u001F]""")

/**
 * Replaces characters that are illegal in file names and truncates to [maxBytes] UTF-8 bytes
 * without cutting a code point in half.
 */
fun sanitizeFileName(name: String, maxBytes: Int = MAX_FILE_NAME_BYTES): String =
    name.replace(ILLEGAL_CHARS, "_")
        .trim()
        .truncateUtf8(maxBytes)
        .trim()
        .trimEnd('.')
        .ifBlank { "_" }

internal fun String.truncateUtf8(maxBytes: Int): String {
    val sb = StringBuilder()
    var bytes = 0
    var i = 0
    while (i < length) {
        val codePoint = codePointAt(i)
        val chars = String(Character.toChars(codePoint))
        val size = chars.toByteArray(Charsets.UTF_8).size
        if (bytes + size > maxBytes) break
        sb.append(chars)
        bytes += size
        i += Character.charCount(codePoint)
    }
    return sb.toString()
}

/** directory relative to offline_music/{user}/ */
fun episodeRelativeDir(episode: PodcastEpisode): String =
    "$PODCASTS_DIR/${sanitizeFileName(episode.podcast.name.ifBlank { episode.podcast.id })}"

/** the episode id keeps two episodes with the same title apart */
fun episodeFileName(episode: PodcastEpisode): String {
    val extension = episode.filename.substringAfterLast('.', "")
        .takeIf { it.isNotBlank() && it.length <= 5 && !it.contains(ILLEGAL_CHARS) }
        ?: DEFAULT_EXTENSION
    return "${sanitizeFileName("${episode.id} - ${episode.title}")}.$extension"
}
