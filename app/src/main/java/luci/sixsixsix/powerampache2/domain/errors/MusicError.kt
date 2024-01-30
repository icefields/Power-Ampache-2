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

    fun getErrorType(): ErrorType {
        ErrorType.entries.forEach {
            if (it.code == errorCode)
                return it
        }
        return ErrorType.Other
    }

    fun isSessionExpiredError(): Boolean = errorCode == ERROR_CODE_SESSION_EXPIRED
    fun isEmptyResult(): Boolean = errorCode == ERROR_CODE_EMPTY
    fun isDuplicateResult(): Boolean = errorCode == ERROR_CODE_DUPLICATE
    fun isServerUrlNotInitialized(): Boolean = errorCode == ERROR_CODE_SERVER_URL_NOT_INITIALIZED
}

enum class ErrorType(val code: String) {
    ACCOUNT(ERROR_CODE_SESSION_EXPIRED),
    EMPTY(ERROR_CODE_EMPTY),
    DUPLICATE(ERROR_CODE_DUPLICATE),
    SYSTEM(ERROR_CODE_SYSTEM),
    Other("_")
}

private const val ERROR_CODE_SESSION_EXPIRED = "4701"
private const val ERROR_CODE_EMPTY = "4704"
private const val ERROR_CODE_DUPLICATE = "4710"
private const val ERROR_CODE_SYSTEM = "4703"

const val ERROR_TYPE_EMPTY = "empty"
const val ERROR_TYPE_ACCOUNT = "account"
const val ERROR_TYPE_DUPLICATE = "duplicate"
const val ERROR_TYPE_SYSTEM = "system"
