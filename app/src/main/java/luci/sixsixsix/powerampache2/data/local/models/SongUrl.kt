package luci.sixsixsix.powerampache2.data.local.models

import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.models.StreamingQuality

data class SongUrl(
    val authToken: String,
    val serverUrl: String,
    val bitrate: Int
) {
    companion object {
        const val songUrl = "%s/json.server.php?action=stream&auth=%s&type=song&id=%s%s"
    }

    fun getUrl(mediaId: String) =
        String.format(songUrl, MainNetwork.buildServerUrl(serverUrl), authToken, mediaId,
            if (bitrate < StreamingQuality.VERY_HIGH.bitrate) "&bitrate=$bitrate" else "")
}
