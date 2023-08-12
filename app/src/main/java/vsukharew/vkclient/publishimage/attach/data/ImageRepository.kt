package vsukharew.vkclient.publishimage.attach.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.AttachmentType
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.extension.*
import vsukharew.vkclient.common.network.ProgressRequestBody
import vsukharew.vkclient.common.utils.AppDirectories
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

    private val savedImages: MutableList<SavedWallImage> = mutableListOf()

    override suspend fun uploadImage(
        image: Image,
        isRetryLoading: Boolean,
        onProgressUpdated: (Double) -> Unit
    ): Either<AppError, SavedWallImage> {
        return sideEffect {
            val wrapper = imageApi.getImageWallUploadAddress().bind()
            val address = safeNonNull { wrapper.response.bind() }
            val response = uploadImage(address.uploadUrl, image, onProgressUpdated).bind()
            response.run {
                SavedWallImage(
                    id,
                    albumId,
                    ownerId
                )
            }.also { savedImages.add(it) }
        }
    }

    override suspend fun postImagesOnWall(
        message: String,
        latitude: Double?,
        longitude: Double?
    ): Either<AppError, Int> {
        return sideEffect {
            if (savedImages.isEmpty()) {
                Left<AppError>(AppError.DomainError.NoPhotosToPostError).bindLeft()
            }
            val attachments = savedImages.joinToString {
                "${AttachmentType.PHOTO.name.lowercase(Locale.getDefault())}${it.ownerId}_${it.id}>"
            }
            val wrapper = wallApi.postToWall(message, attachments, latitude, longitude).bind()
            safeNonNull { wrapper.response?.postId.bind() }
        }.ifSuccess {
            contentResolver.deleteCacheFiles(AppDirectories.WALL_IMAGES)
        }
    }

    private suspend fun uploadImage(
        url: String,
        image: Image,
        onProgressUpdated: (Double) -> Unit
    ): Either<AppError, SavedWallImageResponse> {
        return runCatching {
            contentResolver.openInputStream(image.uri)
                .use { it!!.readBytes() }
        }.mapCatching { bytes ->
            sideEffect {
                val mediaType = "image/*".toMediaType()
                val requestBody = ProgressRequestBody(
                    bytes.toRequestBody(mediaType),
                    image,
                    contentResolver,
                    onProgressUpdated,
                )
                val fileName = filename(image)
                val multipartBody =
                    MultipartBody.Part.createFormData("photo", fileName, requestBody)
                val wrapper = imageApi.uploadImage(url, multipartBody).bind()
                val (photo, server, hash) = safeNonNull {
                    wrapper.run {
                        Triple(
                            photo.bind(),
                            server.bind(),
                            hash.bind()
                        )
                    }
                }
                val response = saveImage(photo, server, hash)
                response.bind()
            }
        }.getOrElse { Left(AppError.UnknownError(it)) }
    }

    private suspend fun saveImage(
        photo: String,
        server: Int,
        hash: String
    ): Either<AppError, SavedWallImageResponse> {
        return sideEffect {
            val wrapper = imageApi.saveImage(photo, server, hash).bind()
            safeNonNull {
                val response = wrapper.response.bind()
                response.firstOrNull().bind()
            }
        }
    }

    private fun filename(image: Image) =
        when (image.source) {
            CAMERA -> image.uri
            GALLERY -> {
                val extension = contentResolver.getExtensionFromContentUri(image.uri)
                "${image.uri}.$extension"
            }
        }
}