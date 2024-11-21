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
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.models.LocalSettings.Companion.SETTINGS_DEFAULTS_PLAYLIST_SORT
import luci.sixsixsix.powerampache2.domain.models.LocalSettings.Companion.SETTINGS_DEFAULTS_THEME
import luci.sixsixsix.powerampache2.presentation.screens.settings.components.PowerAmpacheDropdownItem
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

private const val SORT_MODE_ASC = "ASC"
private const val SORT_MODE_DESC = "DESC"
val defaultPlaylistSort = SortMode.valueOf(SETTINGS_DEFAULTS_PLAYLIST_SORT)

/**
 * interfaces local settings and remote settings from server
 */
@Parcelize
data class LocalSettings(
    val username: String,
    val theme: PowerAmpTheme,
    val enableRemoteLogging: Boolean,
    val hideDonationButton: Boolean,
    val enableAutoUpdates: Boolean,
    val streamingQuality: StreamingQuality,
    val isNormalizeVolumeEnabled: Boolean,
    val isMonoAudioEnabled: Boolean,
    val isSmartDownloadsEnabled: Boolean,
    val isGlobalShuffleEnabled: Boolean,
    val isOfflineModeEnabled: Boolean,
    val playlistSongsSorting: SortMode,
    val isDownloadsSdCard: Boolean,
    ): Parcelable {
    companion object {
        // defaults
        private const val SETTINGS_DEFAULTS_USERNAME = "luci.sixsixsix.powerampache2.user.db.pa_default_user"
        const val SETTINGS_DEFAULTS_ENABLE_REMOTE_LOG = false
        const val SETTINGS_DEFAULTS_HIDE_DONATION = false
        const val SETTINGS_DEFAULTS_ENABLE_SMART_DOWNLOAD = false
        const val SETTINGS_DEFAULTS_ENABLE_AUTO_UPDATE = false
        const val SETTINGS_DEFAULTS_NORMALIZE_VOLUME = false
        const val SETTINGS_DEFAULTS_MONO = false
        const val SETTINGS_DEFAULTS_GLOBAL_SHUFFLE = false
        const val SETTINGS_DEFAULTS_OFFLINE_MODE = false
        const val SETTINGS_DEFAULTS_DOWNLOADS_SD_CARD = false
        const val SETTINGS_DEFAULTS_SMART_DOWNLOADS = false
        const val SETTINGS_DEFAULTS_STREAMING_QUALITY = BITRATE_VERY_HIGH
        const val SETTINGS_DEFAULTS_THEME = ID_DARK
        const val SETTINGS_DEFAULTS_PLAYLIST_SORT = SORT_MODE_ASC // ascending is the default. if ascending do not change the list

        fun defaultSettings(username: String? = null) =
            LocalSettings(
                username = username ?: SETTINGS_DEFAULTS_USERNAME,
                theme = defaultTheme,
                enableRemoteLogging = SETTINGS_DEFAULTS_ENABLE_REMOTE_LOG,
                hideDonationButton = SETTINGS_DEFAULTS_HIDE_DONATION,
                enableAutoUpdates = SETTINGS_DEFAULTS_ENABLE_AUTO_UPDATE,
                streamingQuality = defaultBitrate,
                isNormalizeVolumeEnabled = SETTINGS_DEFAULTS_NORMALIZE_VOLUME,
                isMonoAudioEnabled = SETTINGS_DEFAULTS_MONO,
                isSmartDownloadsEnabled = SETTINGS_DEFAULTS_SMART_DOWNLOADS,
                isGlobalShuffleEnabled = SETTINGS_DEFAULTS_GLOBAL_SHUFFLE,
                isOfflineModeEnabled = SETTINGS_DEFAULTS_OFFLINE_MODE,
                playlistSongsSorting = defaultPlaylistSort,
                isDownloadsSdCard = SETTINGS_DEFAULTS_DOWNLOADS_SD_CARD
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

        sbThis.append(this.streamingQuality.bitrate)
        sbOthe.append(othe.streamingQuality.bitrate)

        sbThis.append(this.enableAutoUpdates)
        sbOthe.append(othe.enableAutoUpdates)

        sbThis.append(this.isNormalizeVolumeEnabled)
        sbOthe.append(othe.isNormalizeVolumeEnabled)

        sbThis.append(this.isMonoAudioEnabled)
        sbOthe.append(othe.isMonoAudioEnabled)

        sbThis.append(this.isSmartDownloadsEnabled)
        sbOthe.append(othe.isSmartDownloadsEnabled)

        sbThis.append(this.isGlobalShuffleEnabled)
        sbOthe.append(othe.isGlobalShuffleEnabled)

        sbThis.append(this.playlistSongsSorting)
        sbOthe.append(othe.playlistSongsSorting)

        sbThis.append(this.isOfflineModeEnabled)
        sbOthe.append(othe.isOfflineModeEnabled)

        sbThis.append(this.isDownloadsSdCard)
        sbOthe.append(othe.isDownloadsSdCard)

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

val streamQualityDropdownItems = listOf(
    StreamingQuality.VERY_HIGH.toPowerAmpacheDropdownItem(),
    StreamingQuality.HIGH.toPowerAmpacheDropdownItem(),
    StreamingQuality.MEDIUM.toPowerAmpacheDropdownItem(),
    StreamingQuality.MEDIUM_LOW.toPowerAmpacheDropdownItem(),
    StreamingQuality.LOW.toPowerAmpacheDropdownItem(),
)

/**
 * available themes
 */
@Parcelize
sealed class PowerAmpTheme(
    val themeId: String,
    @StringRes val title: Int,
    val isEnabled: Boolean
): Parcelable {
    @Parcelize data object SYSTEM:
        PowerAmpTheme(ID_SYSTEM, R.string.theme_system_title, true)
    @Parcelize data object DARK:
        PowerAmpTheme(ID_DARK, R.string.theme_dark_title, true)
    @Parcelize data object LIGHT:
        PowerAmpTheme(ID_LIGHT, R.string.theme_light_title, true)
    @Parcelize data object MATERIAL_YOU_SYSTEM:
        PowerAmpTheme(ID_MATERIAL_YOU_SYSTEM, R.string.theme_system_materialYou_title, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    @Parcelize data object MATERIAL_YOU_DARK:
        PowerAmpTheme(ID_MATERIAL_YOU_DARK, R.string.theme_dark_materialYou_title, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    @Parcelize data object MATERIAL_YOU_LIGHT:
        PowerAmpTheme(ID_MATERIAL_YOU_LIGHT, R.string.theme_light_materialYou_title, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

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

val themesDropDownItems = listOf(
    PowerAmpTheme.MATERIAL_YOU_SYSTEM.toPowerAmpacheDropdownItem(),
    PowerAmpTheme.MATERIAL_YOU_DARK.toPowerAmpacheDropdownItem(),
    PowerAmpTheme.MATERIAL_YOU_LIGHT.toPowerAmpacheDropdownItem(),
    PowerAmpTheme.SYSTEM.toPowerAmpacheDropdownItem(),
    PowerAmpTheme.DARK.toPowerAmpacheDropdownItem(),
    PowerAmpTheme.LIGHT.toPowerAmpacheDropdownItem(),
)

fun PowerAmpTheme.toPowerAmpacheDropdownItem() =
    PowerAmpacheDropdownItem(title = title, value = this, isEnabled = isEnabled)

fun StreamingQuality.toPowerAmpacheDropdownItem() =
    PowerAmpacheDropdownItem(title = title, subtitle = description, value = this)

enum class SortMode(mode: String) {
    ASC(SORT_MODE_ASC),
    DESC(SORT_MODE_DESC)
}
