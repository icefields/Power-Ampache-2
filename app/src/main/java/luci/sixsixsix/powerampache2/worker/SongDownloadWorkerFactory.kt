package luci.sixsixsix.powerampache2.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import luci.sixsixsix.powerampache2.domain.datasource.DbDataSource
import luci.sixsixsix.powerampache2.domain.datasource.NetworkDataSource
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import javax.inject.Inject

class SongDownloadWorkerFactory @Inject constructor(
    private val api: NetworkDataSource,
    private val db: DbDataSource,
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
