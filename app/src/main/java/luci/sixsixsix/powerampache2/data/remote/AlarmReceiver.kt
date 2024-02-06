package luci.sixsixsix.powerampache2.data.remote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.toDebugString
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var musicRepository: MusicRepository

    @Inject
    lateinit var errorHandler: ErrorHandler

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val message = intent?.getStringExtra("MESSAGE")
            L(message)
        } catch (e: Exception) {
            L.e(e)
        }
        // ping to refresh token
        GlobalScope.launch {
            val ping = musicRepository.ping()
            //errorHandler.logError(Gson().toJson(ping))
            L(ping)
        }
    }
}
