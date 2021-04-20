package vsukharew.vkclient.publishimage.attach.domain.interactor

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.data.model.UploadImageResponse
import vsukharew.vkclient.publishimage.attach.domain.model.Image

interface ImageInteractor {
    suspend fun uploadImage(image: Image): Result<UploadImageResponse>
}