package luci.sixsixsix.powerampache2.data.remote.dto


import com.google.gson.annotations.SerializedName

data class ShareDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("owner")
    val owner: String? = null,
    @SerializedName("allow_stream")
    val allowStream: Any? = null,
    @SerializedName("allow_download")
    val allowDownload: Any? = null,
    @SerializedName("creation_date")
    val creationDate: Int? = null,
    @SerializedName("lastvisit_date")
    val lastvisitDate: Int? = null,
    @SerializedName("object_type")
    val objectType: String? = null,
    @SerializedName("object_id")
    val objectId: String? = null,
    @SerializedName("expire_days")
    val expireDays: Int? = null,
    @SerializedName("max_counter")
    val maxCounter: Int? = null,
    @SerializedName("counter")
    val counter: Int? = null,
    @SerializedName("secret")
    val secret: String? = null,
    @SerializedName("public_url")
    val publicUrl: String? = null,
    @SerializedName("description")
    val description: String? = null
): AmpacheBaseResponse()
