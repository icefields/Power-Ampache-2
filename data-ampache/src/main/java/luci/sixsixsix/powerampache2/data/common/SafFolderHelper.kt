/**
 * Copyright (C) 2026  Antonio Tari
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
package luci.sixsixsix.powerampache2.data.common

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import luci.sixsixsix.powerampache2.domain.errors.FileWriteException
import java.io.InputStream

class SafFolderHelper(private val context: Context) {
    fun getOrCreatePath(rootUri: Uri, fullPath: String): DocumentFile {
        var current = DocumentFile.fromTreeUri(context, rootUri)
            ?: error("Cannot access root URI")

        val parts = fullPath.split("/")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        for (folderName in parts) {
            val existing = current.findFileIgnoreCase(folderName)
            current = if (existing != null && existing.isDirectory) {
                existing
            } else {
                current.createDirectory(folderName)
                    ?: error("Cannot create folder: $folderName")
            }
        }
        return current
    }

    private fun DocumentFile.findFileIgnoreCase(name: String): DocumentFile? {
        for (doc in listFiles()) {
            if (doc.name?.equals(name, ignoreCase = true) == true) {
                return doc
            }
        }
        return null
    }

    suspend fun getOrCreateFolder(rootUri: Uri, fullPath: String): Uri {
        return getOrCreatePath(rootUri, fullPath).uri
    }

    @Throws(Exception::class)
    fun writeFile(
        folder: DocumentFile,
        fileName: String,
        mimeType: String?,
        inputStream: InputStream,
        bufferSize: Int
    ): Uri {
        val existing = folder.findFile(fileName)
        existing?.delete() // optional: replace existing file

        val file = folder.createFile(
            if (mimeType.isNullOrBlank()) "application/octet-stream" else mimeType,
            fileName)
            ?: error("Cannot create file: $fileName")

        try {
            context.contentResolver.openOutputStream(file.uri)?.use { outputStream ->
                val buffer = ByteArray(bufferSize)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }

                outputStream.flush()
            }
        } catch (e: Exception) {
            throw FileWriteException("error writing file: ${e.localizedMessage}")
        } finally {
            inputStream.close()
        }

        return file.uri
    }

    fun deleteFile(rootUri: Uri, fullPath: String, fileName: String): Boolean {
        val folder = getOrCreatePath(rootUri, fullPath)
        val existing = folder.findFile(fileName)
        return existing?.delete() ?: false
    }

    suspend fun writeFile(rootUri: Uri, fullPath: String, fileName: String, mimeType: String?, inputStream: InputStream, bufferSize: Int): Uri {
        val folder = getOrCreatePath(rootUri, fullPath)
        return writeFile(folder, fileName, mimeType, inputStream, bufferSize)
    }
}
