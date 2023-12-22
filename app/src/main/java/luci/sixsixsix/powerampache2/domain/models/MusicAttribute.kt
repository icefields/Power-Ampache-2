package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicAttribute(
    val id: String,
    val name: String
): Parcelable {
    companion object {
        fun emptyInstance(): MusicAttribute = MusicAttribute("","")
    }
}
