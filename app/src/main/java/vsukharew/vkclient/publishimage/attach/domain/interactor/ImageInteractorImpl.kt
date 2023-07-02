package vsukharew.vkclient.publishimage.attach.domain.interactor

import kotlinx.coroutines.flow.*
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.extension.ifSuccess
import vsukharew.vkclient.common.extension.sideEffect
import vsukharew.vkclient.common.extension.switchMap
import vsukharew.vkclient.publishimage.attach.data.ImageRepo
import vsukharew.vkclient.publishimage.attach.domain.entity.CheckUploadedImageResolution
import vsukharew.vkclient.publishimage.attach.domain.entity.CheckUploadedImageSize
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.SavedWallImage

class ImageInteractorImpl(
    private val imageRepo: ImageRepo,
    private val checkSizeEntity: CheckUploadedImageSize,
    private val checkResolutionEntity: CheckUploadedImageResolution,
) : ImageInteractor {

    private val publishingPostFlow = MutableStateFlow<Int?>(null)

    override fun doSavedImagesExist(): Boolean = imageRepo.savedImages.isNotEmpty()

    override suspend fun uploadImage(
        image: Image,
        isRetryLoading: Boolean,
        onProgressUpdated: (Double) -> Unit
    ): Either<AppError, SavedWallImage> {
        return sideEffect {
            checkSizeEntity.checkUploadedImageSize(image).bind()
            checkResolutionEntity.checkUploadedImageResolution(image).bind()
            imageRepo.uploadImage(image, isRetryLoading, onProgressUpdated).bind()
        }
    }

    override fun removeAllImages() {
        imageRepo.removeAllImages()
    }

    override fun observePublishedPosts(): Flow<Int?> {
        return publishingPostFlow
    }

    override fun removeUploadedImage(image: Image) {
        imageRepo.removeUploadedImage(image)
    }

    override suspend fun postImagesOnWall(
        message: String,
        latitude: Double?,
        longitude: Double?
    ): Either<AppError, Int> {
        return sideEffect {
            imageRepo.postImagesOnWall(message, latitude, longitude).bind()
        }.ifSuccess { publishingPostFlow.value = it }
    }
}