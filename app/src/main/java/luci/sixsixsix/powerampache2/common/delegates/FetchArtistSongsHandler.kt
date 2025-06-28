package luci.sixsixsix.powerampache2.common.delegates

import luci.sixsixsix.powerampache2.domain.models.Song

interface FetchArtistSongsHandler {
    suspend fun getSongsFromArtist(
        artistId: String,
        isOfflineMode: Boolean,
        fetchRemote: Boolean = true,
        songsCallback: (List<Song>) -> Unit,
        loadingCallback: (Boolean) -> Unit = { },
        errorCallback: (Throwable?) -> Unit = { }
    )
}
