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

import android.content.Context
import luci.sixsixsix.powerampache2.common.WeakContext
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY_SETTINGS_PREFERENCE = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE"
private const val KEY_BACK_BUFFER = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.backBuffer"
private const val KEY_MIN_BUFFER = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.minBufferMs"
private const val KEY_MAX_BUFFER = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.maxBufferMs"
private const val KEY_BUFFER_FOR_PLAYBACK = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.bufferForPlaybackMs"
private const val KEY_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE.bufferForPlaybackAfterRebufferMs"


// PLAYER BUFFERS
private const val BACK_BUFFER_MS = 30000
private const val BUFFER_MIN_MS = 60000
private const val BUFFER_MAX_MS = 120000
private const val BUFFER_FOR_PLAYBACK_MS = 30000
private const val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 35000

@Singleton
class SharedPreferencesManagerImpl @Inject constructor(
    private val weakContext: WeakContext,
): SharedPreferencesManager {

    private fun getSharedPreferences() =
        weakContext.get()?.getSharedPreferences(KEY_SETTINGS_PREFERENCE, Context.MODE_PRIVATE)

    private fun getInt(key: String, defaultValue: Int) = getSharedPreferences()?.let { sp ->
        sp.getInt(key, defaultValue)
    } ?: defaultValue

    private fun setInt(key: String, value: Int) = getSharedPreferences()?.edit()?.run {
        putInt(key, value)
        apply()
    } ?: Unit


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

    override fun resetBufferDefaults() {
        backBuffer = BACK_BUFFER_MS
        minBufferMs = BUFFER_MIN_MS
        maxBufferMs = BUFFER_MAX_MS
        bufferForPlaybackMs = BUFFER_FOR_PLAYBACK_MS
        bufferForPlaybackAfterRebufferMs = BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
    }
}
