package luci.sixsixsix.powerampache2.common.delegates

import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.usecase.artists.SongsFromArtistUseCase
import luci.sixsixsix.powerampache2.domain.usecase.songs.IsSongAvailableOfflineUseCase
import luci.sixsixsix.powerampache2.presentation.models.SongUI
import luci.sixsixsix.powerampache2.presentation.models.toSongUI

class FetchArtistSongsHandlerImpl(
    private val songsFromArtistUseCase: SongsFromArtistUseCase,
    private val isSongAvailableOfflineUseCase: IsSongAvailableOfflineUseCase,
): FetchArtistSongsHandler {
    override suspend fun getSongsFromArtist(
        artistId: String,
        isOfflineMode: Boolean,
        fetchRemote: Boolean,
        songsCallback: (List<SongUI>) -> Unit,
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
                                songsCallback(songs.toSongUI {
                                    isSongAvailableOfflineUseCase(it)
                                })
                            }
                        }
                    }
                    is Resource.Error -> errorCallback(result.exception)
                    is Resource.Loading -> loadingCallback(result.isLoading)
                }
            }

    }
}
