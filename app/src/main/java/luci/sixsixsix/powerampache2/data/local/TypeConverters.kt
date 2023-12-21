package luci.sixsixsix.powerampache2.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute
import java.time.LocalDateTime

object Converters {
    @TypeConverter
    fun stringToMusicAttribute(artist: String?): MusicAttribute? =
        try {
            Gson().fromJson(artist, MusicAttribute::class.java)
        } catch (e: Exception) {
            MusicAttribute.emptyInstance()
        }

    @TypeConverter
    fun stringToMusicAttributeList(artists: String?): List<MusicAttribute>? =
        try {
            Gson().fromJson(artists, MusicAttributesContainer::class.java).attr
        } catch (e: Exception) {
            listOf()
        }

    @TypeConverter
    fun musicAttributeListToString(artists: List<MusicAttribute>?): String? =
        try {
            Gson().toJson(MusicAttributesContainer(artists!!))
        } catch (e: Exception) {
            "{}"
        }

    @TypeConverter
    fun musicAttributeToString(artist: MusicAttribute): String? =
        try {
            Gson().toJson(artist)
        } catch (e: Exception) {
            "{}"
        }

    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun localDateTimeToString(date: LocalDateTime?): String? = date?.toString()
}

data class MusicAttributesContainer(
    val attr: List<MusicAttribute>?
)
