package luci.sixsixsix.powerampache2.data.remote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.MusicRepository
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var musicRepository: MusicRepository
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringArrayExtra("MESSAGE")
        // ping to refresh token
        GlobalScope.launch {
            val ping = musicRepository.ping()
            L(ping)
        }
    }
}
