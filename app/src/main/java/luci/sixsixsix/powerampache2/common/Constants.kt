package luci.sixsixsix.powerampache2.common

import luci.sixsixsix.powerampache2.BuildConfig
import luci.sixsixsix.powerampache2.domain.common.Constants.ERROR_STRING

// UI constants
object Constants {
    const val IS_AMPACHE_DATA = BuildConfig.IS_AMPACHE_DATA
    const val AMPACHE_PREFERENCE_UNDO_VISIBLE = false

    // TIMEOUTS (non-network)
    const val LOCAL_SCROBBLE_TIMEOUT_MS = 20000L
    const val PLAYBACK_ERROR_COUNT_TIMEOUT_MS = 60000L
    const val PLAY_LOAD_TIMEOUT = 10000L
    const val LOGIN_SCREEN_TIMEOUT = 1500L
    const val HOME_LOADING_TIMEOUT = 1000L
    const val SERVICE_STOP_TIMEOUT = 2000L
    const val SEARCH_TIMEOUT = 1200L    // allow user to type before starting search

    // DEBUG VALUES
    const val ERROR_TITLE = ERROR_STRING

    // DONATION LINKS
    const val DONATION_BITCOIN_ADDRESS = "bc1qm9dvdrukgrqpg5f7466u4cy7tfvwcsc8pqshl4"
    const val DONATION_BITCOIN_URI = "bitcoin:$DONATION_BITCOIN_ADDRESS"
    const val DONATION_PAYPAL_URI = "https://paypal.me/powerampache"

    // URLs
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=luci.sixsixsix.powerampache2.play"
    const val GITHUB_IMG_URL = "https://s1.ax1x.com/2023/01/12/pSu1a36.png"
    const val GITHUB_URL = "https://github.com/icefields/Power-Ampache-2"
    const val GPLV3_IMG_URL = "https://www.gnu.org/graphics/gplv3-with-text-136x68.png"
    const val GPLV3_URL = "https://github.com/icefields/Power-Ampache-2/blob/main/LICENSE.md"
    const val TELEGRAM_IMG_URL = "https://static-00.iconduck.com/assets.00/telegram-icon-512x512-4sztjer8.png"
    const val TELEGRAM_URL = "https://t.me/PowerAmpache"
    const val MASTODON_IMG_URL = "https://github.com/icefields/Power-Ampache-2/assets/149625124/81e35dc2-d81c-46ed-9321-4461349dc8e7"
    const val MASTODON_URL = "https://floss.social/@powerampache"
    const val PATREON_IMG_URL = "https://github.com/user-attachments/assets/3318ab05-3c7e-42dd-8784-f12129c0915d"
    const val PATREON_URL = "https://www.patreon.com/Icefields"
    const val BUYMEACOFFEE_URL = "https://buymeacoffee.com/powerampache"
}
