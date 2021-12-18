package vsukharew.vkclient.publishimage.attach.data.model

import com.google.gson.annotations.SerializedName
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.network.response.ErrorResponse

data class UploadedImageWrapper(
    @SerializedName("server") val server : Int?,
    @SerializedName("photo") val photo : String?,
    @SerializedName("aid") val aid : Int?,
    @SerializedName("hash") val hash : String?,
    @SerializedName("error") val errorResponse: ErrorResponse?
)