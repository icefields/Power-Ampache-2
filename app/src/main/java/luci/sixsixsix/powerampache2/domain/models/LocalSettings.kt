package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

/**
 * interfaces local settings and remote settings from server
 */
@Parcelize
data class LocalSettings(
    val username: String,
    val theme: PowerAmpTheme,
    val workerId: String
): Parcelable {
    companion object {
        fun defaultSettings() = LocalSettings("", PowerAmpTheme.MATERIAL_YOU_DARK, UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is LocalSettings) return false
        return "${username}${theme}${workerId}" == "${other.username}${other.theme}${other.workerId}"
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
