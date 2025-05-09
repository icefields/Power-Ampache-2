package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName

abstract class AmpacheBaseResponse(
    @SerializedName("error")
    val error: ErrorDto? = null
)
