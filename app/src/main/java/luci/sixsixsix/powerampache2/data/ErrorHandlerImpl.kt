package luci.sixsixsix.powerampache2.data

import android.app.Application
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.FlowCollector
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.ErrorType
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.ServerUrlNotInitializedException
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandlerImpl @Inject constructor(
    private val playlistManager: MusicPlaylistManager,
    private val db: MusicDatabase,
    private val applicationContext: Application
): ErrorHandler {
    override suspend fun <T> invoke(
        label:String,
        e: Throwable,
        fc: FlowCollector<Resource<T>>,
        onError: (message: String, e: Throwable) -> Unit
    ) {
        // Blocking errors for server url not initialized
        if (e is MusicException && e.musicError.isServerUrlNotInitialized() ) {
            L("ServerUrlNotInitializedException")
            fc.emit(Resource.Loading(false))
            return
        }

        var readableMessage: String? = null
        StringBuilder(label)
            .append(if (label.isBlank())"" else " - ")
            .append( when(e) {
                is IOException -> {
                    readableMessage = applicationContext.getString(R.string.error_io_exception)
                    "cannot load data IOException $e"
                }
                is HttpException -> {
                    readableMessage = applicationContext.getString(R.string.error_http_exception)
                    "cannot load data HttpException $e"
                }
                is ServerUrlNotInitializedException -> "ServerUrlNotInitializedException $e"
                is MusicException -> {
                    when(e.musicError.getErrorType()) {
                        ErrorType.SESSION_EXPIRED -> {
                            // clear session and try to autologin using the saved credentials
                            db.dao.clearCachedData()
                            db.dao.clearSession()
                        }
                        ErrorType.EMPTY ->
                            readableMessage = applicationContext.getString(R.string.error_empty_result)
                        ErrorType.DUPLICATE ->
                            readableMessage = applicationContext.getString(R.string.error_duplicate)
                        ErrorType.Other -> { }
                    }

                    e.musicError.toString()
                }
                else -> "generic exception $e"
            }).toString().apply {
                // check on error on the emitted data for detailed logging
                fc.emit(Resource.Error<T>(message = this, exception = e))
                // log and report error here
                // ....
                // readable message here
                readableMessage?.let {
                    playlistManager.updateErrorMessage(readableMessage)
                }
                onError(this, e)
                L.e(e)
            }
    }
}
