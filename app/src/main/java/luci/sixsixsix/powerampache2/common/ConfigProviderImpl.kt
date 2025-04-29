package luci.sixsixsix.powerampache2.common

import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.domain.utils.ConfigProvider


class ConfigProviderImpl: ConfigProvider {
    override val USE_INCREMENTAL_LIMIT_ALBUMS
        get() = BuildConfig.USE_INCREMENTAL_LIMIT_ALBUMS
    override val RESET_QUEUE_ON_NEW_SESSION
        get() = BuildConfig.RESET_QUEUE_ON_NEW_SESSION
    override val PLAYLISTS_USER_FETCH
        get() = BuildConfig.PLAYLISTS_USER_FETCH
    override val SMARTLISTS_USER_FETCH
        get() = BuildConfig.SMARTLISTS_USER_FETCH
    override val PLAYLISTS_ADMIN_FETCH
        get() = BuildConfig.PLAYLISTS_ADMIN_FETCH
    override val SMARTLISTS_ADMIN_FETCH
        get() = BuildConfig.SMARTLISTS_ADMIN_FETCH
    override val PLAYLISTS_ALL_SERVER_FETCH
        get() = BuildConfig.PLAYLISTS_ALL_SERVER_FETCH
    override val DOGMAZIC_DEMO_USER
        get() = BuildConfig.DOGMAZIC_USER
    override val FORCE_LOGIN_DIALOG_ON_ALL_VERSIONS
        get() = BuildConfig.FORCE_LOGIN_DIALOG_ON_ALL_VERSIONS
    override val ENABLE_TOKEN_LOGIN: Boolean
        get() = BuildConfig.ENABLE_TOKEN_LOGIN
    override val DOGMAZIC_TOKEN: String
        get() = BuildConfig.DOGMAZIC_TOKEN
    override val DOGMAZIC_URL: String
        get() = BuildConfig.DOGMAZIC_URL
    override val CONFIG_URL: String
        get() = "https://power.ampache.dev/${BuildConfig.REMOTE_CONFIG_FILE}"
    override val AMPACHE_USER_AGENT: String
        get() = "PowerAmpache2-${BuildConfig.VERSION_NAME}"
    override val API_KEY: String
        get() = BuildConfig.API_KEY
    override val IS_DEBUG: Boolean
        get() = BuildConfig.DEBUG
    override val URL_ERROR_LOG: String
        get() = BuildConfig.URL_ERROR_LOG
    override val SHOW_EMPTY_PLAYLISTS: Boolean
        get() = BuildConfig.SHOW_EMPTY_PLAYLISTS
    override val VERSION_CODE: Int
        get() = BuildConfig.VERSION_CODE


    override fun defaultPa2Config() = Pa2Config (
        queueResetOnNewSession = RESET_QUEUE_ON_NEW_SESSION,
        dogmazicDemoUser = DOGMAZIC_DEMO_USER,
        forceLoginDialogsOnAllVersions = FORCE_LOGIN_DIALOG_ON_ALL_VERSIONS,
        enableTokenLogin = ENABLE_TOKEN_LOGIN,
        dogmazicDemoToken = DOGMAZIC_TOKEN,
        dogmazicDemoUrl = DOGMAZIC_URL,
        playlistsUserFetch = PLAYLISTS_USER_FETCH,
        smartlistsUserFetch = SMARTLISTS_USER_FETCH,
        playlistsAdminFetch = PLAYLISTS_ADMIN_FETCH,
        smartlistsAdminFetch = SMARTLISTS_ADMIN_FETCH,
        playlistsServerAllFetch = PLAYLISTS_ALL_SERVER_FETCH,
        useIncrementalLimitForAlbums = USE_INCREMENTAL_LIMIT_ALBUMS
    )
}
