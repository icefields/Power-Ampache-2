package luci.sixsixsix.powerampache2.data.mapping


import androidx.room.TypeConverter
import luci.sixsixsix.powerampache2.domain.mappers.DateMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AmpacheDateMapper: DateMapper {
    /**
     * The Ampache handshake method returns dates in ISO 8601
     * 2022-02-25T19:05:00+00:00
     * YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
     * val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssO")//("YYYY-MM-DD'T'hh:mm:ss'T'ZD")
     */
    /**
     * The Ampache handshake method returns dates in ISO 8601
     * 2022-02-25T19:05:00+00:00
     * YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
     * val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssO")//("YYYY-MM-DD'T'hh:mm:ss'T'ZD")
     */
    override fun isoStringToLocalDateTime(timestamp: String): LocalDateTime =
        LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_ZONED_DATE_TIME)


    // ----- DATABASE CONVERTERS

    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }

//    fun Duration.toDaysPartCompat(): Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        toDaysPart()
//    } else {
//        seconds / 86400
//    }
//
//    fun Duration.toHoursPartCompat(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        toHoursPart()
//    } else {
//        (toHours() % 24).toInt()
//    }
//
//    fun Duration.toMinutesPartCompat(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        toMinutesPart()
//    } else {
//        (toMinutes() % 60).toInt()
//    }
//
//    fun Duration.toSecondsPartCompat(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        toSecondsPart()
//    } else {
//        (seconds % 60).toInt()
//    }
}
