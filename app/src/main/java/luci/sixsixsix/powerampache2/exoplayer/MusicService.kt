package luci.sixsixsix.powerampache2.exoplayer

import android.app.PendingIntent
import android.content.Intent
import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import luci.sixsixsix.powerampache2.common.Constants.MEDIA_ROOT_ID
import luci.sixsixsix.powerampache2.common.Constants.NETWORK_ERROR
import luci.sixsixsix.powerampache2.exoplayer.callbacks.MusicPlayerEventListener
import luci.sixsixsix.powerampache2.exoplayer.callbacks.MusicPlayerNotificationListener
import javax.inject.Inject

private const val SERVICE_TAG = "MusicService"

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class MusicService : MediaLibraryService() {

    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var firebaseMusicSource: FirebaseMusicSource

    private lateinit var musicNotificationManager: MusicNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var mediaSession: MediaSession? = null
// TODO    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false

    private var curPlayingSong: MediaMetadata? = null

    private var isPlayerInitialized = false

    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    companion object {
        var curSongDuration = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            firebaseMusicSource.fetchMediaData()
        }

        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE) // TODO changed from 0 to FLAG_IMMUTABLE
        }

//        mediaSession = MediaSession(this, SERVICE_TAG).apply {
//            setSessionActivity(activityIntent)
//            isActive = true
//        }

        mediaSession = activityIntent?.let {
            MediaSession.Builder(this, exoPlayer)
                .setSessionActivity(activityIntent)
                .build().apply {
    //            setSessionActivity(activityIntent)
    //            isActive = true
                }
        }







// TODO
        //sessionToken = mediaSession?.token

//        musicNotificationManager = MusicNotificationManager(
//            this,
//            mediaSession.token,
//            MusicPlayerNotificationListener(this)
//        ) {
//            curSongDuration = exoPlayer.duration
//        }

//        val musicPlaybackPreparer = MusicPlaybackPreparer(firebaseMusicSource) {
//            curPlayingSong = it
//            preparePlayer(
//                firebaseMusicSource.songs,
//                it,
//                true
//            )
//        }

        // TODO
//        mediaSessionConnector = MediaSessionConnector(mediaSession)
//        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
//        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
//        mediaSessionConnector.setPlayer(exoPlayer)

//        musicPlayerEventListener = MusicPlayerEventListener(this)
//        exoPlayer.addListener(musicPlayerEventListener)
//        musicNotificationManager.showNotification(exoPlayer)
    }

    // TODO
//    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {
//
//        override fun getMediaDescription(
//            player: Player?,
//            windowIndex: Int
//        ): MediaDescription = firebaseMusicSource.songs[windowIndex].description
//    }

    private fun preparePlayer(
        songs: List<MediaMetadata>,
        itemToPlay: MediaMetadata?,
        playNow: Boolean
    ) {
        val curSongIndex = if(curPlayingSong == null) 0 else songs.indexOf(itemToPlay)
        exoPlayer.prepare(firebaseMusicSource.asMediaSource(dataSourceFactory))
        exoPlayer.seekTo(curSongIndex, 0L)
        exoPlayer.playWhenReady = playNow
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        // TODO Not yet implemented
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }


        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }

    // TODO
//    override fun onGetRoot(
//        clientPackageName: String,
//        clientUid: Int,
//        rootHints: Bundle?
//    ): BrowserRoot? {
//        return BrowserRoot(MEDIA_ROOT_ID, null)
//    }
//
//    override fun onLoadChildren(
//        parentId: String,
//        result: Result<MutableList<MediaBrowser.MediaItem>>
//    ) {
//        when(parentId) {
//            MEDIA_ROOT_ID -> {
//                val resultsSent = firebaseMusicSource.whenReady { isInitialized ->
//                    if(isInitialized) {
//                        result.sendResult(firebaseMusicSource.asMediaItems())
//                        if(!isPlayerInitialized && firebaseMusicSource.songs.isNotEmpty()) {
//                            preparePlayer(firebaseMusicSource.songs, firebaseMusicSource.songs[0], false)
//                            isPlayerInitialized = true
//                        }
//                    } else {
//                        mediaSession.sendSessionEvent(NETWORK_ERROR, null)
//                        result.sendResult(null)
//                    }
//                }
//                if(!resultsSent) {
//                    result.detach()
//                }
//            }
//        }
//    }
}
