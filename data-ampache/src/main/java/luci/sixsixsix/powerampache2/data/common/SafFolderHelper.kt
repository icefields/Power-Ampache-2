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

        val parts = fullPath.split("/").map { it.trim() }.filter { it.isNotEmpty() }

        for (folderName in parts) {
            val existing = current.findFile(folderName)
            current = if (existing != null && existing.isDirectory) {
                existing
            } else {
                current.createDirectory(folderName)
                    ?: error("Cannot create folder: $folderName")
            }
        }
        return current
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
