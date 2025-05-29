package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SuccessResponse(
    @SerializedName("success") val success: Any? = null
): AmpacheBaseResponse()
