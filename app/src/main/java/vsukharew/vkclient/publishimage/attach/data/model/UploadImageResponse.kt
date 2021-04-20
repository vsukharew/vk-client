package vsukharew.vkclient.publishimage.attach.data.model

import com.google.gson.annotations.SerializedName

data class UploadImageResponse(
    @SerializedName("server") val server : Int,
    @SerializedName("photo") val photosList : String,
    @SerializedName("aid") val aid : Int,
    @SerializedName("hash") val hash : String
)