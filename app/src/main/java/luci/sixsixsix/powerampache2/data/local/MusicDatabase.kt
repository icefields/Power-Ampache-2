package luci.sixsixsix.powerampache2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.data.local.entities.AlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.ArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.DownloadedSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.LocalSettingsEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.SessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.SongEntity
import luci.sixsixsix.powerampache2.data.local.entities.UserEntity

@Database(
    entities = [
        AlbumEntity::class,
        SongEntity::class,
        ArtistEntity::class,
        PlaylistEntity::class,
        SessionEntity::class,
        CredentialsEntity::class,
        UserEntity::class,
        LocalSettingsEntity::class,
        DownloadedSongEntity::class
    ], version = 73
)
@TypeConverters(Converters::class)
abstract class MusicDatabase: RoomDatabase() {
    abstract val dao: MusicDao
}

fun MIGRATION_73_74() =
    Migration(73, 74) { database: SupportSQLiteDatabase ->
        // do nothing if not altering tables.
        L.e("MIGRATING DATABASE")
    }
