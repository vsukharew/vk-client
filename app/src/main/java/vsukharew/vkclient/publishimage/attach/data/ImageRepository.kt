package vsukharew.vkclient.publishimage.attach.data

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.extension.EMPTY
import vsukharew.vkclient.common.extension.map
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.publishimage.attach.data.model.UploadImageResponse
import vsukharew.vkclient.publishimage.attach.data.network.ImageApi
import vsukharew.vkclient.publishimage.attach.domain.model.Image

class ImageRepository(
    private val imageApi: ImageApi,
    private val context: Context
) : ImageRepo {

    override suspend fun uploadImage(image: Image): Result<ResponseWrapper<UploadImageResponse>> {
        val addressResult =
            imageApi.getImageWallUploadAddress().map { it.response?.uploadUrl ?: String.EMPTY }

        return when (addressResult) {
            is Result.Success -> uploadImage(addressResult.data, image)
            is Result.Error -> addressResult
        }
    }

    private suspend fun uploadImage(url: String, image: Image): Result<ResponseWrapper<UploadImageResponse>> {
        val streamResult = runCatching {
            context.contentResolver
                .openInputStream(Uri.parse(image.uri))
                .use { it!!.readBytes() }
        }
        return when (streamResult.isSuccess) {
            true -> {
                val mediaType = "image/*".toMediaType()
                val requestBody = streamResult.getOrThrow().toRequestBody(mediaType)
                val multipartBody =
                    MultipartBody.Part.createFormData("file1", image.uri, requestBody)
                imageApi.uploadImage(url, multipartBody)
            }
            else -> Result.Error.UnknownError(streamResult.exceptionOrNull()!!)
        }
    }
}