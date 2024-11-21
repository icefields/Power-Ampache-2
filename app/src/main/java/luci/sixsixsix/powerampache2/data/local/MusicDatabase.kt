/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.data.local.entities.AlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.ArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.DownloadedSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.GenreEntity
import luci.sixsixsix.powerampache2.data.local.entities.HistoryEntity
import luci.sixsixsix.powerampache2.data.local.entities.LocalSettingsEntity
import luci.sixsixsix.powerampache2.data.local.entities.MultiUserCredentialEntity
import luci.sixsixsix.powerampache2.data.local.entities.MultiUserEntity
import luci.sixsixsix.powerampache2.data.local.entities.MultiUserSessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistSongEntity
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
        DownloadedSongEntity::class,
        GenreEntity::class,
        PlaylistSongEntity::class,
        MultiUserSessionEntity::class,
        MultiUserEntity::class,
        MultiUserCredentialEntity::class,
        HistoryEntity::class
    ], version = Constants.DATABASE_VERSION, // first prod version: 73
    autoMigrations = [
        AutoMigration(from = 73, to = 74),
        AutoMigration(from = 74, to = 75),
        AutoMigration(from = 75, to = 76),
        AutoMigration(from = 76, to = 77),
        AutoMigration(from = 77, to = 78),

        AutoMigration(from = 78, to = 79),
        AutoMigration(from = 78, to = 80),

        AutoMigration(from = 79, to = 80),
        AutoMigration(from = 80, to = 81),
        AutoMigration(from = 81, to = 82),
        AutoMigration(from = 81, to = 83),

        AutoMigration(from = 82, to = 83),
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MusicDatabase: RoomDatabase() {
    abstract val dao: MusicDao
}

//fun MIGRATION_73_74() =
//    Migration(73, 74) { database: SupportSQLiteDatabase ->
//        // do nothing if not altering tables.
//        L.e("MIGRATING DATABASE")
//    }

