package luci.sixsixsix.powerampache2.domain.utils

import luci.sixsixsix.powerampache2.common.Pa2Config

interface ConfigProvider {
    val USE_INCREMENTAL_LIMIT_ALBUMS: Boolean
    val RESET_QUEUE_ON_NEW_SESSION: Boolean
    val PLAYLISTS_USER_FETCH: Boolean
    val SMARTLISTS_USER_FETCH: Boolean
    val PLAYLISTS_ADMIN_FETCH: Boolean
    val SMARTLISTS_ADMIN_FETCH: Boolean
    val PLAYLISTS_ALL_SERVER_FETCH: Boolean
    val DOGMAZIC_DEMO_USER: String
    val FORCE_LOGIN_DIALOG_ON_ALL_VERSIONS: Boolean
    val ENABLE_TOKEN_LOGIN: Boolean
    val DOGMAZIC_TOKEN: String
    val DOGMAZIC_URL: String
    val CONFIG_URL: String
    val AMPACHE_USER_AGENT: String

    fun defaultPa2Config(): Pa2Config
}
