package luci.sixsixsix.powerampache2.common

object Constants {
    // NETWORK
    const val TIMEOUT_CONNECTION_S = 20L
    const val TIMEOUT_READ_S = 120L
    const val TIMEOUT_WRITE_S = 120L

    const val NETWORK_REQUEST_LIMIT_HOME = 40

    const val ERROR_INT = -1
    const val ERROR_FLOAT = -1f
    const val ERROR_STRING = "ERROR"
    const val LOADING_STRING = "LOADING"

    const val CLEAR_TABLE_AFTER_FETCH = false

    // LOCAL DB
    const val DB_LOCAL_NAME = "musicdb.db"

    // DEBUG
    const val NETWORK_REQUEST_LIMIT_DEBUG = 20
    const val ERROR_TITLE = ERROR_STRING
}
