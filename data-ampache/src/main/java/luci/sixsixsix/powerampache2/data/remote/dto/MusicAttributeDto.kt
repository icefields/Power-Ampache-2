package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute

data class MusicAttributeDto (
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)

fun MusicAttributeDto.toMusicAttribute() = MusicAttribute(
    id = id,
    name = name
)
