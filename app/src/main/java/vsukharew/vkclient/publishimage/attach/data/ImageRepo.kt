package vsukharew.vkclient.publishimage.attach.data

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.UploadedImage

interface ImageRepo {
    suspend fun uploadImage(
        image: Image,
        onProgressUpdated: (Double) -> Unit
    ): Result<UploadedImage>
}