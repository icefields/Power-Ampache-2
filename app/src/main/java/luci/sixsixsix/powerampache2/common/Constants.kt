package luci.sixsixsix.powerampache2.common

object Constants {
    const val SONG_COLLECTION = "songs"
    const val MEDIA_ROOT_ID = "root_id"
    const val NETWORK_ERROR = "NETWORK_ERROR"
    const val UPDATE_PLAYER_POSITION_INTERVAL = 100L
    const val NOTIFICATION_CHANNEL_ID = "music"
    const val NOTIFICATION_ID = 1

    // NETWORK
    const val TIMEOUT_CONNECTION_S = 120L
    const val TIMEOUT_READ_S = 120L
    const val TIMEOUT_WRITE_S = 120L

    const val ERROR_INT = -1

    const val CLEAR_TABLE_AFTER_FETCH = false

    const val HOME_MAX_SONGS = 66

    // DEBUG
    const val NETWORK_REQUEST_LIMIT_DEBUG = 20
    const val DEBUG_USER = "test-user"
    const val TAG_LOG = "lucie"
    const val DEBUG_PASSWORD = "testtest"
    const val DEBUG_URL = "http://192.168.1.100/ampache/public/server/"
}
