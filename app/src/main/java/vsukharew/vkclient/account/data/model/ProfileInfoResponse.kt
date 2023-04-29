package vsukharew.vkclient.account.data.model

import com.google.gson.annotations.SerializedName
import vsukharew.vkclient.common.network.response.DEFAULT_STRING

data class ProfileInfoResponse(
    @SerializedName("first_name") val firstName: String = DEFAULT_STRING,
    @SerializedName("last_name") val lastName: String =  DEFAULT_STRING,
    @SerializedName("screen_name") val screen_name: String? = null
)