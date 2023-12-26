package luci.sixsixsix.powerampache2.common

import android.util.Log

class Logger(private vararg val messages: Any?) {
    init {
        invoke()
    }

    operator fun invoke() {
        val sb = StringBuilder()
        messages.forEach {
            sb.append("$it")
            sb.append(" **** ")
        }
        Log.d(Constants.TAG_LOG, sb.toString())
    }
}

typealias L = Logger
