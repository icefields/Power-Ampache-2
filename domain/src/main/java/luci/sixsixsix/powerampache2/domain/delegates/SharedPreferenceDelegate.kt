package luci.sixsixsix.powerampache2.domain.delegates

interface SharedPreferenceDelegate {
    fun getString(key: String, defaultValue: String): String
    fun setString(key: String, value: String)
    fun getInt(key: String, defaultValue: Int): Int
    fun setInt(key: String, value: Int)
    fun getBool(key: String, defaultValue: Boolean): Boolean
    fun setBool(key: String, value: Boolean)
}
