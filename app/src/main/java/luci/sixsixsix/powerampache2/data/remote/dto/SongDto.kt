package luci.sixsixsix.powerampache2.data.remote.dto

import com.google.gson.annotations.SerializedName
import luci.sixsixsix.powerampache2.domain.common.Constants.ERROR_FLOAT
import luci.sixsixsix.powerampache2.data.common.Constants.ERROR_INT
import luci.sixsixsix.powerampache2.domain.common.processArtUrl
import luci.sixsixsix.powerampache2.domain.common.processFlag
import luci.sixsixsix.powerampache2.domain.common.processNumberToFloat
import luci.sixsixsix.powerampache2.domain.common.processNumberToInt
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import luci.sixsixsix.powerampache2.domain.models.Song

data class SongDto(
    @SerializedName("album")
    val album: MusicAttributeDto?,

    @SerializedName("album_mbid")
    val albumMbid: String? = null,

    @SerializedName("albumartist")
    val albumartist: MusicAttributeDto?,

    @SerializedName("albumartist_mbid")
    val albumartistMbid: String? = null,

    @SerializedName("art")
    val art: String?,

    @SerializedName("artist")
    val artist: MusicAttributeDto?,

    @SerializedName("artist_mbid")
    val artistMbid: String? = null,

    @SerializedName("averagerating")
    val averagerating: Any? = null,

    @SerializedName("bitrate")
    val bitrate: Any?,

    @SerializedName("catalog")
    val catalog: Any?,

    @SerializedName("channels")
    val channels: Any?,

    @SerializedName("comment")
    val comment: String? = null,

    @SerializedName("composer")
    val composer: String?,

    @SerializedName("disk")
    val disk: Any? = null,

    @SerializedName("filename")
    val filename: String?,

    @SerializedName("flag")
    val flag: Any? = null, // TODO this can be boolean or integer from the server, find a solution!

    @SerializedName("genre")
    val genre: List<MusicAttributeDto>?,

    @SerializedName("id")
    val id: String,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("license")
    val license: Any? = null,

    @SerializedName("lyrics")
    val lyrics: String? = null,

    @SerializedName("mbid")
    val mbid: String? = null,

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
    val preciserating: Any? = null,

    @SerializedName("publisher")
    val publisher: String? = null,

    @SerializedName("r128_album_gain")
    val r128AlbumGain: Any,

    @SerializedName("r128_track_gain")
    val r128TrackGain: Any,

    @SerializedName("rate")
    val rate: Any?,

    @SerializedName("rating")
    val rating: Any? = null,

    @SerializedName("replaygain_album_gain")
    val replaygainAlbumGain: Any? = null,

    @SerializedName("replaygain_album_peak")
    val replaygainAlbumPeak: Any? = null,

    @SerializedName("replaygain_track_gain")
    val replaygainTrackGain: Any? = null,

    @SerializedName("replaygain_track_peak")
    val replaygainTrackPeak: Any? = null,

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

    @SerializedName("format")
    val format: String? = null,

    @SerializedName("stream_mime")
    val streamMime: String? = null,

    @SerializedName("disksubtitle")
    val diskSubtitle: String? = null,

    @SerializedName("stream_bitrate")
    val streamBitrate: Any? = null,

    @SerializedName("artists")
    val artists: List<MusicAttributeDto> = listOf(),

    @SerializedName("has_art")
    val hasArt: Any? = null
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
    imageUrl = processArtUrl(hasArt, art),
    bitrate = processNumberToInt(bitrate),
    streamBitrate = processNumberToInt(streamBitrate),
    catalog = processNumberToInt(catalog),
    channels = processNumberToInt(channels),
    composer = composer ?: "",
    filename = filename ?: "",
    genre = genre?.map { it.toMusicAttribute() } ?: listOf(),
    mime = mime ?: "",
    name = name ?: "",
    playCount = playcount ?: ERROR_INT,
    playlistTrackNumber = playlisttrack ?: ERROR_INT,
    rateHz = processNumberToInt(rate),
    size = size ?: ERROR_INT,
    time = time ?: ERROR_INT,
    trackNumber = track ?: ERROR_INT,
    year = year ?: ERROR_INT,
    mode = mode,
    artists = artists?.map { it.toMusicAttribute() } ?: listOf<MusicAttribute>(),
    flag = processFlag(flag),
    streamFormat = streamFormat,
    format = format,
    streamMime = streamMime,
    publisher = publisher,
    replayGainTrackGain = processNumberToFloat(replaygainTrackGain),
    replayGainTrackPeak = processNumberToFloat(replaygainTrackPeak),
    lyrics = lyrics ?: "",
    comment = comment ?: "",
    language = language ?: "",
    disk = processNumberToInt(disk),
    diskSubtitle = diskSubtitle ?: "",
    mbId = mbid ?: "",
    albumMbId = albumMbid ?: "",
    artistMbId = artistMbid ?: "",
    albumArtistMbId = albumartistMbid ?: "",
    rating = processNumberToFloat(rating) ?: ERROR_FLOAT,
    preciseRating = processNumberToFloat(preciserating) ?: ERROR_FLOAT,
    averageRating = processNumberToFloat(averagerating)
)
