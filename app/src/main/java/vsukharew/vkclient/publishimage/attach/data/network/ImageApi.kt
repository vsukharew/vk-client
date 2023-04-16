package vsukharew.vkclient.publishimage.attach.data.network

import okhttp3.MultipartBody
import retrofit2.http.*
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.network.ServerUrls.Image.SAVE_IMAGE_WALL
import vsukharew.vkclient.common.network.ServerUrls.Image.UPLOAD_ADDRESS_WALL
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.publishimage.attach.data.model.SavedWallImageResponse
import vsukharew.vkclient.publishimage.attach.data.model.UploadedImageWrapper
import vsukharew.vkclient.publishimage.attach.data.model.WallUploadAddressResponse

interface ImageApi {

    @POST(UPLOAD_ADDRESS_WALL)
    suspend fun getImageWallUploadAddress(): Either<AppError, ResponseWrapper<WallUploadAddressResponse>>

    @Multipart
    @POST
    suspend fun uploadImage(@Url url: String, @Part image: MultipartBody.Part): Either<AppError, UploadedImageWrapper>

    @POST(SAVE_IMAGE_WALL)
    suspend fun saveImage(
        @Query("photo") photo: String,
        @Query("server") server: Int,
        @Query("hash") hash: String,
    ): Either<AppError, ResponseWrapper<List<SavedWallImageResponse>>>
}