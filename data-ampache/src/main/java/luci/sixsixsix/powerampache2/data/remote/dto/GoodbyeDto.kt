package luci.sixsixsix.powerampache2.data.remote.dto

data class GoodbyeDto(
    val success: Any? = null,
    val error: Any? = null
)

fun GoodbyeDto.toBoolean() = success?.let { true } ?: run { false }
