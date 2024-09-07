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
import androidx.room.Delete
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
import luci.sixsixsix.powerampache2.data.local.entities.HistoryEntity
import luci.sixsixsix.powerampache2.data.local.entities.LocalSettingsEntity
import luci.sixsixsix.powerampache2.data.local.entities.MultiUserCredentialEntity
import luci.sixsixsix.powerampache2.data.local.entities.MultiUserEntity
import luci.sixsixsix.powerampache2.data.local.entities.MultiUserSessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistEntity
import luci.sixsixsix.powerampache2.data.local.entities.PlaylistSongEntity
import luci.sixsixsix.powerampache2.data.local.entities.SESSION_PRIMARY_KEY
import luci.sixsixsix.powerampache2.data.local.entities.SessionEntity
import luci.sixsixsix.powerampache2.data.local.entities.SongEntity
import luci.sixsixsix.powerampache2.data.local.entities.UserEntity
import luci.sixsixsix.powerampache2.data.local.models.SongUrl

private const val multiUserCondition = " LOWER(multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) "

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
    fun getSessionLiveData(): Flow<SessionEntity?>

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

    @Query("""SELECT * FROM userentity WHERE $multiUserCondition""")
    suspend fun getUser(): UserEntity?

    @Query("""SELECT * FROM userentity WHERE LOWER(username) = LOWER(:username) AND $multiUserCondition""")
    suspend fun getUser(username: String): UserEntity?

    @Query("""SELECT * FROM userentity WHERE $multiUserCondition""")
    fun getUserLiveData(): Flow<UserEntity?>

// --- ALBUMS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Query("DELETE FROM albumentity")
    suspend fun clearAlbums()

    @Query("DELETE FROM albumentity WHERE LOWER(artistId) == LOWER(:artistId)")
    suspend fun deleteAlbumsFromArtist(artistId: String)

    @Query("DELETE FROM albumentity WHERE LOWER(id) == LOWER(:albumId) AND $multiUserCondition")
    suspend fun deleteAlbum(albumId: String)

    @Query("""SELECT * FROM albumentity WHERE 
        (LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name OR LOWER(artistName) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == LOWER(artistName))
        AND $multiUserCondition""")
    suspend fun searchAlbum(query: String): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE (LOWER(artistId) == LOWER(:artistId) OR LOWER(artists) LIKE '%' || '"' || LOWER(:artistId) || '"' || '%' )
        AND $multiUserCondition 
        ORDER BY year DESC""")
    suspend fun getAlbumsFromArtist(artistId: String): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE LOWER(id) == LOWER(:albumId) order by time""")
    suspend fun getAlbum(albumId: String): AlbumEntity?

    @Query("""SELECT * FROM albumentity WHERE LOWER(id) == LOWER(:albumId) order by time""")
    fun getAlbumFlow(albumId: String): Flow<AlbumEntity?>

    @Query("""SELECT * FROM albumentity WHERE year > 1000 order by year DESC LIMIT 66""")
    suspend fun getRecentlyReleasedAlbums(): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE flag == 1 AND $multiUserCondition ORDER BY RANDOM() LIMIT 222""")
    suspend fun getLikedAlbums(): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE flag == 1 AND $multiUserCondition ORDER BY RANDOM() LIMIT 222""")
    fun getLikedAlbumsFlow(): Flow<List<AlbumEntity>>

    @Query("""SELECT * FROM albumentity WHERE rating > 0 AND $multiUserCondition order by rating DESC""")
    suspend fun getHighestRatedAlbums(): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE rating > 0 AND $multiUserCondition order by rating DESC""")
    fun getHighestRatedAlbumsFlow(): Flow<List<AlbumEntity>>

    @Query("""SELECT * FROM albumentity WHERE $multiUserCondition ORDER BY RANDOM() LIMIT 66""")
    suspend fun getRandomAlbums(): List<AlbumEntity>

    @Query("""SELECT * FROM albumentity WHERE $multiUserCondition ORDER BY RANDOM() LIMIT 22""")
    fun getRandomAlbumsFlow(): Flow<List<AlbumEntity>>

    @Query("""SELECT SUM(playCount) AS acount, a.* FROM songentity AS s, albumentity AS a 
        WHERE a.id == s.albumId 
		AND LOWER(s.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) 
        GROUP BY s.albumId ORDER BY acount DESC LIMIT 122""")
    suspend fun getMostPlayedAlbums(): List<AlbumEntity>

    @Query("""SELECT SUM(playCount) AS acount, a.* FROM songentity AS s, albumentity AS a 
        WHERE a.id == s.albumId 
		AND LOWER(s.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) 
        GROUP BY s.albumId ORDER BY acount DESC LIMIT 122""")
    fun getMostPlayedAlbumsFlow(): Flow<List<AlbumEntity>>


    @Query("""SELECT SUM(history.playCount) AS acount, album.*, history.*, song.* FROM historyentity as history, downloadedsongentity as song , albumentity AS album
            WHERE history.mediaId == song.mediaId
			AND album.id == song.albumId
			AND LOWER(album.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == 'power-ampache-2-credentials')) 
            GROUP BY album.id
            ORDER BY history.playCount DESC LIMIT 666""")
    fun getMostPlayedOfflineAlbumsFlow(): Flow<List<AlbumEntity>>


// --- SONGS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(companyListingEntities: List<SongEntity>)

    @Query("DELETE FROM songentity")
    suspend fun clearSongs()

    @Query("""SELECT * FROM songentity 
            WHERE (LOWER(title) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name OR LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(artistName) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == artistName OR LOWER(albumName) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == albumName)
            AND $multiUserCondition
            order by (LOWER(title) LIKE '%' || LOWER(:query) || '%') DESC, flag DESC, rating DESC, playCount DESC LIMIT 666""")
    suspend fun searchSong(query: String): List<SongEntity>

    @Query("""SELECT * FROM songentity WHERE playCount > 0 AND $multiUserCondition order by playCount DESC, flag DESC, rating DESC""")
    suspend fun getMostPlayedSongs(): List<SongEntity>

    @Query("""SELECT history.*, song.* FROM historyentity as history, SongEntity as song 
            WHERE LOWER(history.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) 
            AND song.mediaId == history.mediaId
            GROUP BY song.mediaId
            ORDER BY history.playCount DESC LIMIT 666""")
    suspend fun getMostPlayedSongsLocal(): List<SongEntity>

    @Query("""SELECT * FROM songentity WHERE LOWER(:songId) == LOWER(mediaId)""")
    suspend fun getSongById(songId: String): SongEntity?

    @Query("""SELECT * FROM songentity WHERE LOWER(albumId) == LOWER(:albumId) order by trackNumber, flag DESC, rating DESC, playCount DESC""")
    suspend fun getSongFromAlbum(albumId: String): List<SongEntity>

// --- ARTISTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Query("DELETE FROM artistentity")
    suspend fun clearArtists()

    @Query("""SELECT * FROM artistentity WHERE (LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name) AND $multiUserCondition order by name""")
    suspend fun searchArtist(query: String): List<ArtistEntity>

    @Query("""SELECT * FROM artistentity WHERE LOWER(genre) LIKE '%' || LOWER(:genre) || '%' OR LOWER(:genre) == LOWER(genre) order by name""")
    suspend fun searchArtistByGenre(genre: String): List<ArtistEntity>

    @Query("""SELECT * FROM songentity WHERE LOWER(genre) LIKE '%' || LOWER(:genre) || '%' OR LOWER(:genre) == LOWER(genre)""")
    suspend fun searchSongByGenre(genre: String): List<SongEntity>

    @Query("""SELECT * FROM artistentity WHERE LOWER(id) == LOWER(:artistId) order by time DESC""")
    suspend fun getArtist(artistId: String): ArtistEntity?

    @Query("""SELECT SUM(playCount) AS acount, a.* FROM songentity AS s, artistentity AS a 
        WHERE a.id == s.artistId 
        AND LOWER(a.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) 
        GROUP BY s.artistId ORDER BY acount DESC LIMIT 20""")
    suspend fun getMostPlayedArtists(): List<ArtistEntity>

// --- PLAYLISTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(companyListingEntities: List<PlaylistEntity>)

    @Query("""SELECT song.*, songIds.position FROM songentity as song, 
        (SELECT * FROM playlistsongentity WHERE LOWER(:playlistId) == LOWER(playlistId)) as songIds 
        WHERE LOWER(song.mediaId) == LOWER(songIds.songId) 
        AND LOWER(song.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))
        GROUP BY LOWER(song.mediaId) ORDER BY songIds.position""")
    suspend fun getSongsFromPlaylist(playlistId: String): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSongs(companyListingEntities: List<PlaylistSongEntity>)

    @Query("DELETE FROM playlistentity WHERE $multiUserCondition")
    suspend fun clearPlaylists()

    @Query("DELETE FROM playlistentity WHERE LOWER(id) == LOWER(:playlistId) AND $multiUserCondition")
    suspend fun deletePlaylist(playlistId: String)

    @Delete
    suspend fun deletePlaylists(playlists: List<PlaylistEntity>)

    @Query("""DELETE FROM playlistentity WHERE $multiUserCondition AND LOWER(owner) != LOWER('admin') AND
            LOWER(owner) != LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    suspend fun deleteNonUserAdminPlaylist()

    @Query("DELETE FROM playlistsongentity WHERE $multiUserCondition")
    suspend fun clearPlaylistSongs()

    @Query("DELETE FROM playlistsongentity WHERE LOWER(playlistId) == LOWER(:playlistId) AND $multiUserCondition")
    suspend fun clearPlaylistSongs(playlistId: String)

    @Query("DELETE FROM playlistsongentity WHERE LOWER(songId) == LOWER(:songId) AND LOWER(playlistId) == LOWER(:playlistId) AND $multiUserCondition")
    suspend fun deleteSongFromPlaylist(playlistId: String, songId: String)

    @Query("""SELECT * FROM playlistentity WHERE (LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name)
        AND $multiUserCondition
        GROUP BY id
        ORDER BY flag DESC, rating DESC, (LOWER(owner) == LOWER( (SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) ) DESC, id DESC""")
    suspend fun searchPlaylists(query: String): List<PlaylistEntity>

    @Query("""SELECT * FROM playlistentity WHERE $multiUserCondition order by flag DESC, rating DESC, (LOWER(owner) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) ) DESC, id DESC""")
    fun playlistsLiveData(): LiveData<List<PlaylistEntity>>

    @Query("""SELECT * FROM playlistentity WHERE $multiUserCondition order by flag DESC, rating DESC, id DESC""")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("""SELECT * FROM playlistentity WHERE id == :playlistId AND $multiUserCondition""")
    fun playlistLiveData(playlistId: String): Flow<PlaylistEntity?>

    // get only playlists user owns
    @Query("""SELECT * FROM playlistentity WHERE LOWER(owner) = LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) order by rating DESC, id DESC""")
    suspend fun getMyPlaylists(): List<PlaylistEntity>

    @Query("""SELECT * FROM playlistentity WHERE LOWER(owner) = LOWER('admin') order by rating DESC, id DESC""")
    suspend fun getAdminPlaylists(): List<PlaylistEntity>

// --- OFFLINE SONGS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDownloadedSong(downloadedSongEntity: DownloadedSongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDownloadedSongs(downloadedSongEntities: List<DownloadedSongEntity>)

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(mediaId) == LOWER(:songId) AND LOWER(artistId) == LOWER(:artistId) AND LOWER(albumId) == LOWER(:albumId) AND $multiUserCondition""")
    suspend fun getDownloadedSong(songId: String, artistId: String, albumId: String): DownloadedSongEntity?

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(mediaId) == LOWER(:songId) AND $multiUserCondition""")
    suspend fun getDownloadedSongById(songId: String): DownloadedSongEntity?

    @Query("""SELECT * FROM downloadedsongentity WHERE $multiUserCondition""")
    fun getDownloadedSongsLiveData(): LiveData<List<DownloadedSongEntity>>

    @Query("""SELECT * FROM downloadedsongentity WHERE $multiUserCondition""")
    suspend fun getOfflineSongs(): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE LOWER(:albumId) == LOWER(albumId) AND $multiUserCondition""")
    suspend fun getOfflineSongsFromAlbum(albumId: String): List<DownloadedSongEntity>

    @Query("""SELECT  song.*, songIds.position FROM downloadedsongentity as song, (SELECT * FROM playlistsongentity WHERE :playlistId == playlistId) as songIds WHERE song.mediaId == songIds.songId""")
    suspend fun getOfflineSongsFromPlaylist(playlistId: String): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE 
        (LOWER(title) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == name OR LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(artistName) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == artistName OR LOWER(albumName) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == albumName) 
        AND $multiUserCondition""")
    suspend fun searchOfflineSongs(query: String): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE year > 1000 AND $multiUserCondition order by year DESC LIMIT 66""")
    suspend fun getRecentlyReleasedOfflineSongs(): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE flag == 1 AND $multiUserCondition LIMIT 222""")
    suspend fun getLikedOfflineSongs(): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE rating > 0 AND $multiUserCondition order by rating DESC""")
    suspend fun getHighestRatedOfflineSongs(): List<DownloadedSongEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE $multiUserCondition ORDER BY RANDOM() LIMIT 222""")
    suspend fun getRandomOfflineSongs(): List<DownloadedSongEntity>

    @Query("""SELECT history.*, song.* FROM historyentity as history, DownloadedSongEntity as song 
            WHERE LOWER(history.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY')) 
            AND song.mediaId == history.mediaId
            GROUP BY song.mediaId
            ORDER BY history.playCount DESC LIMIT 666""")
    suspend fun getMostPlayedOfflineSongs(): List<DownloadedSongEntity>

    @Query("""SELECT album.* FROM DownloadedSongEntity as song, (SELECT * FROM AlbumEntity) as album WHERE song.albumId == album.id GROUP BY album.id ORDER BY album.name""")
    suspend fun getOfflineAlbums(): List<AlbumEntity>

    @Query("""SELECT  artist.* FROM DownloadedSongEntity as song, (SELECT * FROM ArtistEntity ) as artist WHERE song.artistId == artist.id GROUP BY artist.id ORDER BY artist.name""")
    suspend fun getOfflineArtists(): List<ArtistEntity>

    /**
     * generates artist entities using only the info available in DownloadedSongEntity
     */
    @Query("""SELECT count(artistId) as songCount, artistId as id, artistName as name, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as yearFormed, multiUserId FROM DownloadedSongEntity as song WHERE LOWER(owner) == LOWER(:owner) GROUP BY artistId ORDER BY artistName""")
    suspend fun generateOfflineArtists(owner: String): List<ArtistEntity>

    /**
     * generates album entities using only the info available in DownloadedSongEntity
     */
    @Query("""SELECT count(albumId) as songCount, albumId as id, multiUserId, albumName as name, albumName as basename, artistId, '{"attr":[' || albumArtist ||']}' as artists, artistName, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as year, -1 as diskCount, 0 as rating, 0 as averageRating FROM DownloadedSongEntity as song WHERE LOWER(owner) == LOWER(:owner) GROUP BY albumId ORDER BY albumName""")
    suspend fun generateOfflineAlbums(owner: String): List<AlbumEntity>

    @Query("""SELECT count(albumId) as songCount, albumId as id, albumName as name, multiUserId, albumName as basename, artistId, '{"attr":[' || albumArtist ||']}' as artists, artistName, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as year, -1 as diskCount, 0 as rating, 0 as averageRating FROM DownloadedSongEntity as song WHERE artistId = LOWER(:artistId) GROUP BY albumId ORDER BY albumName""")
    suspend fun getOfflineAlbumsByArtist(artistId: String): List<AlbumEntity>

    @Query("""SELECT count(albumId) as songCount, albumId as id, albumName as name, multiUserId, albumName as basename, artistId, artistName, '{"attr":[' || albumArtist ||']}' as artists, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as year, -1 as diskCount, 0 as rating, 0 as averageRating FROM DownloadedSongEntity as song WHERE :albumId == id GROUP BY albumId""")
    suspend fun generateOfflineAlbum(albumId: String): AlbumEntity?

    @Query("""SELECT count(albumId) as songCount, albumId as id, albumName as name, multiUserId, albumName as basename, artistId, artistName, '{"attr":[' || albumArtist ||']}' as artists, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as year, -1 as diskCount, 0 as rating, 0 as averageRating FROM DownloadedSongEntity as song WHERE :albumId == id GROUP BY albumId""")
    fun generateOfflineAlbumFlow(albumId: String): Flow<AlbumEntity?>

    @Query("""SELECT count(artistId) as songCount, artistId as id, artistName as name, multiUserId, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as yearFormed FROM DownloadedSongEntity as song WHERE LOWER(id) == LOWER(:artistId) GROUP BY artistId""")
    suspend fun generateOfflineArtist(artistId: String): ArtistEntity?

    @Query("""SELECT SUM(playCount) AS acount, artist.* FROM songentity AS song, 
              (
                SELECT count(artistId) as songCount, artistId as id, multiUserId, artistName as name, genre, imageUrl as artUrl, -1 as 'time', 0 as flag, -1 as albumCount, -1 as yearFormed FROM DownloadedSongEntity as song 
                WHERE LOWER(owner) == (SELECT username FROM credentialsentity WHERE primaryKey == 'power-ampache-2-credentials')
                GROUP BY artistId ORDER BY artistName
              ) AS artist
            WHERE artist.id == song.artistId GROUP BY song.artistId ORDER BY acount DESC""")
    suspend fun getMostPlayedOfflineArtists(): List<ArtistEntity>

    @Query("""SELECT * FROM downloadedsongentity WHERE $multiUserCondition ORDER BY RANDOM() LIMIT 66""")
    fun getRandomOfflineSongsFlow(): Flow<List<DownloadedSongEntity>>

    @Query("DELETE FROM downloadedsongentity WHERE LOWER(mediaId) == LOWER(:songId)")
    suspend fun deleteDownloadedSong(songId: String)

    @Query("DELETE FROM downloadedsongentity")
    suspend fun deleteAllDownloadedSong()

// --- SETTINGS ---

    @Query("""SELECT * FROM localsettingsentity WHERE LOWER(username) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    suspend fun getSettings(): LocalSettingsEntity?

    @Query("""SELECT * FROM localsettingsentity WHERE LOWER(username) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    fun settingsLiveData(): LiveData<LocalSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun writeSettings(localSettingsEntity: LocalSettingsEntity)

    @Query("""SELECT isOfflineModeEnabled FROM localsettingsentity WHERE LOWER(username) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    fun offlineModeEnabled(): Flow<Boolean?>

    @Query("""SELECT isOfflineModeEnabled FROM localsettingsentity WHERE LOWER(username) == LOWER((SELECT username FROM credentialsentity WHERE primaryKey == '$CREDENTIALS_PRIMARY_KEY'))""")
    fun isOfflineModeEnabled(): Boolean?

// --- GENRES ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Query("""SELECT * FROM genreentity WHERE $multiUserCondition""")
    suspend fun getGenres(): List<GenreEntity>

    @Query("""SELECT * FROM genreentity WHERE LOWER(id) == LOWER(:genreId) AND $multiUserCondition""")
    suspend fun getGenreById(genreId: String): GenreEntity?

    @Query("""SELECT * FROM genreentity WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(:query) == LOWER(name) AND $multiUserCondition""")
    suspend fun searchGenres(query: String): List<GenreEntity>

    @Query("DELETE FROM genreentity")
    suspend fun clearGenres()


// --- MULTIUSER ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiUserSession(session: MultiUserSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiUserUser(user: MultiUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiUserCredentials(credentials: MultiUserCredentialEntity)

    @Query("DELETE FROM multiuserentity WHERE primaryKey == :multiUserKey")
    suspend fun deleteMultiUserUser(multiUserKey: String)

    @Query("DELETE FROM multiusercredentialentity WHERE primaryKey == :multiUserKey")
    suspend fun deleteMultiUserCredentials(multiUserKey: String)

    @Query("DELETE FROM multiusersessionentity WHERE primaryKey == :multiUserKey")
    suspend fun deleteMultiUserSession(multiUserKey: String)

    @Query("""SELECT * FROM multiuserentity WHERE LOWER(:multiUserKey) == LOWER(primaryKey)""")
    suspend fun getMultiUserUser(multiUserKey: String): MultiUserEntity?

    @Query("""SELECT * FROM multiusercredentialentity WHERE LOWER(:multiUserKey) == LOWER(primaryKey)""")
    suspend fun getMultiUserCredentials(multiUserKey: String): MultiUserCredentialEntity?

    @Query("""SELECT * FROM multiusersessionentity WHERE LOWER(:multiUserKey) == LOWER(primaryKey)""")
    suspend fun getMultiUserSession(multiUserKey: String): MultiUserSessionEntity?

    @Query("""SELECT * FROM multiuserentity""")
    suspend fun getAllMultiUserUsers(): List<MultiUserEntity>

// --- HISTORY ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToHistory(historyEntity: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongsToHistory(historyEntities: List<HistoryEntity>)

    @Query("""SELECT history.*, song.* FROM historyentity as history, songentity as song 
            WHERE LOWER(history.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == 'power-ampache-2-credentials'))
            AND song.mediaId == history.mediaId
			AND song.mediaId == LOWER(:id)
            GROUP BY history.mediaId
            ORDER BY lastPlayed DESC LIMIT 666""")
    suspend fun getSongFromHistory(id: String): SongEntity?

    @Query("""SELECT history.*, song.* FROM historyentity as history, songentity as song 
            WHERE LOWER(history.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == 'power-ampache-2-credentials')) 
            AND song.mediaId == history.mediaId
            GROUP BY history.mediaId
            ORDER BY lastPlayed DESC LIMIT 666""")
    suspend fun getSongHistory(): List<SongEntity>

    @Query("""SELECT history.*, song.* FROM historyentity as history, songentity as song 
            WHERE LOWER(history.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == 'power-ampache-2-credentials')) 
            AND song.mediaId == history.mediaId
            GROUP BY history.mediaId
            ORDER BY lastPlayed DESC LIMIT 666""")
    fun getSongHistoryFlow(): Flow<List<SongEntity>>

    @Query("""SELECT history.*, song.* FROM historyentity as history, downloadedsongentity as song 
            WHERE LOWER(history.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == 'power-ampache-2-credentials')) 
            AND song.mediaId == history.mediaId
            GROUP BY history.mediaId
            ORDER BY lastPlayed DESC LIMIT 666""")
    suspend fun getOfflineSongHistory(): List<DownloadedSongEntity>

    @Query("""SELECT history.*, song.* FROM historyentity as history, downloadedsongentity as song 
            WHERE LOWER(history.multiUserId) == LOWER((SELECT multiUserId FROM credentialsentity WHERE primaryKey == 'power-ampache-2-credentials')) 
            AND song.mediaId == history.mediaId
            GROUP BY history.mediaId
            ORDER BY lastPlayed DESC LIMIT 666""")
    fun getOfflineSongHistoryFlow(): Flow<List<DownloadedSongEntity>>

    @Query("DELETE FROM historyentity")
    suspend fun clearHistory()

    suspend fun clearCachedData() {
        clearAlbums()
        clearArtists()
        clearSongs()
        clearPlaylists()
        clearPlaylistSongs()
        clearGenres()
        clearHistory()
    }

    suspend fun clearCachedLibraryData() {
        clearAlbums()
        clearArtists()
        clearSongs()
    }
}
