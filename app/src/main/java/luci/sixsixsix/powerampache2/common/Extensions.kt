package luci.sixsixsix.powerampache2.common

import android.util.Log
import luci.sixsixsix.powerampache2.common.Constants.TAG_LOG
import java.security.MessageDigest

fun String.md5(): String {
    return hashString(this, "MD5")
}

fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}



