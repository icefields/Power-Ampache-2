package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.common.processFlag
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Song

data class SongDto(
    @SerializedName("album")
    val album: MusicAttributeDto?,

    @SerializedName("album_mbid")
    val albumMbid: Any,

    @SerializedName("albumartist")
    val albumartist: MusicAttributeDto?,

    @SerializedName("albumartist_mbid")
    val albumartistMbid: Any,

    @SerializedName("art")
    val art: String?,

    @SerializedName("artist")
    val artist: MusicAttributeDto?,

    @SerializedName("artist_mbid")
    val artistMbid: Any,

    @SerializedName("averagerating")
    val averagerating: Float = 0.0f,

    @SerializedName("bitrate")
    val bitrate: Int?,

    @SerializedName("catalog")
    val catalog: Int?,

    @SerializedName("channels")
    val channels: Int?,

    @SerializedName("comment")
    val comment: Any,

    @SerializedName("composer")
    val composer: String?,

    @SerializedName("disk")
    val disk: Int,

    @SerializedName("filename")
    val filename: String?,

    @SerializedName("flag")
    val flag: Any? = null, // TODO this can be boolean or integer from the server, find a solution!

    @SerializedName("genre")
    val genre: List<MusicAttributeDto>?,

    @SerializedName("id")
    val id: String,

    @SerializedName("language")
    val language: Any,

    @SerializedName("license")
    val license: Any,

    @SerializedName("lyrics")
    val lyrics: Any,

    @SerializedName("mbid")
    val mbid: Any,

    @SerializedName("mime")
    val mime: String?,

    @SerializedName("mode")
    val mode: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("playcount")
    val playcount: Int?,

    @SerializedName("playlisttrack")
    val playlisttrack: Int?,

    @SerializedName("preciserating")
    val preciserating: Any,

    @SerializedName("publisher")
    val publisher: String? = null,

    @SerializedName("r128_album_gain")
    val r128AlbumGain: Any,

    @SerializedName("r128_track_gain")
    val r128TrackGain: Any,

    @SerializedName("rate")
    val rate: Int?,

    @SerializedName("rating")
    val rating: Any,

    @SerializedName("replaygain_album_gain")
    val replaygainAlbumGain: Any,

    @SerializedName("replaygain_album_peak")
    val replaygainAlbumPeak: Any,

    @SerializedName("replaygain_track_gain")
    val replaygainTrackGain: Float? = null,

    @SerializedName("replaygain_track_peak")
    val replaygainTrackPeak: Float? = null,

    @SerializedName("size")
    val size: Int?,

    @SerializedName("time")
    val time: Int?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("track")
    val track: Int?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("year")
    val year: Int?,

    @SerializedName("stream_format")
    val streamFormat: String? = null,

    @SerializedName("stream_mime")
    val streamMime: String? = null,

    @SerializedName("artists")
    val artists: List<MusicAttributeDto> = listOf()
)

data class SongsResponse(
    @SerializedName("total_count") val totalCount: Int? = ERROR_INT,
    @SerializedName("song") val songs: List<SongDto>?,
) : AmpacheBaseResponse()

fun SongDto.toSong() = Song(
    mediaId = id,
    title = title ?: "",
    artist = artist?.toMusicAttribute() ?: MusicAttribute.emptyInstance(),
    album = album?.toMusicAttribute() ?: MusicAttribute.emptyInstance(),
    albumArtist = albumartist?.toMusicAttribute() ?: MusicAttribute.emptyInstance(),
    songUrl = url ?: "",
    imageUrl = art ?: "",
    bitrate = bitrate ?: ERROR_INT,
    catalog = catalog ?: ERROR_INT,
    channels = channels ?: ERROR_INT,
    composer = composer ?: "",
    filename = filename ?: "",
    genre = genre?.map { it.toMusicAttribute() } ?: listOf<MusicAttribute>(),
    mime = mime ?: "",
    name = name ?: "",
    playCount = playcount ?: ERROR_INT,
    playlistTrackNumber = playlisttrack ?: ERROR_INT,
    rate = rate ?: ERROR_INT,
    size = size ?: ERROR_INT,
    time = time ?: ERROR_INT,
    trackNumber = track ?: ERROR_INT,
    year = year ?: ERROR_INT,
    mode = mode,
    artists = artists?.map { it.toMusicAttribute() } ?: listOf<MusicAttribute>(),
    flag = processFlag(flag),
    streamFormat = streamFormat,
    streamMime = streamMime,
    publisher = publisher,
    replayGainTrackGain = replaygainTrackGain,
    replayGainTrackPeak = replaygainTrackPeak,
)
