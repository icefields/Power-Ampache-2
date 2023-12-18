package luci.sixsixsix.powerampache2.common

import luci.sixsixsix.powerampache2.data.entities.Song

object Constants {
    const val SONG_COLLECTION = "songs"
    const val MEDIA_ROOT_ID = "root_id"
    const val NETWORK_ERROR = "NETWORK_ERROR"
    const val UPDATE_PLAYER_POSITION_INTERVAL = 100L
    const val NOTIFICATION_CHANNEL_ID = "music"
    const val NOTIFICATION_ID = 1

    val mockSongs: List<Song> = listOf(
        Song(mediaId = "",
            title = "",
            subtitle = "",
            songUrl = "",
            imageUrl = ""),
        Song(mediaId = "",
            title = "",
            subtitle = "",
            songUrl = "",
            imageUrl = ""),
        Song(mediaId = "",
            title = "",
            subtitle = "",
            songUrl = "",
            imageUrl = ""),
        Song(mediaId = "",
            title = "",
            subtitle = "",
            songUrl = "",
            imageUrl = ""),
        Song(mediaId = "",
            title = "",
            subtitle = "",
            songUrl = "",
            imageUrl = ""),
        Song(mediaId = "",
            title = "",
            subtitle = "",
            songUrl = "",
            imageUrl = ""),
        Song(mediaId = "",
            title = "",
            subtitle = "",
            songUrl = "",
            imageUrl = "")
    )
}
