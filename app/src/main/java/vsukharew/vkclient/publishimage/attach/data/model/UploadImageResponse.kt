package vsukharew.vkclient.publishimage.attach.data.model

import com.google.gson.annotations.SerializedName
import vsukharew.vkclient.common.network.response.DEFAULT_INT
import vsukharew.vkclient.common.network.response.DEFAULT_STRING

data class UploadImageResponse(
    @SerializedName("server") val server : Int = DEFAULT_INT,
    @SerializedName("photo") val photosList : String = DEFAULT_STRING,
    @SerializedName("aid") val aid : Int = DEFAULT_INT,
    @SerializedName("hash") val hash : String = DEFAULT_STRING
)