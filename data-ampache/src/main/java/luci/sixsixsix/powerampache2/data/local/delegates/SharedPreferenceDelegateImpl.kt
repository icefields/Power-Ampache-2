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

    override fun getInt(key: String, defaultValue: Int) = getSharedPreferences()?.let { sp ->
        sp.getInt(key, defaultValue)
    } ?: defaultValue

    override fun setInt(key: String, value: Int) = getSharedPreferences()?.edit()?.run {
        putInt(key, value)
        apply()
    } ?: Unit

    override fun getString(key: String, defaultValue: String) = getSharedPreferences()?.let { sp ->
        sp.getString(key, defaultValue)
    } ?: defaultValue

    override fun setString(key: String, value: String) = getSharedPreferences()?.edit()?.run {
        putString(key, value)
        apply()
    } ?: Unit

    override fun getBool(key: String, defaultValue: Boolean) = getSharedPreferences()?.let { sp ->
        sp.getBoolean(key, defaultValue)
    } ?: defaultValue

    override fun setBool(key: String, value: Boolean) = getSharedPreferences()?.edit()?.run {
        putBoolean(key, value)
        apply()
    } ?: Unit
}
