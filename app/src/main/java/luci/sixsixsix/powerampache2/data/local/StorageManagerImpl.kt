package luci.sixsixsix.powerampache2.data.local

import android.app.Application
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.utils.StorageManager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import javax.inject.Inject

private const val BUFFER_SIZE = 4 * 1024
private const val SUB_DIR = "offline_music"

class StorageManagerImpl @Inject constructor(
    private val weakContext: WeakReference<Application>,
    private val musicRepository: MusicRepository
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
            StringBuffer(weakContext.get()!!.filesDir.absolutePath).append("/").append(SUB_DIR).append("/")
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

    private suspend fun getAbsolutePathFile(song: Song): String? =
        getAbsolutePathDir(song = song)?.let { absoluteDirPath ->
            val relativePath = song.filename
            val fileName = relativePath.substring(relativePath.lastIndexOf("/") + 1)
            StringBuffer(absoluteDirPath).append("/").append(fileName).toString()
        }

    private suspend fun getAbsolutePathDir(song: Song): String? =
        musicRepository.getUser()?.username?.let { owner ->
            val relativePath = song.filename
            val fileName = relativePath.substring(relativePath.lastIndexOf("/") + 1)
            val relativeDirectory = relativePath.replace(fileName, "")

            StringBuffer(weakContext.get()!!.filesDir.absolutePath)
                .append("/")
                .append(SUB_DIR)
                .append("/")
                .append(owner)
                .append("/")
                .append(relativeDirectory)
                .toString()
        }
}