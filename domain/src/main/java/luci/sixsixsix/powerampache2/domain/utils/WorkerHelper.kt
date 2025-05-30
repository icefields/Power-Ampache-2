package luci.sixsixsix.powerampache2.domain.utils

import luci.sixsixsix.powerampache2.domain.models.Song
import java.util.UUID

interface WorkerHelper {
    suspend fun startSongDownloadWorker(authToken: String, username: String, song: Song): UUID
}
