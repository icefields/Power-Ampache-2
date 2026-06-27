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
package luci.sixsixsix.powerampache2.data.local.delegates

import android.content.Context
import luci.sixsixsix.powerampache2.domain.common.WeakContext
import luci.sixsixsix.powerampache2.domain.delegates.SharedPreferenceDelegate

private const val KEY_SETTINGS_PREFERENCE = "luci.sixsixsix.powerampache2.data.KEY_SETTINGS_PREFERENCE"

class SharedPreferenceDelegateImpl (
    private val weakContext: WeakContext
): SharedPreferenceDelegate {

    private fun getSharedPreferences() =
        weakContext.get()?.getSharedPreferences(KEY_SETTINGS_PREFERENCE, Context.MODE_PRIVATE)

    override fun getInt(key: String, defaultValue: Int) =
        getSharedPreferences()?.getInt(key, defaultValue) ?: defaultValue

    override fun setInt(key: String, value: Int) = getSharedPreferences()?.edit()?.run {
        putInt(key, value)
        apply()
    } ?: Unit

    override fun getString(key: String, defaultValue: String) =
        getSharedPreferences()?.getString(key, defaultValue) ?: defaultValue

    override fun setString(key: String, value: String) = getSharedPreferences()?.edit()?.run {
        putString(key, value)
        apply()
    } ?: Unit

    override fun getBool(key: String, defaultValue: Boolean) =
        getSharedPreferences()?.getBoolean(key, defaultValue) ?: defaultValue

    override fun setBool(key: String, value: Boolean) = getSharedPreferences()?.edit()?.run {
        putBoolean(key, value)
        apply()
    } ?: Unit
}
