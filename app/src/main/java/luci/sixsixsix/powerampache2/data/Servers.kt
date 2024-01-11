package luci.sixsixsix.powerampache2.data

const val DEBUG_USER = "test-user"
const val DEBUG_PASSWORD = "testtest"
const val DEBUG_LOCAL_DEMO_URL = "http://192.168.1.100/ampache/public/"

const val DEBUG_REMOTE_USER = "test-user"
const val DEBUG_REMOTE_PASSWORD = "testtest"
const val DEBUG_REMOTE_DEMO_URL = "tari.ddns.net/"

const val AMPACHE_DEMO_APIKEY = "demodemo"
const val AMPACHE_DEMO_URL = "demo.ampache.dev"

const val DOGMAZIC_USER = "ths6hh"
const val DOGMAZIC_PASSWORD = "8z19xeid0f6b4"
const val DOGMAZIC_URL = "play.dogmazic.net"

sealed class Servers(
    val url: String,
    val user: String = "",
    val password: String = "",
    val apiKey: String = ""
) {
    data object LocalDebug: Servers(
        url = DEBUG_LOCAL_DEMO_URL,
        user = DEBUG_USER,
        password = DEBUG_REMOTE_PASSWORD
    )

    data object RemoteDebug: Servers(
        url = DEBUG_REMOTE_DEMO_URL,
        user = DEBUG_REMOTE_USER,
        password = DEBUG_PASSWORD
    )

    data object AmpacheDemo: Servers(
        url = AMPACHE_DEMO_URL,
        apiKey = AMPACHE_DEMO_APIKEY
    )

    data object Dogmazic: Servers(
        url = DOGMAZIC_URL,
        user = DOGMAZIC_USER,
        password = DOGMAZIC_PASSWORD
    )
}
