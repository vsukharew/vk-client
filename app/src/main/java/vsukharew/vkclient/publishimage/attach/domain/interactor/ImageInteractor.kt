package vsukharew.vkclient.publishimage.attach.domain.interactor

import kotlinx.coroutines.flow.Flow
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.SavedWallImage

interface ImageInteractor {
    fun doSavedImagesExist(): Boolean
    suspend fun uploadImage(
        image: Image,
        isRetryLoading: Boolean,
        onProgressUpdated: (Double) -> Unit
    ): Either<AppError, SavedWallImage>

    fun removeUploadedImage(image: Image)
    fun removeAllImages()
    suspend fun postImagesOnWall(
        message: String,
        latitude: Double? = null,
        longitude: Double? = null
    ): Either<AppError, Int>

    fun observePublishedPosts(): Flow<Int?>
}