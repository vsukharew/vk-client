package vsukharew.vkclient.publishimage.attach.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.data.ImageRepo
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.UploadedImage

class ImageInteractorImpl(private val imageRepo: ImageRepo) : ImageInteractor {

    private val areImagesReadyForPublishing: Boolean
        get() = with(imageRepo) {
            rawImages.size == uploadedImages.size
                    && rawImages.isNotEmpty()
                    && uploadedImages.isNotEmpty()
        }

    private val publishingReadinessFlow = MutableStateFlow(false)

    override suspend fun uploadImage(
        image: Image,
        isRetryLoading: Boolean,
        onProgressUpdated: (Double) -> Unit
    ): Result<UploadedImage> {
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