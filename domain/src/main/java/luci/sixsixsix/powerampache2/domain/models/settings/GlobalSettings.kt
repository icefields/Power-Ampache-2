package luci.sixsixsix.powerampache2.domain.models.settings

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow

data class GlobalSettings(
    val backBuffer: Int,
    val minBufferMs: Int,
    val maxBufferMs: Int,
    val bufferForPlaybackMs: Int,
    val bufferForPlaybackAfterRebufferMs: Int,
    val cacheSizeMb: Int,
    val prioritizeTimeOverSizeThresholds: Boolean,
    val targetBufferBytes: Int,

    val isAllowAllCertificates: Boolean,
    val useOkHttpForExoPlayer: Boolean,
    //val isAllowAllCertificatesFlow: StateFlow<Boolean>,

    //val introDialogContent: String,

    // sleep timer
    //val sleepTimerEndTimestampFlow: StateFlow<Long>,
    val sleepTimerEndTimestamp: Long,
    val sleepTimerWaitSongEnd: Boolean,

    val customDownloadRootUri: Uri?
)
