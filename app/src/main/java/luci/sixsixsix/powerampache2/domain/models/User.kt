package luci.sixsixsix.powerampache2.domain.models

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.common.Constants

@Parcelize
data class User(
    val id: String,
    val username: String,
    val email: String,
    val access: Int,
    val streamToken: String? = null,
    val fullNamePublic: Int,
    val fullName: String? = null,
    //val validation: Any? = null,
    val disabled: Boolean,
    val createDate: Int = Constants.ERROR_INT,
    val lastSeen: Int = Constants.ERROR_INT,
    val website: String,
    val state: String,
    val city: String
): Parcelable {
    companion object {
        fun emptyUser(): User = User(
            "", "", "",
            Constants.ERROR_INT,
            null,
            Constants.ERROR_INT,
            null,
            true,
            Constants.ERROR_INT,
            Constants.ERROR_INT,
            "",
            "",
            ""
        )
        fun mockUser(): User = Gson().fromJson("{\n" +
                "    \"id\": \"3\",\n" +
                "    \"username\": \"luci\",\n" +
                "    \"auth\": null,\n" +
                "    \"email\": \"some@er.fd\",\n" +
                "    \"access\": 25,\n" +
                "    \"streamtoken\": null,\n" +
                "    \"fullname_public\": 1,\n" +
                "    \"validation\": null,\n" +
                "    \"disabled\": false,\n" +
                "    \"create_date\": 1704516888,\n" +
                "    \"last_seen\": 1706202621,\n" +
                "    \"website\": null,\n" +
                "    \"state\": \"Mercury\",\n" +
                "    \"city\": \"Phobos Town\",\n" +
                "    \"fullname\": \"Lucifer\"\n" +
                "}", User::class.java)
    }
}
