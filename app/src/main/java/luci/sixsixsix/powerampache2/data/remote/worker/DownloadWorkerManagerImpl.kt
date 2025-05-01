package luci.sixsixsix.powerampache2.data.remote.worker

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import luci.sixsixsix.powerampache2.domain.utils.DownloadWorkerManager
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val prefix = "luci.sixsixsix.powerampache2.worker."
private const val KEY_WORKER_PREFERENCE = "${prefix}KEY_WORKER_PREFERENCE"
private const val KEY_WORKER_PREFERENCE_ID = "${prefix}downloadWorkerId"

@Singleton
class DownloadWorkerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
): DownloadWorkerManager {
    override suspend fun getDownloadWorkerId(): String = context
        .getSharedPreferences(KEY_WORKER_PREFERENCE, Context.MODE_PRIVATE)
        .getString(KEY_WORKER_PREFERENCE_ID, null) ?: run {
        // if not existent create one now
        resetDownloadWorkerId()
    }

    override suspend fun resetDownloadWorkerId() =
        writeDownloadWorkerId(UUID.randomUUID().toString())

    @SuppressLint("ApplySharedPref")
    private suspend fun writeDownloadWorkerId(
        newWorkerId: String
    ): String = withContext(Dispatchers.IO) {
        val sharedPreferences = context.getSharedPreferences(KEY_WORKER_PREFERENCE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString(KEY_WORKER_PREFERENCE_ID, newWorkerId)
            commit()
        }
        return@withContext newWorkerId
    }

    override suspend fun stopAllDownloads() {
        WorkManager.getInstance(context).cancelUniqueWork(getDownloadWorkerId())
        // change worker name otherwise cannot restart work
        resetDownloadWorkerId()
    }
}
