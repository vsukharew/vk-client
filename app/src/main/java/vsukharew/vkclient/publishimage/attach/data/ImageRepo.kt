package vsukharew.vkclient.publishimage.attach.data

import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.SavedWallImage

interface ImageRepo {

    suspend fun uploadImage(
        image: Image,
        isRetryLoading: Boolean,
        onProgressUpdated: (Double) -> Unit
    ): Either<AppError, SavedWallImage>

    suspend fun postImagesOnWall(
        message: String,
        latitude: Double? = null,
        longitude: Double? = null
    ): Either<AppError, Int>
}