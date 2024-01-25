package luci.sixsixsix.powerampache2.domain.errors

// this is an internal error, not coming from the server
const val ERROR_CODE_SERVER_URL_NOT_INITIALIZED = "666000666"
const val ERROR_MESSAGE_SERVER_URL_NOT_INITIALIZED = "url not initialized by the user"
class ServerUrlNotInitializedException: MusicException(serverUrlNotInitError)

val serverUrlNotInitError = MusicError(
    errorAction = "authorize",
    errorCode = ERROR_CODE_SERVER_URL_NOT_INITIALIZED,
    errorMessage = ERROR_MESSAGE_SERVER_URL_NOT_INITIALIZED,
    errorType = ERROR_TYPE_ACCOUNT
)