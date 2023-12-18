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
}
