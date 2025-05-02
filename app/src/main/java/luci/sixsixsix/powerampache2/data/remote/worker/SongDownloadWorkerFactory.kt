package luci.sixsixsix.powerampache2.data.remote.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import javax.inject.Inject

class SongDownloadWorkerFactory @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val storageManager: StorageManager
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = SongDownloadWorker(
        api,
        db,
        storageManager,
        appContext,
        workerParameters
    )
}
