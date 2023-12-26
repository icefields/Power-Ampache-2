package luci.sixsixsix.powerampache2.common

sealed class Resource<T>(
    val data: T? = null,
    val exception: Throwable? = null,
    val message: String? = null
) {
    /**
     * if networkData is null means we fetched only from cache/database. If network data is empty
     * and data is not it might the end of the list if an offset present in the request
     */
    class Success<T>(data: T, val networkData: T? = null) : Resource<T>(data)
    class Error<T>(
        data: T? = null,
        exception: Throwable,
        message: String = exception.localizedMessage ?: ""
    ) : Resource<T>(data = data, message = message, exception = exception)

    class Loading<T>(val isLoading: Boolean = true) : Resource<T>()
}
