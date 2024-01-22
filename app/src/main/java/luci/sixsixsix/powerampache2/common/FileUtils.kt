package luci.sixsixsix.powerampache2.common

import android.R.string
import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.models.Song
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


private const val BUFFER_SIZE = 4 * 1024
private const val SUB_DIR = "offline_music"
class FileUtils @Inject constructor(val context: Context) {
    @Throws(Exception::class)
    suspend fun saveFile(song: Song, body: ResponseBody?) = withContext(Dispatchers.IO) {
        val relativePath = song.filename
        val fileName = relativePath.substring(relativePath.lastIndexOf("/")+1)
        val relativeDirectory = relativePath.replace(fileName, "")

        val pathBuilder = StringBuffer(context.filesDir.absolutePath).append("/").append(SUB_DIR).append("/").append(relativeDirectory)
        val absoluteDirPath = pathBuilder.toString()

        val directory = File(absoluteDirPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val absolutePath = pathBuilder.append("/").append(fileName).toString()
        var input: InputStream? = null
        try {
            // TODO fix double bang, right now will throw nullpointerexception which is fine
            input = body!!.byteStream()
            val fos = FileOutputStream(absolutePath)
            fos.use { output ->
                val buffer = ByteArray(BUFFER_SIZE)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return@withContext absolutePath
        } catch (e: Exception) {
            throw e
        } finally {
            input?.close()
        }
    }

    @Throws(Exception::class)
    suspend fun deleteSong(song: Song) = withContext(Dispatchers.IO) {
        val relativePath = song.filename
        val fileName = relativePath.substring(relativePath.lastIndexOf("/")+1)
        val relativeDirectory = relativePath.replace(fileName, "")
        val pathBuilder = StringBuffer(context.filesDir.absolutePath).append("/").append(SUB_DIR).append("/").append(relativeDirectory)
        val absolutePath = pathBuilder.append("/").append(fileName).toString()
        L(absolutePath)
        val myFile = File(absolutePath)
        if (myFile.exists()) {
            L("deleting file")
            myFile.delete()
        } else {
            L("file not exists")

        }
    }
}
