package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CREDENTIALS_PRIMARY_KEY = "power-ampache-2-credentials"
@Entity
data class CredentialsEntity(
    @PrimaryKey val primaryKey: String = CREDENTIALS_PRIMARY_KEY,
    val username: String,
    val password: String,
    val serverUrl: String
)
