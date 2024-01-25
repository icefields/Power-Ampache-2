package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * interfaces local settings and remote settings from server
 */
@Parcelize
data class LocalSettings(
    val username: String,
    val theme: PowerAmpTheme
): Parcelable {
    companion object {
        fun defaultSettings() =
            LocalSettings("luci.sixsixsix.powerampache2.user.db.pa_default_user",
                PowerAmpTheme.MATERIAL_YOU_DARK
            )
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is LocalSettings) return false
        return "${username}${theme}" == "${other.username}${other.theme}"
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
