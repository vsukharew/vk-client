package vsukharew.vkclient.publishimage.attach.domain.interactor

import kotlinx.coroutines.flow.*
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.extension.ifSuccess
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
    ): Result<SavedWallImage> {
        return checkSizeEntity.checkUploadedImageSize(image)
            .switchMap { checkResolutionEntity.checkUploadedImageResolution(image) }
            .switchMap { with(imageRepo) {
                uploadImage(image, isRetryLoading, onProgressUpdated)
            }
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
    ): Result<Int> {
        return imageRepo.postImagesOnWall(message, latitude, longitude)
            .ifSuccess { publishingPostFlow.value = it }
    }
}