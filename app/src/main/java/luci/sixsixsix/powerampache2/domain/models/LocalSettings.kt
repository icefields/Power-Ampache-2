/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.domain.models

import android.os.Build
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.LocalSettings.Companion.SETTINGS_DEFAULTS_THEME
import java.lang.StringBuilder

private const val BITRATE_VERY_HIGH = 320
private const val BITRATE_HIGH = 256
private const val BITRATE_MEDIUM = 192
private const val BITRATE_MEDIUM_LOW = 128
private const val BITRATE_LOW = 96
private val defaultBitrate = StreamingQuality.getStreamingQualityFromBitrate(LocalSettings.SETTINGS_DEFAULTS_STREAMING_QUALITY)


private const val ID_SYSTEM = "SYSTEM"
private const val ID_DARK = "DARK"
private const val ID_LIGHT = "LIGHT"
private const val ID_MATERIAL_YOU_SYSTEM = "MATERIAL_YOU_SYSTEM"
private const val ID_MATERIAL_YOU_DARK = "MATERIAL_YOU_DARK"
private const val ID_MATERIAL_YOU_LIGHT = "MATERIAL_YOU_LIGHT"
private val defaultTheme = PowerAmpTheme.getThemeFromId(SETTINGS_DEFAULTS_THEME)

/**
 * interfaces local settings and remote settings from server
 */
@Parcelize
data class LocalSettings(
    val username: String,
    val theme: PowerAmpTheme,
    val enableRemoteLogging: Boolean,
    val hideDonationButton: Boolean,
    val smartDownloadEnabled: Boolean,
    val enableAutoUpdates: Boolean,
    val streamingQuality: StreamingQuality
): Parcelable {
    companion object {
        // defaults
        private const val SETTINGS_DEFAULTS_USERNAME = "luci.sixsixsix.powerampache2.user.db.pa_default_user"
        const val SETTINGS_DEFAULTS_ENABLE_REMOTE_LOG = false
        const val SETTINGS_DEFAULTS_HIDE_DONATION = false
        const val SETTINGS_DEFAULTS_ENABLE_SMART_DOWNLOAD = false
        const val SETTINGS_DEFAULTS_ENABLE_AUTO_UPDATE = false
        const val SETTINGS_DEFAULTS_STREAMING_QUALITY = BITRATE_VERY_HIGH
        const val SETTINGS_DEFAULTS_THEME = ID_DARK

        fun defaultSettings(username: String? = null) =
            LocalSettings(
                username = username ?: SETTINGS_DEFAULTS_USERNAME,
                theme = defaultTheme,
                enableRemoteLogging = SETTINGS_DEFAULTS_ENABLE_REMOTE_LOG,
                hideDonationButton = SETTINGS_DEFAULTS_HIDE_DONATION,
                smartDownloadEnabled = SETTINGS_DEFAULTS_ENABLE_SMART_DOWNLOAD,
                enableAutoUpdates = SETTINGS_DEFAULTS_ENABLE_AUTO_UPDATE,
                streamingQuality = defaultBitrate
            )
    }

    override fun equals(othe: Any?): Boolean {
        if (othe == null) return false
        if (othe !is LocalSettings) return false

        val sbThis = StringBuilder(username)
        val sbOthe = StringBuilder(othe.username)

        sbThis.append(this.theme)
        sbOthe.append(othe.theme)

        sbThis.append(this.enableRemoteLogging)
        sbOthe.append(othe.enableRemoteLogging)

        sbThis.append(this.hideDonationButton)
        sbOthe.append(othe.hideDonationButton)

        sbThis.append(this.smartDownloadEnabled)
        sbOthe.append(othe.smartDownloadEnabled)

        sbThis.append(this.streamingQuality.bitrate)
        sbOthe.append(othe.streamingQuality.bitrate)

        sbThis.append(this.enableAutoUpdates)
        sbOthe.append(othe.enableAutoUpdates)

        return sbThis.toString() == sbOthe.toString()
    }
}

@Parcelize
sealed class StreamingQuality(
    val bitrate: Int,
    @StringRes val title: Int,
    @StringRes val description: Int
): Parcelable {
    @Parcelize data object VERY_HIGH: StreamingQuality(
        bitrate = BITRATE_VERY_HIGH,
        title = R.string.quality_veryHigh_title,
        description = R.string.quality_veryHigh_subtitle
    )
    @Parcelize data object HIGH: StreamingQuality(
        bitrate = BITRATE_HIGH,
        title = R.string.quality_high_title,
        description = R.string.quality_high_subtitle
    )
    @Parcelize data object MEDIUM: StreamingQuality(
        bitrate = BITRATE_MEDIUM,
        title = R.string.quality_medium_title,
        description = R.string.quality_medium_subtitle
    )
    @Parcelize data object MEDIUM_LOW: StreamingQuality(
        bitrate = BITRATE_MEDIUM_LOW,
        title = R.string.quality_mediumLow_title,
        description = R.string.quality_mediumLow_subtitle
    )
    @Parcelize data object LOW: StreamingQuality(
        bitrate = BITRATE_LOW,
        title = R.string.quality_low_title,
        description = R.string.quality_low_subtitle
    )
    override fun hashCode() = bitrate.hashCode()
    override fun toString() = "$bitrate"

    companion object {
        fun getStreamingQualityFromBitrate(bitrate: Int): StreamingQuality =
            when(bitrate) {
                BITRATE_VERY_HIGH -> VERY_HIGH
                BITRATE_HIGH -> HIGH
                BITRATE_MEDIUM -> MEDIUM
                BITRATE_MEDIUM_LOW -> MEDIUM_LOW
                BITRATE_LOW -> LOW
                else -> VERY_HIGH
            }
    }
}

/**
 * available themes
 */
@Parcelize
sealed class PowerAmpTheme(val themeId: String, val title: String, val isEnabled: Boolean): Parcelable {
    @Parcelize data object SYSTEM:
        PowerAmpTheme(ID_SYSTEM, "System Theme", true)
    @Parcelize data object DARK:
        PowerAmpTheme(ID_DARK, "Dark", true)
    @Parcelize data object LIGHT:
        PowerAmpTheme(ID_LIGHT, "Light", true)
    @Parcelize data object MATERIAL_YOU_SYSTEM:
        PowerAmpTheme(ID_MATERIAL_YOU_SYSTEM, "MaterialYou System", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    @Parcelize data object MATERIAL_YOU_DARK:
        PowerAmpTheme(ID_MATERIAL_YOU_DARK, "MaterialYou Dark", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    @Parcelize data object MATERIAL_YOU_LIGHT:
        PowerAmpTheme(ID_MATERIAL_YOU_LIGHT, "MaterialYou Light", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

    override fun equals(other: Any?) = try { (other as PowerAmpTheme).themeId == themeId } catch (e: Exception) { false }
    override fun hashCode() = themeId.hashCode()
    override fun toString() = themeId

    companion object {
        fun getThemeFromId(themeId: String): PowerAmpTheme =
            when(themeId) {
                ID_SYSTEM -> SYSTEM
                ID_DARK -> DARK
                ID_LIGHT -> LIGHT
                ID_MATERIAL_YOU_SYSTEM -> MATERIAL_YOU_SYSTEM
                ID_MATERIAL_YOU_DARK -> MATERIAL_YOU_DARK
                ID_MATERIAL_YOU_LIGHT -> MATERIAL_YOU_LIGHT
                else -> defaultTheme
            }
    }
}
