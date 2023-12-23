package luci.sixsixsix.powerampache2.common

import android.util.Log

class Logger(private val message: String) {
    init {
        invoke()
    }

    operator fun invoke() {
        Log.d(Constants.TAG_LOG, message)
    }
}

typealias L = Logger