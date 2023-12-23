package luci.sixsixsix.powerampache2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import luci.sixsixsix.powerampache2.data.local.entities.AlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.ArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.CREDENTIALS_PRIMARY_KEY
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.SESSION_PRIMARY_KEY
import luci.sixsixsix.powerampache2.data.local.entities.SessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.SongEntity

@Dao
interface MusicDao {

// --- SESSION ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSession(companyListingEntities: SessionEntity)

    @Query("DELETE FROM sessionentity")
    suspend fun clearSession()

    @Query("""SELECT * FROM sessionentity WHERE primaryKey == '$SESSION_PRIMARY_KEY'""")
    suspend fun getSession(): SessionEntity?


// --- CREDENTIALS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCredentials(credentialsEntity: CredentialsEntity)

    @Query("DELETE FROM credentialsentity")
    suspend fun clearCredentials()

    @Query("""SELECT * FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'""")
    suspend fun getCredentials(): CredentialsEntity?


// --- ALBUMS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(companyListingEntities: List<AlbumEntity>)

    @Query("DELETE FROM albumentity")
    suspend fun clearAlbums()

    @Query("""SELECT * FROM albumentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR UPPER(:query) == basename""")
    suspend fun searchAlbum(query: String): List<AlbumEntity>


// --- SONGS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(companyListingEntities: List<SongEntity>)

    @Query("DELETE FROM songentity")
    suspend fun clearSongs()

    @Query("""SELECT * FROM songentity WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name order by flag, playCount""")
    suspend fun searchSong(query: String): List<SongEntity>

// --- ARTISTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(companyListingEntities: List<ArtistEntity>)

    @Query("DELETE FROM artistentity")
    suspend fun clearArtists()

    @Query("""SELECT * FROM artistentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name""")
    suspend fun searchArtist(query: String): List<ArtistEntity>

// --- PLAYLISTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(companyListingEntities: List<PlaylistEntity>)

    @Query("DELETE FROM playlistentity")
    suspend fun clearPlaylists()

    @Query("""SELECT * FROM playlistentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name""")
    suspend fun searchPlaylists(query: String): List<PlaylistEntity>
}
