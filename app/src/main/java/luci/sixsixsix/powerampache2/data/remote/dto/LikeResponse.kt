package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LikeResponse(
    @SerializedName("success") val success:Any? = null,
    @SerializedName("error") val error:Any? = null
)
