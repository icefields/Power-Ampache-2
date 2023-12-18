package luci.sixsixsix.powerampache2.domain.mappers

import java.time.LocalDateTime

interface DateMapper {
    fun isoStringToLocalDateTime(timestamp: String): LocalDateTime
    operator fun invoke(timestamp: String): LocalDateTime = isoStringToLocalDateTime(timestamp)
}
