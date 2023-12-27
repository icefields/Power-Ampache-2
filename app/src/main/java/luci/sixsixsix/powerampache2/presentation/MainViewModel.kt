package luci.sixsixsix.powerampache2.presentation

import android.media.MediaMetadata
import android.media.MediaMetadata.METADATA_KEY_MEDIA_ID
import android.media.browse.MediaBrowser
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.remote.dto.SongDto
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.toSong
import luci.sixsixsix.powerampache2.exoplayer.MusicServiceConnection
import luci.sixsixsix.powerampache2.exoplayer.State
import luci.sixsixsix.powerampache2.exoplayer.isPlayEnabled
import luci.sixsixsix.powerampache2.exoplayer.isPlaying
import luci.sixsixsix.powerampache2.exoplayer.isPrepared
import javax.inject.Inject

//@HiltViewModel
//class MainViewModel @Inject constructor(
//    private val musicServiceConnection: MusicServiceConnection,
//    private val musicRepository: MusicRepository
//) : ViewModel() {



    // TODO OLD CODE
//    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
//    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems
//
//    val isConnected = musicServiceConnection.isConnected
//    val networkError = musicServiceConnection.networkError
//    val curPlayingSong = musicServiceConnection.curPlayingSong
//    val playbackState = musicServiceConnection.playbackState
//
//    init {
//        _mediaItems.postValue(Resource.Loading(true))
//        musicServiceConnection.subscribe(MEDIA_ROOT_ID, object : MediaBrowser.SubscriptionCallback() {
//            override fun onChildrenLoaded(
//                parentId: String,
//                children: MutableList<MediaBrowser.MediaItem>
//            ) {
//                super.onChildrenLoaded(parentId, children)
//                val items = children.map {
//                    it.toSong()
//                }
//                _mediaItems.postValue(Resource.Success(items))
//            }
//        })
//    }
//
//    fun skipToNextSong() {
//        musicServiceConnection.transportControls.skipToNext()
//    }
//
//    fun skipToPreviousSong() {
//        musicServiceConnection.transportControls.skipToPrevious()
//    }
//
//    fun seekTo(pos: Long) {
//        musicServiceConnection.transportControls.seekTo(pos)
//    }
//
//    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false) {
//        val isPrepared = playbackState.value?.isPrepared ?: false
//        if(isPrepared && mediaItem.mediaId ==
//            curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
//            playbackState.value?.let { playbackState ->
//                when {
//                    playbackState.isPlaying -> if(toggle) musicServiceConnection.transportControls.pause()
//                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
//                    else -> Unit
//                }
//            }
//        } else {
//            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId, null)
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object : MediaBrowser.SubscriptionCallback() {})
//    }
//}
