package luci.sixsixsix.powerampache2.presentation.screens_detail.album_detail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.player.MusicPlaylistManager
import luci.sixsixsix.powerampache2.presentation.common.SongWrapper
import javax.inject.Inject
import kotlin.math.abs

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle, // a way to get access to navigation arguments
    // in the view model directly without passing them from the UI or the previos view model, we
    // need this because we're passing the symbol around
    private val songsRepository: SongsRepository,
    private val albumsRepository: AlbumsRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val playlistManager: MusicPlaylistManager,
) : ViewModel() {
    //var state by mutableStateOf(AlbumDetailState())
    var state by savedStateHandle.saveable {
        mutableStateOf(AlbumDetailState())
    }

    init {
        val album = savedStateHandle.get<Album>("album")?.also {
            state = state.copy(album = it)
        }

        savedStateHandle.get<String>("albumId")?.let {
            L("albumId", it)
            if (!it.isNullOrBlank()) {
                getSongsFromAlbum(it)
            }
            if (album == null) {
                getAlbumInfo(it)
            }
        }

        viewModelScope.launch {
            playlistManager.downloadedSongFlow.collect {
                if(it != null) {
                    L("RefreshFromCache")
                    onEvent(AlbumDetailEvent.RefreshFromCache)
                }
            }
        }
    }

    fun onEvent(event: AlbumDetailEvent) {
        when (event) {
            is AlbumDetailEvent.Fetch ->
                getSongsFromAlbum(albumId = event.albumId, fetchRemote = true)
            is AlbumDetailEvent.OnSongSelected ->
                // play the selected song and add the rest of the album to the queue
                playlistManager.updateTopSong(event.song)
            is AlbumDetailEvent.OnPlayAlbum -> {
                L("AlbumDetailViewModel.AlbumDetailEvent.OnPlayAlbum")
                playlistManager.updateCurrentSong(state.songs[0].song)
                playlistManager.addToCurrentQueueTop(getSongs())
            }
            AlbumDetailEvent.OnShareAlbum -> {}
            AlbumDetailEvent.OnShuffleAlbum -> {
                val shuffled = getSongs().shuffled()
                playlistManager.addToCurrentQueueNext(shuffled)
                playlistManager.moveToSongInQueue(shuffled[0])
            }
            AlbumDetailEvent.OnFavouriteAlbum ->
                favouriteAlbum()

            AlbumDetailEvent.RefreshFromCache ->
                getSongsFromAlbum(albumId = state.album.id, fetchRemote = false)
        }
    }

    private fun favouriteAlbum(albumId: String = state.album.id) = viewModelScope.launch {
        playlistsRepository.likeAlbum(albumId, (state.album.flag != 1))
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            // refresh album
                            state = state.copy(
                                album = state.album.copy(flag = abs(state.album.flag - 1))
                            )
                        }
                    }
                    is Resource.Error -> state = state.copy(isLikeLoading = false)
                    is Resource.Loading -> state = state.copy(isLikeLoading = result.isLoading)
                }
            }
    }


    private fun getAlbumInfo(albumId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            albumsRepository
                .getAlbum(albumId, fetchRemote)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            // TODO why am I using network data here? please comment
                            result.networkData?.let { album ->
                                state = state.copy(album = album)
                                L("AlbumDetailViewModel.getAlbumInfo size ${result.data} network: ${result.networkData}")
                            }
                        }
                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }

    fun getSongs() = state.songs.map { it.song }

    private fun getSongsFromAlbum(albumId: String, fetchRemote: Boolean = true) {
        viewModelScope.launch {
            songsRepository
                .getSongsFromAlbum(albumId, fetchRemote)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { songs ->
                                val songWrapperList = mutableListOf<SongWrapper>()
                                songs.forEach { song ->
                                    songWrapperList.add(SongWrapper(
                                        song = song,
                                        isOffline = !songsRepository.getSongUri(song).startsWith("http")
                                        )
                                    )
                                }
                                state = state.copy(songs = songWrapperList)
                                L("AlbumDetailViewModel.getSongsFromAlbum size", result.data?.size, "network", result.networkData?.size)
                            }
                        }

                        is Resource.Error -> state = state.copy(isLoading = false)
                        is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    }
                }
        }
    }
}
