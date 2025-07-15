package luci.sixsixsix.powerampache2.domain.usecase.plugin

import luci.sixsixsix.powerampache2.domain.SongsRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import javax.inject.Inject

class SongDataFromPluginUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(song: Song) =
        songsRepository.getPluginSongData(
            songId = song.mediaId,
            songMbId = song.mbId,
            songTitle = song.title,
            albumTitle = song.album.name,
            artistName = song.artist.name
        )
}
