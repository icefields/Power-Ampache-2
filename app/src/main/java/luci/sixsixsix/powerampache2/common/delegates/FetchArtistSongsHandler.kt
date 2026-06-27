package luci.sixsixsix.powerampache2.common.delegates

import luci.sixsixsix.powerampache2.presentation.models.SongUI

interface FetchArtistSongsHandler {
    suspend fun getSongsFromArtist(
        artistId: String,
        isOfflineMode: Boolean,
        fetchRemote: Boolean = true,
        songsCallback: (List<SongUI>) -> Unit,
        loadingCallback: (Boolean) -> Unit = { },
        errorCallback: (Throwable?) -> Unit = { }
    )
}
