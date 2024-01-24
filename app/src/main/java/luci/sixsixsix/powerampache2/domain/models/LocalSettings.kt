package luci.sixsixsix.powerampache2.domain.models

import java.util.UUID

/**
 * interfaces local settings and remote settings from server
 */
data class LocalSettings(
    val username: String,
    val theme: PowerAmpTheme,
    val workerId: String
) {
    companion object {
        fun defaultSettings() = LocalSettings("", PowerAmpTheme.MATERIAL_YOU_DARK, UUID.randomUUID().toString())
    }
}

enum class PowerAmpTheme {
    SYSTEM,
    DARK,
    LIGHT,
    MATERIAL_YOU_SYSTEM,
    MATERIAL_YOU_DARK,
    MATERIAL_YOU_LIGHT,
}
