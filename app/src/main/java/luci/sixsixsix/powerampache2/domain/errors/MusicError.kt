package luci.sixsixsix.powerampache2.domain.errors

import com.google.gson.Gson

data class MusicError(
    val errorAction: String,
    val errorCode: String,
    val errorMessage: String,
    val errorType: String
) {
    fun toJson(): String = Gson().toJson(this)

    override fun toString(): String = try {
        toJson()
    } catch (e: Exception) {
        super.toString()
    }

    fun isSessionExpiredError(): Boolean = errorCode == ERROR_CODE_SESSION_EXPIRED
    fun isEmptyResult(): Boolean = errorCode == ERROR_CODE_EMPTY
}

const val ERROR_CODE_SESSION_EXPIRED = "4701"
const val ERROR_CODE_EMPTY = "4704"
const val ERROR_TYPE_EMPTY = "empty"
const val ERROR_TYPE_ACCOUNT = "account"
