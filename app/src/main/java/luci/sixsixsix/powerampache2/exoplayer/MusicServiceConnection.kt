package luci.sixsixsix.powerampache2.exoplayer

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.os.Bundle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import luci.sixsixsix.powerampache2.common.Constants.NETWORK_ERROR
import luci.sixsixsix.powerampache2.common.Event
import luci.sixsixsix.powerampache2.common.Resource

class MusicServiceConnection(context: Context) {
    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackState?>()
    val playbackState: LiveData<PlaybackState?> = _playbackState

    private val _curPlayingSong = MutableLiveData<MediaMetadata?>()
    val curPlayingSong: LiveData<MediaMetadata?> = _curPlayingSong
    
    lateinit var mediaController: MediaController

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowser(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply { connect() }

    val transportControls: MediaController.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowser.SubscriptionCallback) = mediaBrowser.subscribe(parentId, callback)

    fun unsubscribe(parentId: String, callback: MediaBrowser.SubscriptionCallback) = mediaBrowser.unsubscribe(parentId, callback)


    /**
     *
     */
    private inner class MediaBrowserConnectionCallback(private val context: Context) : MediaBrowser.ConnectionCallback() {

        override fun onConnected() {
            Log.d("MusicServiceConnection", "CONNECTED")
            mediaController = MediaController(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _isConnected.postValue(Event(Resource.success(true)))
        }

        override fun onConnectionSuspended() {
            Log.d("MusicServiceConnection", "SUSPENDED")

            _isConnected.postValue(Event(Resource.error(
                "The connection was suspended", false
            )))
        }

        override fun onConnectionFailed() {
            Log.d("MusicServiceConnection", "FAILED")

            _isConnected.postValue(Event(Resource.error(
                "Couldn't connect to media browser", false
            )))
        }
    }


    /**
     *
     */
    private inner class MediaControllerCallback : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) = _playbackState.postValue(state)
        override fun onMetadataChanged(metadata: MediaMetadata?) = _curPlayingSong.postValue(metadata)
        override fun onSessionDestroyed() = mediaBrowserConnectionCallback.onConnectionSuspended()

        override fun onSessionEvent(event: String, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event) {
                NETWORK_ERROR -> _networkError.postValue(
                    Event(
                        Resource.error(
                            "Couldn't connect to the server. Please check your internet connection.",
                            null
                        )
                    )
                )
            }
        }
    }
}
