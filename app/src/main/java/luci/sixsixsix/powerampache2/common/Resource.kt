package luci.sixsixsix.powerampache2.common

import java.lang.Exception

sealed class Resource<T> (val data: T? = null, val exception: Exception? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(data: T? = null, exception: Exception, message: String = exception.localizedMessage ?: "") : Resource<T>(data = data, message = message, exception = exception)
    class Loading<T>(val isLoading: Boolean = true) : Resource<T>()
}
