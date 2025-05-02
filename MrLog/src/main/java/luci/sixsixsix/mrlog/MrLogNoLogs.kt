package luci.sixsixsix.mrlog

object MrLogNoLogs: MrLogI {
    override fun invoke(vararg messages: Any?) { /* Do Nothing */ }
    override fun e(vararg strings: Any?) { /* Do Nothing */ }
    override fun d(vararg strings: Any?) { /* Do Nothing */ }
    override fun w(vararg strings: Any?) { /* Do Nothing */ }
    override fun json(obj: Any?) { /* Do Nothing */ }
}