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
import kotlinx.coroutines.flow.Flow
import luci.sixsixsix.powerampache2.data.local.entities.AlbumEntity
import luci.sixsixsix.powerampache2.data.local.entities.ArtistEntity
import luci.sixsixsix.powerampache2.data.local.entities.CREDENTIALS_PRIMARY_KEY
import luci.sixsixsix.powerampache2.data.local.entities.CredentialsEntity
import luci.sixsixsix.powerampache2.data.local.entities.DownloadedSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.GenreEntity
import luci.sixsixsix.powerampache2.data.local.entities.LocalSettingsEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.SESSION_PRIMARY_KEY
import luci.sixsixsix.powerampache2.data.local.entities.SessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.SongEntity
import luci.sixsixsix.powerampache2.data.local.entities.UserEntity
import luci.sixsixsix.powerampache2.data.local.models.SongUrl

@Dao
interface MusicDao {
    @Query("""SELECT session.auth AS authToken, credentials.serverUrl, settings.streamingQuality as bitrate, credentials.username as user FROM
        (SELECT * FROM sessionentity WHERE primaryKey == '$SESSION_PRIMARY_KEY') AS session, 
        (SELECT * FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY') AS credentials,
        (SELECT * FROM localsettingsentity WHERE LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) == LOWER(username)) AS settings""")
    suspend fun getSongUrlData(): SongUrl?

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

//    @Query("""SELECT * FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'""")
//    fun getCredentialsLiveData(): LiveData<CredentialsEntity?>

// --- USER ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(userEntity: UserEntity)

    @Query("DELETE FROM userentity")
    suspend fun clearUser()

    @Query("""SELECT * FROM userentity WHERE LOWER(username) = LOWER((SELECT username FROM credentialsentity WHERE primaryKey = '$CREDENTIALS_PRIMARY_KEY')) """)
    suspend fun getUser(): UserEntity?

    @Query("""SELECT * FROM userentity WHERE LOWER(username) = LOWER(:username)""")
    suspend fun getUser(username: String): UserEntity?

    //@Query("""SELECT credentials.username as credentialsUsername, user.* FROM
    //    (SELECT * FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY') AS credentials,
    //    (SELECT * FROM userentity WHERE username = (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) AS user""")
    @Query("""SELECT * FROM userentity WHERE LOWER(username) = LOWER((SELECT username FROM credentialsentity WHERE primaryKey = '$CREDENTIALS_PRIMARY_KEY')) """)
    fun getUserLiveData(): Flow<UserEntity?>

    //    @Query("""SELECT * FROM userentity WHERE username = (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY') """)
    //    fun getUserLiveDataOld(): LiveData<UserEntity?>

// --- ALBUMS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Query("DELETE FROM albumentity")
    suspend fun clearAlbums()

    @Query("DELETE FROM albumentity WHERE LOWER(artistId) == LOWER(:artistId)")
    suspend fun deleteAlbumsFromArtist(artistId: String)

    @Query("""SELECT * FROM albumentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name OR LOWER(artistName) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == LOWER(artistName)""")
    suspend fun searchAlbum(query: String): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE LOWER(artistId) == LOWER(:artistId) order by year DESC""")
    suspend fun getAlbumsFromArtist(artistId: String): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE LOWER(id) == LOWER(:albumId) order by time""")
    suspend fun getAlbum(albumId: String): AlbumEntity?

    @Query("""SELECT * FROM albumentity WHERE year > 1000 order by year DESC LIMIT 66""")
    suspend fun getRecentlyReleasedAlbums(): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE flag == 1 LIMIT 222""")
    suspend fun getLikedAlbums(): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE rating > 0 order by rating DESC""")
    suspend fun getHighestRatedAlbums(): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity ORDER BY RANDOM() LIMIT 66""")
    suspend fun getRandomAlbums(): List<AlbumEntity>

    @Query("""SELECT SUM(playCount) AS acount, a.* FROM songentity AS s, albumentity AS a WHERE a.id == s.albumId GROUP BY s.albumId ORDER BY acount DESC LIMIT 122""")
    suspend fun getMostPlayedAlbums(): List<AlbumEntity>

// --- SONGS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(companyListingEntities: List<SongEntity>)

    @Query("DELETE FROM songentity")
    suspend fun clearSongs()

    @Query("""SELECT * FROM songentity WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name OR LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(artistName) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == artistName OR LOWER(albumName) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == albumName order by flag DESC, rating DESC, playCount DESC""")
    suspend fun searchSong(query: String): List<SongEntity>

    @Query("""SELECT * FROM songentity WHERE playCount > 0 order by playCount DESC, flag DESC, rating DESC""")
    suspend fun getMostPlayedSongs(): List<SongEntity>

    @Query("""SELECT * FROM songentity WHERE LOWER(:songId) == LOWER(mediaId)""")
    suspend fun getSongById(songId: String): SongEntity?

    @Query("""SELECT * FROM songentity WHERE LOWER(albumId) == LOWER(:albumId) order by trackNumber, flag DESC, rating DESC, playCount DESC""")
    suspend fun getSongFromAlbum(albumId: String): List<SongEntity>

// --- ARTISTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Query("DELETE FROM artistentity")
    suspend fun clearArtists()

    @Query("""SELECT * FROM artistentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name order by name""")
    suspend fun searchArtist(query: String): List<ArtistEntity>

    @Query("""SELECT * FROM artistentity WHERE LOWER(genre) LIKE '%' || LOWER(:genre) || '%' OR LOWER(:genre) == LOWER(genre) order by name""")
    suspend fun searchArtistByGenre(genre: String): List<ArtistEntity>

    @Query("""SELECT * FROM songentity WHERE LOWER(genre) LIKE '%' || LOWER(:genre) || '%' OR LOWER(:genre) == LOWER(genre)""")
    suspend fun searchSongByGenre(genre: String): List<SongEntity>

    @Query("""SELECT * FROM artistentity WHERE LOWER(id) == LOWER(:artistId) order by time DESC""")
    suspend fun getArtist(artistId: String): ArtistEntity?

// --- PLAYLISTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(companyListingEntities: List<PlaylistEntity>)

    @Query("""SELECT song.*, songIds.position FROM songentity as song, (SELECT * FROM playlistsongentity WHERE LOWER(:playlistId) == LOWER(playlistId)) as songIds WHERE LOWER(song.mediaId) == LOWER(songIds.songId) ORDER BY songIds.position""")
    suspend fun getSongsFromPlaylist(playlistId: String): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSongs(companyListingEntities: List<PlaylistSongEntity>)

    @Query("DELETE FROM playlistentity")
    suspend fun clearPlaylists()

    @Query("DELETE FROM playlistsongentity")
    suspend fun clearPlaylistSongs()

    @Query("DELETE FROM playlistsongentity WHERE LOWER(playlistId) == LOWER(:playlistId)")
    suspend fun clearPlaylistSongs(playlistId: String)

    @Query("""SELECT * FROM playlistentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name order by flag DESC, rating DESC, (LOWER(owner) == LOWER( (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) ) DESC, id DESC""")
    suspend fun searchPlaylists(query: String): List<PlaylistEntity>

    @Query("""SELECT * FROM playlistentity order by flag DESC, rating DESC, (LOWER(owner) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) ) DESC, id DESC""")
    fun playlistsLiveData(): LiveData<List<PlaylistEntity>>

    @Query("""SELECT * FROM playlistentity order by flag DESC, rating DESC, id DESC""")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("""SELECT * FROM playlistentity WHERE id == :playlistId""")
    fun playlistLiveData(playlistId: String): LiveData<PlaylistEntity?>

    // get only playlists user owns
    @Query("""SELECT * FROM playlistentity WHERE LOWER(owner) = LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) order by rating DESC, id DESC""")
    suspend fun getMyPlaylists(): List<PlaylistEntity>

// --- OFFLINE SONGS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDownloadedSong(downloadedSongEntity: DownloadedSongEntity)

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(mediaId) == LOWER(:songId) AND LOWER(artistId) == LOWER(:artistId) AND LOWER(albumId) == LOWER(:albumId)""")
    suspend fun getDownloadedSong(songId: String, artistId: String, albumId: String): DownloadedSongEntity?

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(mediaId) == LOWER(:songId) AND LOWER(owner) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    suspend fun getDownloadedSongById(songId: String): DownloadedSongEntity?

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(owner) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    fun getDownloadedSongsLiveData(): LiveData<List<DownloadedSongEntity>>

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(owner) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    suspend fun getOfflineSongs(): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(:albumId) == LOWER(albumId)""")
    suspend fun getOfflineSongsFromAlbum(albumId: String): List<DownloadedSongEntity>

    @Query("""SELECT  song.*,  songIds.position FROM downloadedsongentity as song, (SELECT * FROM playlistsongentity WHERE :playlistId == playlistId  ) as songIds WHERE song.mediaId == songIds.songId""")
    suspend fun getOfflineSongsFromPlaylist(playlistId: String): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name OR LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(artistName) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == artistName OR LOWER(albumName) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == albumName AND LOWER(owner) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    suspend fun searchOfflineSongs(query: String): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE year > 1000 order by year DESC LIMIT 66""")
    suspend fun getRecentlyReleasedOfflineSongs(): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE flag == 1 LIMIT 222""")
    suspend fun getLikedOfflineSongs(): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE rating > 0 order by rating DESC""")
    suspend fun getHighestRatedOfflineSongs(): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity ORDER BY RANDOM() LIMIT 166""")
    suspend fun getRandomOfflineSongs(): List<DownloadedSongEntity>

    // TODO missing playCount field in offline table
    suspend fun getMostPlayedOfflineSongs(): List<DownloadedSongEntity> = getRandomOfflineSongs()

    @Query("""SELECT album.* FROM DownloadedSongEntity as song, (SELECT * FROM AlbumEntity) as album WHERE song.albumId == album.id GROUP BY album.id ORDER BY album.name""")
    suspend fun getOfflineAlbums(): List<AlbumEntity>

    @Query("""SELECT  artist.* FROM DownloadedSongEntity as song, (SELECT * FROM ArtistEntity ) as artist WHERE song.artistId == artist.id GROUP BY artist.id ORDER BY artist.name""")
    suspend fun getOfflineArtists(): List<ArtistEntity>

    /**
     * generates artist entities using only the info available in DownloadedSongEntity
     */
    @Query("""SELECT count(artistId) as songCount, artistId as id, artistName as name, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as yearFormed FROM DownloadedSongEntity as song GROUP BY artistId ORDER BY artistName""")
    suspend fun generateOfflineArtists(): List<ArtistEntity>

    /**
     * generates album entities using only the info available in DownloadedSongEntity
     */
    @Query("""SELECT count(albumId) as songCount, albumId as id, albumName as name, albumName as basename, artistId, '{"attr":[' || albumArtist ||']}' as artists, artistName, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as year, -1 as diskCount, 0 as rating, 0 as averageRating FROM DownloadedSongEntity as song GROUP BY albumId ORDER BY albumName""")
    suspend fun generateOfflineAlbums(): List<AlbumEntity>

    @Query("""SELECT count(albumId) as songCount, albumId as id, albumName as name, albumName as basename, artistId, '{"attr":[' || albumArtist ||']}' as artists, artistName, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as year, -1 as diskCount, 0 as rating, 0 as averageRating FROM DownloadedSongEntity as song WHERE artistId = LOWER(:artistId) GROUP BY albumId ORDER BY albumName""")
    suspend fun getOfflineAlbumsByArtist(artistId: String): List<AlbumEntity>

    @Query("""SELECT count(albumId) as songCount, albumId as id, albumName as name, albumName as basename, artistId, artistName, '{"attr":[' || albumArtist ||']}' as artists, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as year, -1 as diskCount, 0 as rating, 0 as averageRating FROM DownloadedSongEntity as song WHERE :albumId == id GROUP BY albumId""")
    suspend fun generateOfflineAlbum(albumId: String): AlbumEntity?

    @Query("""SELECT count(artistId) as songCount, artistId as id, artistName as name, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as yearFormed FROM DownloadedSongEntity as song WHERE LOWER(id) == LOWER(:artistId) GROUP BY artistId""")
    suspend fun generateOfflineArtist(artistId: String): ArtistEntity?

    @Query("DELETE FROM downloadedsongentity WHERE LOWER(mediaId) == LOWER(:songId)")
    suspend fun deleteDownloadedSong(songId: String)

    @Query("DELETE FROM downloadedsongentity")
    suspend fun deleteAllDownloadedSong()

    @Query("""SELECT * FROM localsettingsentity WHERE LOWER(username) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    suspend fun getSettings(): LocalSettingsEntity?

    @Query("""SELECT * FROM localsettingsentity WHERE LOWER(username) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    fun settingsLiveData(): LiveData<LocalSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun writeSettings(localSettingsEntity: LocalSettingsEntity)

    @Query("""SELECT isOfflineModeEnabled FROM localsettingsentity WHERE LOWER(username) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    fun offlineModeEnabled(): LiveData<Boolean?>

    @Query("""SELECT isOfflineModeEnabled FROM localsettingsentity WHERE LOWER(username) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    fun isOfflineModeEnabled(): Boolean?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Query("""SELECT * FROM genreentity""")
    suspend fun getGenres(): List<GenreEntity>

    @Query("""SELECT * FROM genreentity WHERE LOWER(id) == LOWER(:genreId)""")
    suspend fun getGenreById(genreId: String): GenreEntity?

    @Query("""SELECT * FROM genreentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == LOWER(name)""")
    suspend fun searchGenres(query: String): List<GenreEntity>

    @Query("DELETE FROM genreentity")
    suspend fun clearGenres()

    //@Query("""DELETE FROM playlistentity artistentity, songentity, albumentity""")
    suspend fun clearCachedData() {
        clearAlbums()
        clearArtists()
        clearSongs()
        clearPlaylists()
        clearPlaylistSongs()
        clearGenres()
    }
}
