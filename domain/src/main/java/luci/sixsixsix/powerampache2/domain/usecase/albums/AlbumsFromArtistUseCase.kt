package luci.sixsixsix.powerampache2.domain.usecase.albums

import luci.sixsixsix.powerampache2.domain.AlbumsRepository
import javax.inject.Inject


class AlbumsFromArtistUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository
) {
    suspend operator fun invoke(artistId: String, fetchRemote: Boolean = true) =
        albumsRepository.getAlbumsFromArtist(artistId, fetchRemote = fetchRemote)
}
