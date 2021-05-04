package vsukharew.vkclient.publishimage.attach.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import vsukharew.vkclient.common.domain.model.AttachmentType
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.extension.map
import vsukharew.vkclient.common.extension.switchMap
import vsukharew.vkclient.common.network.ProgressRequestBody
import vsukharew.vkclient.publishimage.attach.data.model.SavedWallImageResponse
import vsukharew.vkclient.publishimage.attach.data.network.ImageApi
import vsukharew.vkclient.publishimage.attach.data.network.WallApi
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.DomainContentResolver
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.CAMERA
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.GALLERY
import vsukharew.vkclient.publishimage.attach.domain.model.SavedWallImage
import java.util.*

class ImageRepository(
    private val imageApi: ImageApi,
    private val wallApi: WallApi,
    private val contentResolver: DomainContentResolver
) : ImageRepo {

    override val rawImages: MutableList<Image> = mutableListOf()
    override val savedImages: MutableList<SavedWallImage> = mutableListOf()

    override suspend fun uploadImage(
        image: Image,
        isRetryLoading: Boolean,
        onProgressUpdated: (Double) -> Unit
    ): Result<SavedWallImage> {
        if (!isRetryLoading) rawImages.add(image)
        return imageApi.getImageWallUploadAddress()
            .switchMap { uploadImageInternal(it.response!!.uploadUrl, image, onProgressUpdated) }
            .map { response ->
                with(response) {
                    SavedWallImage(
                        id,
                        albumId,
                        ownerId
                    ).also { savedImages.add(it) }
                }
            }
    }

    override fun removeUploadedImage(image: Image) {
        val imageToRemove = rawImages.indexOf(image)
        if (rawImages.size == savedImages.size) {
            savedImages.removeAt(imageToRemove)
        }
        rawImages.removeAt(imageToRemove)
    }

    override suspend fun postImagesOnWall(message: String): Result<Int> {
        val attachments = savedImages.joinToString {
            "${AttachmentType.PHOTO.name.toLowerCase(Locale.getDefault())}${it.ownerId}_${it.id}>"
        }
        return wallApi.postToWall(message, attachments).map { it.response!!.postId }
    }

    private suspend fun uploadImageInternal(
        url: String,
        image: Image,
        onProgressUpdated: (Double) -> Unit
    ): Result<SavedWallImageResponse> {
        val streamResult = runCatching {
            contentResolver.openInputStream(image.uri)
                .use { it!!.readBytes() }
        }
        return when (streamResult.isSuccess) {
            true -> {
                val mediaType = "image/*".toMediaType()
                val requestBody = ProgressRequestBody(
                    streamResult.getOrThrow().toRequestBody(mediaType),
                    image,
                    contentResolver,
                    onProgressUpdated,
                )
                val fileName = when (image.source) {
                    CAMERA -> image.uri
                    GALLERY -> {
                        val extension = contentResolver.getExtensionFromContentUri(image.uri)
                        "${image.uri}.$extension"
                    }
                }
                val multipartBody =
                    MultipartBody.Part.createFormData("photo", fileName, requestBody)
                with(imageApi.uploadImage(url, multipartBody)) {
                    when {
                        isDataReceived() -> { saveImage(photo!!, server!!, hash!!) }
                        else -> domainError!!
                    }
                }
            }
            else -> Result.Error.UnknownError(streamResult.exceptionOrNull()!!)
        }
    }

    private suspend fun saveImage(
        photo: String,
        server: Int,
        hash: String
    ): Result<SavedWallImageResponse> {
        return imageApi.saveImage(photo, server, hash).map { it.response!!.first() }
    }
}