package luci.sixsixsix.powerampache2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import luci.sixsixsix.powerampache2.data.local.entities.AlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.ArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.SessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.SongEntity

@Database(
    entities = [
        AlbumEntity::class,
        SongEntity::class,
        ArtistEntity::class,
        PlaylistEntity::class,
        SessionEntity::class,
        CredentialsEntity::class],
    version = 33
)
@TypeConverters(Converters::class)
abstract class MusicDatabase: RoomDatabase() {
    abstract val dao: MusicDao
}
