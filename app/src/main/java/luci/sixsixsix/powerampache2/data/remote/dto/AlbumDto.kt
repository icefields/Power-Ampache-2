package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.common.processFlag
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute

data class AlbumDto(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("name")
    val name: String? = "",

    @SerializedName("prefix")
    val prefix: Any? = Any(),

    @SerializedName("basename")
    val basename: String?,

    @SerializedName("artist")
    val artist: MusicAttributeDto?,

    @SerializedName("artists")
    val artists: List<MusicAttributeDto>? = listOf(),

    @SerializedName("time")
    val time: Int? = 0,

    @SerializedName("year")
    val year: Int? = 0,

    @SerializedName("tracks")
    val tracks: List<Any>? = listOf(),

    @SerializedName("songcount")
    val songcount: Int? = 0,

    @SerializedName("diskcount")
    val diskcount: Int? = 0,

    @SerializedName("type")
    val type: Any? = Any(),

    @SerializedName("genre")
    val genre: List<MusicAttributeDto>? = listOf(),

    @SerializedName("art")
    val art: String? = "",

    @SerializedName("flag")
    val flag: Any? = null, // TODO this can be boolean or integer from the server, find a solution!

    @SerializedName("rating")
    val rating: Int? = 0,

    @SerializedName("averagerating")

    val averagerating: Float = 0.0f,

    @SerializedName("mbid")
    val mbid: Any? = Any()
)

data class AlbumsResponse(
    @SerializedName("album") val albums: List<AlbumDto>?,
) : AmpacheBaseResponse()

fun AlbumDto.toAlbum() = Album(
    id = id,
    name = name ?: "",
    basename = basename ?: "",
    artist = artist?.toMusicAttribute() ?: MusicAttribute.emptyInstance(),
    artists = artists?.map { it.toMusicAttribute() } ?: listOf(),
    genre = genre?.map { it.toMusicAttribute() } ?: listOf(),
    artUrl = art ?: "",
    songCount = songcount ?: 0,
    flag = processFlag(flag),
    time = time ?: 0,
    year = year ?: 0,
)
