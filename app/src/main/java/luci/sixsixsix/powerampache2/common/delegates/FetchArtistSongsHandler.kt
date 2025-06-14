package luci.sixsixsix.powerampache2.common.delegates

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.usecase.artists.SongsFromArtistUseCase

interface FetchArtistSongsHandler {
    suspend fun getSongsFromArtist(
        artistId: String,
        isOfflineMode: Boolean,
        fetchRemote: Boolean = true,
        songsCallback: (List<Song>) -> Unit,
        loadingCallback: (Boolean) -> Unit = {},
        errorCallback: (Throwable?) -> Unit = {}
    )
}

class FetchArtistSongsHandlerImpl constructor(
    private val songsFromArtistUseCase: SongsFromArtistUseCase,
): FetchArtistSongsHandler {
    override suspend fun getSongsFromArtist(
        artistId: String,
        isOfflineMode: Boolean,
        fetchRemote: Boolean,
        songsCallback: (List<Song>) -> Unit,
        loadingCallback: (Boolean) -> Unit,
        errorCallback: (Throwable?) -> Unit
    ) {
            songsFromArtistUseCase(artistId, fetchRemote = fetchRemote)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            if (result.networkData != null || isOfflineMode) {
                                // only get the data when a network response is returned
                                // check against network data but use db data.
                                // OR if in offline mode
                                result.data?.let { songs ->
                                    songsCallback(songs)
                                }
                            }
                        }
                        is Resource.Error -> errorCallback(result.exception)
                        is Resource.Loading -> loadingCallback(result.isLoading)
                    }
                }

    }
}
