package luci.sixsixsix.powerampache2.data.remote

import luci.sixsixsix.powerampache2.data.entities.Song

interface MusicDatabase {
    suspend fun getAllSongs(): List<Song>
}
