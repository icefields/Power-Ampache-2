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

import android.content.Context
import android.net.Uri
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.data.common.SafFolderHelper
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.utils.SharedPreferencesManager
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

private const val BUFFER_SIZE = 4 * 1024
private const val SUB_DIR = "offline_music"

class StorageManagerImpl @Inject constructor(
    private val musicRepository: MusicRepository,
    @ApplicationContext private val context: Context,
    private val sharedPreferencesManager: SharedPreferencesManager
): StorageManager {
    private fun isStorageCustom(downloadRootUri: Uri?) = downloadRootUri.let {
        it != null && it.toString().isNotBlank() && it.toString() != getFilesDir() && it.toString() != getExternalFilesDir()
    }

    @Throws(Exception::class)
    override suspend fun saveSong(song: Song, inputStream: InputStream) =
        withContext(Dispatchers.IO) {
            val safFolderHelper = SafFolderHelper(context)
            val rootUri = sharedPreferencesManager.customDownloadRootUri
            return@withContext if (Constants.config.enableExternalDirDownloads
                && isStorageCustom(rootUri)) {
                safFolderHelper.writeFile(
                    rootUri = rootUri!!,
                    fullPath = getDirPathFromSong(song),
                    fileName = getFileNameFromSong(song),
                    mimeType = song.mime,
                    inputStream = inputStream,
                    bufferSize = BUFFER_SIZE
                ).toString()
            } else {
                val absoluteDirPath = getAbsolutePathDir(song)
                val directory = File(absoluteDirPath)
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val absolutePath = getAbsolutePathFile(song)!! // TODO fix double-bang!!
                try {
                    val fos = FileOutputStream(absolutePath)
                    fos.use { output ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var read: Int
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                        }
                        output.flush()
                    }
                    absolutePath
                } catch (e: Exception) {
                    throw e
                } finally {
                    inputStream.close()
                }
            }
        }

    @Throws(Exception::class)
    override suspend fun saveImage(song: Song, inputStream: InputStream) =
        withContext(Dispatchers.IO) {
            val absoluteDirPath = getAbsolutePathDir(song)
            val directory = File(absoluteDirPath)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val absolutePath = StringBuffer(absoluteDirPath).append("/").append(song.album.id).append(".png").toString()

            try {
                val fos = FileOutputStream(absolutePath)
                fos.use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                }
                return@withContext absolutePath
            } catch (e: Exception) {
                throw e
            } finally {
                inputStream.close()
            }
        }

    private fun getDirPathFromSong(song: Song): String {
        val fullPath = song.filename
        val lastSlashIndex = fullPath.lastIndexOf('/')
        return if (lastSlashIndex != -1) fullPath.take(lastSlashIndex) else ""
    }

    private fun getFileNameFromSong(song: Song): String {
        val fullPath = song.filename
        val lastSlashIndex = fullPath.lastIndexOf('/')
        val fileName = if (lastSlashIndex != -1) fullPath.substring(lastSlashIndex + 1) else fullPath
        return fileName
    }

    @Throws(Exception::class)
    override suspend fun deleteSong(song: Song): Boolean = withContext(Dispatchers.IO) {
        // try to delete from all storages
        val isSongDeletedFromStorage = deleteSongFromStorage(song, getFilesDir())
        if (!isSongDeletedFromStorage) {
            val isSongDeletedFromExternalStorage = deleteSongFromStorage(song, getExternalFilesDir())
            if (!isSongDeletedFromExternalStorage) {
                val rootUri = sharedPreferencesManager.customDownloadRootUri
                if (isStorageCustom(rootUri)) {
                    return@withContext SafFolderHelper(context).deleteFile(
                        rootUri = rootUri!!,
                        fullPath = getDirPathFromSong(song),
                        getFileNameFromSong(song)
                    )
                } else return@withContext false
            }
        }
        return@withContext isSongDeletedFromStorage
    }

    @Throws(Exception::class)
    suspend fun deleteSongFromStorage(song: Song, storage: String): Boolean = withContext(Dispatchers.IO) {
        val relativePath = song.filename
        val fileName = relativePath.substring(relativePath.lastIndexOf("/") + 1)
        val relativeDirectory = relativePath.replace(fileName, "")
        val pathBuilder = // TODO fix double-bang!!
            StringBuffer(storage)
                .append("/")
                .append(SUB_DIR)
                .append("/")
                .append(relativeDirectory)
        val absolutePath = pathBuilder.append("/").append(fileName).toString()
        L(absolutePath)
        val myFile = File(absolutePath)
        if (myFile.exists()) {
            myFile.delete()
            return@withContext true
        }
        return@withContext false
    }

    /**
     * Deletes all downloaded files
     *
     * @throws NullPointerException when context is null
     * @throws Exception when other types of exceptions are thrown
     */
    @Throws(Exception::class)
    override suspend fun deleteAll() = withContext(Dispatchers.IO) {
        deleteAllFromStorage(getFilesDir())
        deleteAllFromStorage(getExternalFilesDir())
    }

    @Throws(Exception::class)
    private suspend fun deleteAllFromStorage(storage: String) = withContext(Dispatchers.IO) {
        // TODO: also delete the non-selected storage.
        File(StringBuffer(storage)
            .append("/")
            .append(SUB_DIR)
            .append("/")
            .toString()
        ).deleteRecursively()
    }

    @Throws(Exception::class)
    suspend fun getAllSongsFromStorage(storage: String) = withContext(Dispatchers.IO) {
        val dir = File(
            StringBuffer(storage)
                .append("/")
                .append(SUB_DIR)
                .append("/")
                .toString()
        )

        val mp3Files: List<File> =
            dir.walkTopDown()
                .filter { it.isFile
                        && !it.extension.equals("png", ignoreCase = true)
                        && !it.extension.equals("jpg", ignoreCase = true)
                        && !it.extension.equals("jpeg", ignoreCase = true)
                        && !it.extension.equals("webp", ignoreCase = true)
                }
                .toList()

        return@withContext mp3Files
    }

    @Throws(Exception::class)
    override suspend fun getAllSongsFromInternalStorages() = withContext(Dispatchers.IO) {
        val mp3FilesExt: List<File> = getAllSongsFromStorage(getExternalFilesDir())
        val mp3Files: List<File> = getAllSongsFromStorage(getFilesDir())

        return@withContext ArrayList<File>().apply {
            addAll(mp3FilesExt)
            addAll(mp3Files)
        }
    }

    private suspend fun getAbsolutePathFile(song: Song): String? =
        getAbsolutePathDir(song = song)?.let { absoluteDirPath ->
            val relativePath = song.filename
            val fileName = relativePath.substring(relativePath.lastIndexOf("/") + 1)
            StringBuffer(absoluteDirPath).append("/").append(fileName).toString()
        }

    private suspend fun getAbsolutePathDir(song: Song): String? =
        musicRepository.getUsername()?.let { owner ->
            val relativePath = song.filename
            val fileName = relativePath.substring(relativePath.lastIndexOf("/") + 1)
            val relativeDirectory = relativePath.replace(fileName, "")

            StringBuffer(getStorage())
                .append("/")
                .append(SUB_DIR)
                .append("/")
                .append(owner)
                .append("/")
                .append(relativeDirectory)
                .toString()
        }

    private fun getFilesDir() = context.filesDir.absolutePath

    private fun getExternalFilesDir() =
        context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath ?: getFilesDir()


    suspend fun getStorage(): String = if (isDownloadsSdCard()) {
        getExternalFilesDir()
    } else {
        getFilesDir()
    }

    private suspend fun isDownloadsSdCard() = musicRepository.isDownloadsSdCard()
}
