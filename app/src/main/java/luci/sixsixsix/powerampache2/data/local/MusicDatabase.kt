package luci.sixsixsix.powerampache2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        AlbumEntity::class,
        SongEntity::class,
        ArtistEntity::class,
        PlaylistEntity::class,
        SessionEntity::class,
        CredentialsEntity::class],
    version = 19
)
@TypeConverters(Converters::class)
abstract class MusicDatabase: RoomDatabase() {
    abstract val dao: MusicDao
}
