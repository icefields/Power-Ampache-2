package luci.sixsixsix.powerampache2.common

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.StarRating
import luci.sixsixsix.powerampache2.domain.models.Song

fun Song.toMediaItem(songUri: String) = MediaItem.Builder()
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
            .setOverallRating(StarRating(5, if (averageRating in 0f..5f) averageRating.toFloat() else 0f))
            .setReleaseYear(year)
            .setUserRating(StarRating(5, if (rating in 0f..5f) rating.toFloat() else 0f))
            .build()
    ).build()