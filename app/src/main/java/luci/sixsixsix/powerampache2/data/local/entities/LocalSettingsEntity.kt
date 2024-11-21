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
package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.models.SortMode
import luci.sixsixsix.powerampache2.domain.models.StreamingQuality

@Entity
data class LocalSettingsEntity(
    @PrimaryKey
    val username: String,

    val theme: String,

    @ColumnInfo(name = "enableRemoteLogging", defaultValue = "${LocalSettings.SETTINGS_DEFAULTS_ENABLE_REMOTE_LOG}")
    val enableRemoteLogging: Boolean,

    @ColumnInfo(name = "hideDonationButton", defaultValue = "${LocalSettings.SETTINGS_DEFAULTS_HIDE_DONATION}")
    val hideDonationButton: Boolean,

    @ColumnInfo(name = "smartDownloadEnabled", defaultValue = "${LocalSettings.SETTINGS_DEFAULTS_ENABLE_SMART_DOWNLOAD}")
    val smartDownloadEnabled: Boolean,

    @ColumnInfo(name = "enableAutoUpdates", defaultValue = "${LocalSettings.SETTINGS_DEFAULTS_ENABLE_AUTO_UPDATE}")
    val enableAutoUpdates: Boolean,

    @ColumnInfo(name = "streamingQuality", defaultValue = "${LocalSettings.SETTINGS_DEFAULTS_STREAMING_QUALITY}")
    val streamingQuality: StreamingQuality,

    @ColumnInfo(name = "isNormalizeVolumeEnabled", defaultValue = "${LocalSettings.SETTINGS_DEFAULTS_NORMALIZE_VOLUME}")
    val isNormalizeVolumeEnabled: Boolean,

    @ColumnInfo(name = "isMonoAudioEnabled", defaultValue = "${LocalSettings.SETTINGS_DEFAULTS_MONO}")
    val isMonoAudioEnabled: Boolean,

    @ColumnInfo(name = "isGlobalShuffleEnabled", defaultValue = "${LocalSettings.SETTINGS_DEFAULTS_GLOBAL_SHUFFLE}")
    val isGlobalShuffleEnabled: Boolean,

    @ColumnInfo(name = "playlistSongsSorting", defaultValue = LocalSettings.SETTINGS_DEFAULTS_PLAYLIST_SORT)
    val playlistSongsSorting: SortMode,

    @ColumnInfo(name = "isOfflineModeEnabled", defaultValue = "${LocalSettings.SETTINGS_DEFAULTS_OFFLINE_MODE}")
    val isOfflineModeEnabled: Boolean,

    @ColumnInfo(name = "isDownloadsSdCard", defaultValue = "${LocalSettings.SETTINGS_DEFAULTS_DOWNLOADS_SD_CARD}")
    val isDownloadsSdCard: Boolean,
)

fun LocalSettingsEntity.toLocalSettings() = LocalSettings(
    username = username,
    theme = PowerAmpTheme.getThemeFromId(theme),
    enableRemoteLogging = enableRemoteLogging,
    hideDonationButton = hideDonationButton,
    streamingQuality = streamingQuality,
    enableAutoUpdates = enableAutoUpdates,
    isNormalizeVolumeEnabled = isNormalizeVolumeEnabled,
    isSmartDownloadsEnabled = smartDownloadEnabled,
    isMonoAudioEnabled = isMonoAudioEnabled,
    isGlobalShuffleEnabled = isGlobalShuffleEnabled,
    isOfflineModeEnabled = isOfflineModeEnabled,
    playlistSongsSorting = playlistSongsSorting,
    isDownloadsSdCard = isDownloadsSdCard
)

fun LocalSettings.toLocalSettingsEntity() = LocalSettingsEntity(
    username = username,
    theme = theme.themeId,
    enableRemoteLogging = enableRemoteLogging,
    hideDonationButton = hideDonationButton,
    smartDownloadEnabled = isSmartDownloadsEnabled,
    streamingQuality = streamingQuality,
    enableAutoUpdates = enableAutoUpdates,
    isMonoAudioEnabled = isMonoAudioEnabled,
    isNormalizeVolumeEnabled = isNormalizeVolumeEnabled,
    isGlobalShuffleEnabled = isGlobalShuffleEnabled,
    isOfflineModeEnabled = isOfflineModeEnabled,
    playlistSongsSorting = playlistSongsSorting,
    isDownloadsSdCard = isDownloadsSdCard
)
