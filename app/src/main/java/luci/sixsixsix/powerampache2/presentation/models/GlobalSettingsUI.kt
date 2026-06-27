/**
 * Copyright (C) 2026  Antonio Tari
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
package luci.sixsixsix.powerampache2.presentation.models

import android.net.Uri
import luci.sixsixsix.powerampache2.domain.models.settings.GlobalSettings

data class GlobalSettingsUI(
    val backBuffer: Int,
    val minBuffer: Int,
    val maxBuffer: Int,
    val bufferForPlayback: Int,
    val bufferForPlaybackAfterRebuffer: Int,
    val cacheSizeMb: Int,
    val useOkHttpExoplayer: Boolean,
    val prioritizeTimeOverSizeThresholds: Boolean,
    val targetBufferBytes: Int,
    val sleepTimerMins: Int,
    val sleepTimerEndTime: String?,
    val sleepTimerWaitSongEnd: Boolean,
    val customDownloadLocation: Uri?
)

fun GlobalSettings.toGlobalSettingsUI(
    sleepTimerEndTime: String?,
    sleepTimerMins: Int = 0
) = GlobalSettingsUI(
    backBuffer = backBuffer / 1000,
    minBuffer = minBufferMs / 1000,
    maxBuffer = maxBufferMs / 1000,
    bufferForPlayback = bufferForPlaybackMs / 1000,
    bufferForPlaybackAfterRebuffer = bufferForPlaybackAfterRebufferMs / 1000,
    useOkHttpExoplayer = useOkHttpForExoPlayer,
    cacheSizeMb = cacheSizeMb,
    prioritizeTimeOverSizeThresholds = prioritizeTimeOverSizeThresholds,
    targetBufferBytes = targetBufferBytes,
    sleepTimerEndTime = sleepTimerEndTime,
    sleepTimerMins = sleepTimerMins,
    sleepTimerWaitSongEnd = sleepTimerWaitSongEnd,
    customDownloadLocation = customDownloadRootUri
)
