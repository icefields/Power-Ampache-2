package luci.sixsixsix.powerampache2.data.remote.dto


import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.domain.models.Genre

data class GenreDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("albums")
    val albums: Int? = null,
    @SerializedName("artists")
    val artists: Int? = null,
    @SerializedName("songs")
    val songs: Int? = null,
    @SerializedName("videos")
    val videos: Int? = null,
    @SerializedName("playlists")
    val playlists: Int? = null,
    @SerializedName("live_streams")
    val liveStreams: Int? = null
)

data class GenresResponse(
    @SerializedName("genre") val genres: List<GenreDto>?,
) : AmpacheBaseResponse()

fun GenreDto.toGenre() = Genre(
    id = id,
    name = name,
    albums = albums ?: ERROR_INT,
    artists = artists ?: ERROR_INT,
    songs = songs ?: ERROR_INT,
    playlists = playlists ?: ERROR_INT
)
