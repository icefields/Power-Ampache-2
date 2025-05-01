package luci.sixsixsix.powerampache2.domain.utils

import android.content.Context

interface DownloadWorkerManager {
    suspend fun stopAllDownloads()
    suspend fun getDownloadWorkerId(): String
    suspend fun resetDownloadWorkerId(): String
}