package luci.sixsixsix.powerampache2.data.remote.dto


import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.domain.errors.MusicError

data class ErrorDto(
    @SerializedName("errorAction") // "errorAction":"handshake", "albums"
    val errorAction: String,

    @SerializedName("errorCode") // "errorCode":"4701" with handshake,"4704" with empty result
    val errorCode: String,

    @SerializedName("errorMessage")
    val errorMessage: String,

    @SerializedName("errorType") // "errorType":"account", "empty"
    val errorType: String
)

fun ErrorDto.toError() = MusicError(
    errorAction = errorAction,
    errorCode = errorCode,
    errorMessage = errorMessage,
    errorType = errorType
)
/*
"error": {
"errorCode": "4701",
"errorAction": ,
"errorType": "account",
"errorMessage": "Session Expired"
}

"errorAction":"albums","errorCode":"4704","errorMessage":"No Results","errorType":"empty"
 */