package luci.sixsixsix.powerampache2.data.remote.dto


import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.domain.models.Artist

data class ArtistDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("albums")
    val albums: List<Any?>? = null,
    @SerializedName("albumcount")
    val albumcount: Int? = null,
    @SerializedName("songs")
    val songs: List<Any?>? = null,
    @SerializedName("songcount")
    val songcount: Int? = null,
    @SerializedName("genre")
    val genre: List<MusicAttributeDto>? = null,
    @SerializedName("art")
    val art: String? = null,
    @SerializedName("flag")
    val flag: Any? = null, // TODO this can be boolean or integer from the server, find a solution!
    @SerializedName("preciserating")
    val preciserating: Any? = null,
    @SerializedName("rating")
    val rating: Any? = null,
    @SerializedName("averagerating")
    val averagerating: Any? = null,
    @SerializedName("mbid")
    val mbid: Any? = null,
    @SerializedName("summary")
    val summary: Any? = null,
    @SerializedName("time")
    val time: Int? = null,
    @SerializedName("yearformed")
    val yearformed: Int? = null,
    @SerializedName("placeformed")
    val placeformed: Any? = null
)

data class ArtistsResponse(
    @SerializedName("artist") val artists: List<ArtistDto>?,
) : AmpacheBaseResponse()

fun ArtistDto.toArtist() = Artist(
    id = id,
    name = name ?: "ERROR no name",
    albumCount = albumcount ?: 0,
    songCount = songcount ?: 0,
    genre = genre?.map { it.toMusicAttribute() } ?: listOf(),
    artUrl = art ?: "ERROR no name",
//    flag = flag ?: false,
    summary = summary,
    time = time ?: 0,
    yearFormed = yearformed ?: 0,
    placeFormed = placeformed
)
