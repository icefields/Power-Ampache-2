package luci.sixsixsix.powerampache2.common

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.utils.WorkerHelper
import luci.sixsixsix.powerampache2.worker.SongDownloadWorker
import java.util.UUID
import javax.inject.Inject

class WorkerHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context
): WorkerHelper {
    override suspend fun startSongDownloadWorker(
        authToken: String,
        username: String,
        song: Song
    ): UUID = SongDownloadWorker.startSongDownloadWorker(context, authToken, username, song)
}
