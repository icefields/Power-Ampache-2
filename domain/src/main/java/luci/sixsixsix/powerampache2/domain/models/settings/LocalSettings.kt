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
package luci.sixsixsix.powerampache2.domain.models.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
    val sleepTimerMinutes: Int,
    val saveSongAfterPlayback: Boolean,
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
        const val SETTINGS_DEFAULTS_SAVE_AFTER_PLAY = false
        const val SETTINGS_DEFAULTS_SMART_DOWNLOADS = false
        const val SETTINGS_DEFAULTS_STREAMING_QUALITY = BITRATE_VERY_HIGH
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
                isDownloadsSdCard = SETTINGS_DEFAULTS_DOWNLOADS_SD_CARD,
                sleepTimerMinutes = 0,
                saveSongAfterPlayback = SETTINGS_DEFAULTS_SAVE_AFTER_PLAY
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

        sbThis.append(this.sleepTimerMinutes)
        sbOthe.append(othe.sleepTimerMinutes)

        sbThis.append(this.saveSongAfterPlayback)
        sbOthe.append(othe.saveSongAfterPlayback)

        return sbThis.toString() == sbOthe.toString()
    }
}
