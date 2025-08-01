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
package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import luci.sixsixsix.powerampache2.data.local.delegates.SharedPreferenceDelegateImpl

import luci.sixsixsix.powerampache2.domain.common.WeakContext
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.common.Constants.BACK_BUFFER_MS
import luci.sixsixsix.powerampache2.domain.common.Constants.BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
import luci.sixsixsix.powerampache2.domain.common.Constants.BUFFER_FOR_PLAYBACK_MS
import luci.sixsixsix.powerampache2.domain.common.Constants.BUFFER_MAX_MS
import luci.sixsixsix.powerampache2.domain.common.Constants.BUFFER_MIN_MS
import luci.sixsixsix.powerampache2.domain.common.Constants.PLAYER_CACHE_SIZE_MB
import luci.sixsixsix.powerampache2.domain.delegates.SharedPreferenceDelegate
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY_BACK_BUFFER = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.backBuffer"
private const val KEY_MIN_BUFFER = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.minBufferMs"
private const val KEY_MAX_BUFFER = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.maxBufferMs"
private const val KEY_BUFFER_FOR_PLAYBACK = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.bufferForPlaybackMs"
private const val KEY_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.bufferForPlaybackAfterRebufferMs"
private const val KEY_PLAYER_CACHE_SIZE = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.playerCacheSize"
private const val KEY_ALLOW_ALL_CERTIFICATES = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.alloallcertificates"
private const val KEY_USE_OKHTTP_EXOPLAYER = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.useokhttpforexoplayer"
private const val KEY_INTRO_DIALOG_CONTENT = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.intro.dialog.content"

@Singleton
class SharedPreferencesManagerImpl @Inject constructor(
    private val weakContext: WeakContext,
): SharedPreferencesManager, SharedPreferenceDelegate by SharedPreferenceDelegateImpl(weakContext)  {

    // every time isAllowAllCertificates changes, this flow is triggered
    private val _isAllowAllCertificatesFlow = MutableStateFlow(isAllowAllCertificates)
    override val isAllowAllCertificatesFlow: StateFlow<Boolean> = _isAllowAllCertificatesFlow

    override var backBuffer: Int
        get() = getInt(KEY_BACK_BUFFER, BACK_BUFFER_MS)
        set(value) = setInt(KEY_BACK_BUFFER, value)

    override var minBufferMs: Int
        get() = getInt(KEY_MIN_BUFFER, BUFFER_MIN_MS)
        set(value) = setInt(KEY_MIN_BUFFER, value)


    override var maxBufferMs: Int
        get() = getInt(KEY_MAX_BUFFER, BUFFER_MAX_MS)
        set(value) = setInt(KEY_MAX_BUFFER, value)


    override var bufferForPlaybackMs: Int
        get() = getInt(KEY_BUFFER_FOR_PLAYBACK, BUFFER_FOR_PLAYBACK_MS)
        set(value) = setInt(KEY_BUFFER_FOR_PLAYBACK, value)


    override var bufferForPlaybackAfterRebufferMs: Int
        get() = getInt(KEY_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER, BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
        set(value) = setInt(KEY_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER, value)

    override var cacheSizeMb: Int
        get() = getInt(KEY_PLAYER_CACHE_SIZE, PLAYER_CACHE_SIZE_MB)
        set(value) = setInt(KEY_PLAYER_CACHE_SIZE, value)

    override var isAllowAllCertificates: Boolean
        get() = getBool(KEY_ALLOW_ALL_CERTIFICATES, false)
        set(value) = setBool(KEY_ALLOW_ALL_CERTIFICATES, value)
            .also { _isAllowAllCertificatesFlow.value = value }

    override var useOkHttpForExoPlayer: Boolean
        get() = getBool(KEY_USE_OKHTTP_EXOPLAYER, false)
        set(value) = setBool(KEY_USE_OKHTTP_EXOPLAYER, value)

    override var introDialogContent: String
        get() = getString(KEY_INTRO_DIALOG_CONTENT, "")
        set(value) = setString(KEY_INTRO_DIALOG_CONTENT, value)

    override fun shouldShowIntroDialog(newContent: String) =
        Constants.config.shouldShowIntroMessage && newContent != introDialogContent

    override fun resetBufferDefaults() {
        backBuffer = BACK_BUFFER_MS
        minBufferMs = BUFFER_MIN_MS
        maxBufferMs = BUFFER_MAX_MS
        bufferForPlaybackMs = BUFFER_FOR_PLAYBACK_MS
        bufferForPlaybackAfterRebufferMs = BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
        cacheSizeMb = PLAYER_CACHE_SIZE_MB
    }
}
