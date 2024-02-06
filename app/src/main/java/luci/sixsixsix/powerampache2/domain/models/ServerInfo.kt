package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ServerInfo (
    var server: String? = null,
    var version: String? = null,
    var compatible: String? = null,
): Parcelable
