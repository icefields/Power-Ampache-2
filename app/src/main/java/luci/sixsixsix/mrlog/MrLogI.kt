package luci.sixsixsix.mrlog

interface MrLogI {
    operator fun invoke(vararg messages: Any?)
    fun e(vararg strings: Any?)
    fun d(vararg strings: Any?)
    fun w(vararg strings: Any?)
    fun json(obj: Any?)
}