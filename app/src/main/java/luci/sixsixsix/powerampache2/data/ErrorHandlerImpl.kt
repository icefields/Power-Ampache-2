package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.FlowCollector
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
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

        StringBuilder(label)
            .append(if (label.isBlank())"" else " - ")
            .append( when(e) {
                is IOException -> "cannot load data IOException $e"
                is HttpException -> "cannot load data HttpException $e"
                is ServerUrlNotInitializedException -> "ServerUrlNotInitializedException $e"
                is MusicException -> {
                    if (e.musicError.isSessionExpiredError()) {
                        // clear session and try to autologin using the saved credentials
                        db.dao.clearCachedData()
                        db.dao.clearSession()
                    } else if (e.musicError.isEmptyResult()) {
                        // TODO handle empty result
                    }
                    e.musicError.toString()
                }
                else -> "generic exception $e"
            }).toString().apply {
                fc.emit(Resource.Error<T>(message = this, exception = e))
                playlistManager.updateErrorMessage(this)
                onError(this, e)
                L.e(e)
            }
    }
}
