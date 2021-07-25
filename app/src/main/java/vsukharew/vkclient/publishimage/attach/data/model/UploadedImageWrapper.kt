package vsukharew.vkclient.publishimage.attach.data.model

import com.google.gson.annotations.SerializedName
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.network.response.ErrorResponse

data class UploadedImageWrapper(
    @SerializedName("server") val server : Int?,
    @SerializedName("photo") val photo : String?,
    @SerializedName("aid") val aid : Int?,
    @SerializedName("hash") val hash : String?,
    @SerializedName("error") val errorResponse: ErrorResponse?
) {
    var domainError: Either.Error? = null
    var responseCode: Int? = null

    fun isDataReceived(): Boolean = server != null && photo != null && hash != null

    companion object {
        val EMPTY = UploadedImageWrapper(null, null, null, null, null)
    }
}