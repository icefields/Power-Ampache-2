package luci.sixsixsix.powerampache2.data.remote.dto


import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.common.processFlag
import luci.sixsixsix.powerampache2.domain.models.Playlist

data class PlaylistDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("owner")
    val owner: String? = null,
    @SerializedName("items")
    val items: Int? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("art")
    val art: String? = null,
    @SerializedName("flag")
    val flag: Any? = null, // TODO this can be boolean or integer from the server, find a solution!
    @SerializedName("preciserating")
    val preciserating: Float = 0.0f,
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("averagerating")
    val averagerating: Float = 0.0f,
)

data class PlaylistsResponse(
    @SerializedName("playlist") val playlist: List<PlaylistDto>?,
) : AmpacheBaseResponse()

fun PlaylistDto.toPlaylist() = Playlist(
    id = id,
    name = name ?: "ERROR no name",
    owner = owner ?: "ERROR no owner",
    artUrl = art ?: "",
    items = items ?: 0,
    type = type ?: "ERROR no type",
    flag = processFlag(flag),
    preciseRating = preciserating,
    rating = rating,
    averageRating = averagerating
)
