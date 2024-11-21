package luci.sixsixsix.powerampache2.data.local.models

import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.domain.models.StreamingQuality

data class SongUrl(
    val authToken: String,
    val serverUrl: String,
    val bitrate: Int = StreamingQuality.VERY_HIGH.bitrate
) {
    companion object {
        const val SONG_URL = "%s/json.server.php?action=stream&stats=0&auth=%s&type=song&id=%s%s"
    }

    fun getUrl(mediaId: String) =
        String.format(SONG_URL, MainNetwork.buildServerUrl(serverUrl), authToken, mediaId,
            if (bitrate < StreamingQuality.VERY_HIGH.bitrate) "&bitrate=$bitrate" else "")
}
