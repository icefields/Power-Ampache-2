package luci.sixsixsix.powerampache2.domain.usecase.plugin

import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import luci.sixsixsix.powerampache2.domain.models.Album
import javax.inject.Inject

class AlbumDataFromPluginUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository
) {
    suspend operator fun invoke(album: Album) =
        albumsRepository.getPluginAlbumData(
            albumId = album.id,
            albumMbId = "",
            albumTitle = album.name,
            artistName = album.artist.name
        )
}
