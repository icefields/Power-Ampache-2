package luci.sixsixsix.powerampache2.data.remote.dto


import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.domain.errors.MusicError

data class ErrorDto(
    @SerializedName("errorAction")
    val errorAction: String,

    @SerializedName("errorCode")
    val errorCode: String,

    @SerializedName("errorMessage")
    val errorMessage: String,

    @SerializedName("errorType")
    val errorType: String
)

fun ErrorDto.toError() = MusicError(
    errorAction = errorAction,
    errorCode = errorCode,
    errorMessage = errorMessage,
    errorType = errorType
)
