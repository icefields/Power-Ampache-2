package luci.sixsixsix.powerampache2.common

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.StarRating
import luci.sixsixsix.powerampache2.presentation.models.PlayableUI
import luci.sixsixsix.powerampache2.presentation.models.PodcastEpisodeUI
import luci.sixsixsix.powerampache2.presentation.models.SongUI

fun SongUI.toMediaItem(songUri: String) = MediaItem.Builder()
    .setMediaId(mediaId)
    .setUri(songUri)
    .setMimeType(mime)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
            .setDiscNumber(disk)
            .setWriter(composer)
            .setRecordingYear(year)
            .setArtworkUri(Uri.parse(imageUrl))
            .setAlbumTitle(album.name)
            .setArtist(artist.name)
            .setDisplayTitle(title)
            .setTitle(title)
            .setTrackNumber(if (trackNumber > 0) { trackNumber } else null)
            .setGenre(if (genre.isNotEmpty()) { genre[0].name } else null)
            .setComposer(composer)
            .setAlbumArtist(albumArtist.name)
            .setOverallRating(StarRating(
                5,
                if (averageRating in 0f..5f) averageRating else 0f))
            .setReleaseYear(year)
            .setUserRating(StarRating(
                5,
                if (rating in 0f..5f) rating else 0f)
            ).build()
    ).build()

fun PlayableUI.toPlayerMediaItem(uri: String): MediaItem = when (this) {
    is SongUI -> toMediaItem(uri)
    is PodcastEpisodeUI -> MediaItem.Builder()
        .setMediaId(key.playerId)
        .setUri(uri)
        .setMimeType(episode.mime.takeIf { it.isNotBlank() })
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setMediaType(MediaMetadata.MEDIA_TYPE_PODCAST_EPISODE)
                .setArtworkUri(Uri.parse(imageUrl))
                .setArtist(subtitle)
                .setAlbumTitle(subtitle)
                .setDisplayTitle(title)
                .setTitle(title)
                .build()
        ).build()
}
