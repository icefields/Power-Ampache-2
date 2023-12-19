package luci.sixsixsix.powerampache2.data.remote.dto


import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.domain.errors.MusicError

data class ErrorDto(
    @SerializedName("errorAction") // "errorAction":"handshake"
    val errorAction: String,

    @SerializedName("errorCode") // "errorCode":"4701",
    val errorCode: String,

    @SerializedName("errorMessage")
    val errorMessage: String,

    @SerializedName("errorType") // "errorType":"account"
    val errorType: String
)

fun ErrorDto.toError() = MusicError(
    errorAction = errorAction,
    errorCode = errorCode,
    errorMessage = errorMessage,
    errorType = errorType
)
