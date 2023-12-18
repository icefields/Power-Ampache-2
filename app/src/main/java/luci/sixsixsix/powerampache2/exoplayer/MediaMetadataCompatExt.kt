package luci.sixsixsix.powerampache2.exoplayer

import android.media.MediaMetadata
import luci.sixsixsix.powerampache2.data.entities.Song

fun MediaMetadata.toSong(): Song? = description?.let {
    Song(
        it.mediaId ?: "",
        it.title.toString(),
        it.subtitle.toString(),
        it.mediaUri.toString(),
        it.iconUri.toString()
    )
}
