package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.LocalSettings
import luci.sixsixsix.powerampache2.domain.models.PowerAmpTheme
import luci.sixsixsix.powerampache2.domain.models.User

@Entity
data class LocalSettingsEntity(
    @PrimaryKey val username: String,
    val theme: String
)

fun LocalSettingsEntity.toLocalSettings() = LocalSettings(
    username = username,
    theme = PowerAmpTheme.valueOf(theme)
)

fun LocalSettings.toLocalSettingsEntity() = LocalSettingsEntity(
    username = username,
    theme = theme.name
)
