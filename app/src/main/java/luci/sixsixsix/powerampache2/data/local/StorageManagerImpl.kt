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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.WeakContext
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

private const val BUFFER_SIZE = 4 * 1024
private const val SUB_DIR = "offline_music"

class StorageManagerImpl @Inject constructor(
    private val weakContext: WeakContext,
    private val musicRepository: MusicRepository,
): StorageManager {
    @Throws(Exception::class)
    override suspend fun saveSong(song: Song, inputStream: InputStream) =
        withContext(Dispatchers.IO) {
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
                return@withContext absolutePath
            } catch (e: Exception) {
                throw e
            } finally {
                inputStream.close()
            }
        }

    @Throws(Exception::class)
    override suspend fun deleteSong(song: Song): Boolean = withContext(Dispatchers.IO) {
        val relativePath = song.filename
        val fileName = relativePath.substring(relativePath.lastIndexOf("/") + 1)
        val relativeDirectory = relativePath.replace(fileName, "")
        val pathBuilder = // TODO fix double-bang!!
            StringBuffer(getStorage())
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
        File(StringBuffer(getStorage())
            .append("/")
            .append(SUB_DIR)
            .append("/")
            .toString()
        ).deleteRecursively()
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

    private suspend fun getStorage() = musicRepository.getStorageLocation(weakContext.get()!!)
}
