package vsukharew.vkclient.publishimage.attach.domain.interactor

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.UploadedImage

interface ImageInteractor {
    suspend fun uploadImage(
        image: Image,
        onProgressUpdated: (Double) -> Unit
    ): Result<UploadedImage>
    fun addUploadedImage(image: UploadedImage)
    fun removeUploadedImage(hash: String)
}