package vsukharew.vkclient.publishimage.attach.data

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.extension.EMPTY
import vsukharew.vkclient.common.extension.map
import vsukharew.vkclient.common.network.ProgressRequestBody
import vsukharew.vkclient.publishimage.attach.data.network.ImageApi
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.UploadedImage

class ImageRepository(
    private val imageApi: ImageApi,
    private val context: Context
) : ImageRepo {

    override suspend fun uploadImage(
        image: Image,
        onProgressUpdated: (Double) -> Unit
    ): Result<UploadedImage> {
        val addressResult =
            imageApi.getImageWallUploadAddress().map { it.response?.uploadUrl ?: String.EMPTY }

        return when (addressResult) {
            is Result.Success -> uploadImage(
                addressResult.data,
                image,
                onProgressUpdated
            ).map { wrapper ->
                with(wrapper) { UploadedImage(server, photosList, aid, hash) }
            }
            is Result.Error -> addressResult
        }
    }

    private suspend fun uploadImage(
        url: String,
        image: Image,
        onProgressUpdated: (Double) -> Unit
    ): Result<UploadedImage> {
        val streamResult = runCatching {
            context.contentResolver
                .openInputStream(Uri.parse(image.uri))
                .use { it!!.readBytes() }
        }
        return when (streamResult.isSuccess) {
            true -> {
                val mediaType = "image/*".toMediaType()
                val requestBody = ProgressRequestBody(
                    streamResult.getOrThrow().toRequestBody(mediaType),
                    image,
                    context,
                    onProgressUpdated,
                )
                val multipartBody =
                    MultipartBody.Part.createFormData("file1", image.uri, requestBody)
                with(imageApi.uploadImage(url, multipartBody)) {
                    when {
                        isDataReceived() -> {
                            Result.Success(UploadedImage(server!!, photo!!, aid, hash!!))
                        }
                        else -> domainError!!
                    }
                }
            }
            else -> Result.Error.UnknownError(streamResult.exceptionOrNull()!!)
        }
    }
}