package vsukharew.vkclient.account.data.model

import com.google.gson.annotations.SerializedName

data class ProfileInfoResponse(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("screen_name") val screen_name: String?
)