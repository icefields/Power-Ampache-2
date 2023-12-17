package luci.sixsixsix.powerampache2.common

import java.lang.Exception

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?) = Resource(Status.SUCCESS, data, null)
        fun <T> error(message: String, data: T?) = Resource(Status.ERROR, data, message)
        fun <T> loading(data: T?) = Resource(Status.LOADING, data, null)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}


sealed class Resource2<T> (val data: T? = null, val exception: Exception? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource2<T>(data)
    class Error<T>(data: T? = null, exception: Exception, message: String = exception.localizedMessage ?: "") : Resource2<T>(data = data, message = message, exception = exception)
    class Loading<T>(val isLoading: Boolean = true) : Resource2<T>()
}
