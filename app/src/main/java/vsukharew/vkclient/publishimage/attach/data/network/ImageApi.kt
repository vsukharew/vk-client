package vsukharew.vkclient.publishimage.attach.data.network

import okhttp3.MultipartBody
import retrofit2.http.*
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.network.ServerUrls.Image.UPLOAD_ADDRESS_WALL
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.publishimage.attach.data.model.UploadImageResponse
import vsukharew.vkclient.publishimage.attach.data.model.WallUploadAddressResponse

interface ImageApi {

    @POST(UPLOAD_ADDRESS_WALL)
    suspend fun getImageWallUploadAddress(): Result<ResponseWrapper<WallUploadAddressResponse>>

    @Multipart
    @POST
    suspend fun uploadImage(
        @Url url: String,
        @Part image: MultipartBody.Part
    ): Result<ResponseWrapper<UploadImageResponse>>
}