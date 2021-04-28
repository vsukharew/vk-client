package vsukharew.vkclient.publishimage.attach.data

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.UploadedImage

interface ImageRepo {
    val rawImages: List<Image>
    val uploadedImages: List<UploadedImage>

    suspend fun uploadImage(
        image: Image,
        isRetryLoading: Boolean,
        onProgressUpdated: (Double) -> Unit
    ): Result<UploadedImage>
    fun removeUploadedImage(image: Image)
}