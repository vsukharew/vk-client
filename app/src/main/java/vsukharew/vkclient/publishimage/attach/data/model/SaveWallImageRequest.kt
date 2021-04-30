package vsukharew.vkclient.publishimage.attach.data.model

import com.google.gson.annotations.SerializedName

data class SaveWallImageRequest(
    @SerializedName("server") val server: Int,
    @SerializedName("photo") val photo: String,
    @SerializedName("hash") val hash: String,
    @SerializedName("caption") val caption: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
)