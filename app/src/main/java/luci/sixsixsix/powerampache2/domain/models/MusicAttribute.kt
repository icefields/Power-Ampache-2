package luci.sixsixsix.powerampache2.domain.models

data class MusicAttribute(
    val id: String,
    val name: String
) {
    companion object {
        fun emptyInstance(): MusicAttribute = MusicAttribute("","")
    }
}
