package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MusicAttributeDto (
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)
