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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import luci.sixsixsix.powerampache2.data.local.entities.AlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.ArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.CREDENTIALS_PRIMARY_KEY
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.DownloadedSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.LocalSettingsEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.SESSION_PRIMARY_KEY
import luci.sixsixsix.powerampache2.data.local.entities.SessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.SongEntity
import luci.sixsixsix.powerampache2.data.local.entities.UserEntity

@Dao
interface MusicDao {

// --- SESSION ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSession(companyListingEntities: SessionEntity)

    @Query("DELETE FROM sessionentity")
    suspend fun clearSession()

    @Query("""SELECT * FROM sessionentity WHERE primaryKey == '$SESSION_PRIMARY_KEY'""")
    suspend fun getSession(): SessionEntity?

    @Query("""SELECT * FROM sessionentity WHERE primaryKey == '$SESSION_PRIMARY_KEY'""")
    fun getSessionLiveData(): LiveData<SessionEntity?>

// --- CREDENTIALS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCredentials(credentialsEntity: CredentialsEntity)

    @Query("DELETE FROM credentialsentity")
    suspend fun clearCredentials()

    @Query("""SELECT * FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'""")
    suspend fun getCredentials(): CredentialsEntity?

// --- USER ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(userEntity: UserEntity)

    @Query("DELETE FROM userentity")
    suspend fun clearUser()

    @Query("""SELECT * FROM userentity WHERE username = (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY') """)
    suspend fun getUser(): UserEntity?

    @Query("""SELECT * FROM userentity WHERE username = (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY') """)
    fun getUserLiveData(): LiveData<UserEntity?>

// --- ALBUMS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(companyListingEntities: List<AlbumEntity>)

    @Query("DELETE FROM albumentity")
    suspend fun clearAlbums()

    @Query("""SELECT * FROM albumentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name""")
    suspend fun searchAlbum(query: String): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE LOWER(artistId) == LOWER(:artistId) order by year DESC""")
    suspend fun getAlbumsFromArtist(artistId: String): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE LOWER(id) == LOWER(:albumId) order by time""")
    suspend fun getAlbum(albumId: String): AlbumEntity?

// --- SONGS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(companyListingEntities: List<SongEntity>)

    @Query("DELETE FROM songentity")
    suspend fun clearSongs()

    @Query("""SELECT * FROM songentity WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name order by playCount""")
    suspend fun searchSong(query: String): List<SongEntity>

    @Query("""SELECT * FROM songentity WHERE LOWER(:songId) == LOWER(mediaId)""")
    suspend fun getSongById(songId: String): SongEntity?

    @Query("""SELECT * FROM songentity WHERE LOWER(albumId) == LOWER(:albumId) order by trackNumber, playCount""")
    suspend fun getSongFromAlbum(albumId: String): List<SongEntity>

// --- ARTISTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Query("DELETE FROM artistentity")
    suspend fun clearArtists()

    @Query("""SELECT * FROM artistentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name order by name""")
    suspend fun searchArtist(query: String): List<ArtistEntity>

    @Query("""SELECT * FROM artistentity WHERE LOWER(id) == LOWER(:artistId) order by time""")
    suspend fun getArtist(artistId: String): ArtistEntity?

// --- PLAYLISTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(companyListingEntities: List<PlaylistEntity>)

    @Query("DELETE FROM playlistentity")
    suspend fun clearPlaylists()

    @Query("""SELECT * FROM playlistentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name order by rating DESC, id DESC""")
    suspend fun searchPlaylists(query: String): List<PlaylistEntity>

    // TODO get only playlists user owns
    @Query("""SELECT * FROM playlistentity WHERE owner = (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY') order by rating DESC, id DESC""")
    suspend fun getMyPlaylists(): List<PlaylistEntity>

    @Query("""SELECT * FROM playlistentity order by rating DESC, id DESC""")
    fun playlistsLiveData(): LiveData<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDownloadedSong(downloadedSongEntity: DownloadedSongEntity)

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(mediaId) == LOWER(:songId) AND LOWER(artistId) == LOWER(:artistId) AND LOWER(albumId) == LOWER(:albumId)""")
    suspend fun getDownloadedSong(songId: String, artistId: String, albumId: String): DownloadedSongEntity?

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(owner) == (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')""")
    fun getDownloadedSongsLiveData(): LiveData<List<DownloadedSongEntity>>

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(owner) == (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')""")
    suspend fun getOfflineSongs(): List<DownloadedSongEntity>

    @Query("DELETE FROM downloadedsongentity WHERE LOWER(mediaId) == LOWER(:songId)")
    suspend fun deleteDownloadedSong(songId: String)

    @Query("""SELECT * FROM localsettingsentity WHERE LOWER(username) == (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')""")
    suspend fun getSettings(): LocalSettingsEntity?

    @Query("""SELECT * FROM localsettingsentity WHERE LOWER(username) == (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')""")
    fun settingsLiveData(): LiveData<LocalSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun writeSettings(localSettingsEntity: LocalSettingsEntity)

    //@Query("""DELETE FROM playlistentity artistentity, songentity, albumentity""")
    suspend fun clearCachedData() {
        clearAlbums()
        clearArtists()
        clearSongs()
        clearPlaylists()
    }
}
