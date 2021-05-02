package vsukharew.vkclient.publishimage.attach.domain.interactor

import kotlinx.coroutines.flow.*
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.extension.ifSuccess
import vsukharew.vkclient.publishimage.attach.data.ImageRepo
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.SavedWallImage

class ImageInteractorImpl(private val imageRepo: ImageRepo) : ImageInteractor {

    private val areImagesReadyForPublishing: Boolean
        get() = with(imageRepo) {
            rawImages.size == savedImages.size
                    && rawImages.isNotEmpty()
                    && savedImages.isNotEmpty()
        }

    private val publishingReadinessFlow = MutableStateFlow(false)

    override suspend fun uploadImage(
        image: Image,
        isRetryLoading: Boolean,
        onProgressUpdated: (Double) -> Unit
    ): Result<SavedWallImage> {
        return with(imageRepo) {
            uploadImage(image, isRetryLoading, onProgressUpdated).also {
                publishingReadinessFlow.value = areImagesReadyForPublishing
            }
        }
    }

    override fun observePublishingReadiness(): Flow<Boolean> {
        return publishingReadinessFlow
    }

    override fun removeUploadedImage(image: Image) {
        imageRepo.removeUploadedImage(image).also {
            publishingReadinessFlow.value = areImagesReadyForPublishing
        }
    }
}