package vsukharew.vkclient.account.data.model

import com.google.gson.annotations.SerializedName

sealed class ScreenNameResponse {
    object UnresolvedScreenNameResponse : ScreenNameResponse()
    data class ResolvedScreenNameResponse (
        @SerializedName("object_id") val objectId : Int,
        @SerializedName("type") val type : String
    ) : ScreenNameResponse()
}
