package luci.sixsixsix.powerampache2.exoplayer


import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.MediaMetadata.*
import android.media.browse.MediaBrowser
import android.media.browse.MediaBrowser.MediaItem.FLAG_PLAYABLE
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.MusicRepository
import javax.inject.Inject

@OptIn(UnstableApi::class)
class FirebaseMusicSource @Inject constructor(private val musicDatabase: MusicRepository) {
    var songs = emptyList<MediaMetadata>()
    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = State.STATE_INITIALIZING
//        musicDatabase.getSongs().collect { result ->
//                when(result) {
//                    is Resource.Success -> {
//                        result.data?.let { allSongs ->
//                            songs = allSongs.map { song: Song ->
//                                MediaMetadata.Builder()
//                                    .putString(METADATA_KEY_ARTIST, song.subtitle)
//                                    .putString(METADATA_KEY_MEDIA_ID, song.mediaId)
//                                    .putString(METADATA_KEY_TITLE, song.title)
//                                    .putString(METADATA_KEY_DISPLAY_TITLE, song.title)
//                                    .putString(METADATA_KEY_DISPLAY_ICON_URI, song.imageUrl)
//                                    .putString(METADATA_KEY_MEDIA_URI, song.songUrl)
//                                    .putString(METADATA_KEY_ALBUM_ART_URI, song.imageUrl)
//                                    .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.subtitle)
//                                    .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.subtitle)
//                                    .build()
//                            }
//                            state = State.STATE_INITIALIZED
//                        }
//                    }
//                    is Resource.Error -> {
//
//                    }
//                    is Resource.Loading -> {
//
//                    }
//                }
//            }


    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            // val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(song.getString(METADATA_KEY_MEDIA_URI).toUri()))
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song: MediaMetadata ->
        val desc = MediaDescription.Builder()
            .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowser.MediaItem(desc, FLAG_PLAYABLE)
    }.toMutableList()

    private var state: State = State.STATE_CREATED
        set(value) {
            if(value == State.STATE_INITIALIZED || value == State.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == State.STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        if(state == State.STATE_CREATED || state == State.STATE_INITIALIZING) {
            onReadyListeners += action
            return false
        } else {
            action(state == State.STATE_INITIALIZED)
            return true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}
