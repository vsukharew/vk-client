package vsukharew.vkclient.publishimage.attach.domain.interactor

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.publishimage.attach.data.ImageRepo
import vsukharew.vkclient.publishimage.attach.data.model.UploadImageResponse
import vsukharew.vkclient.publishimage.attach.domain.model.Image

class ImageInteractorImpl(private val imageRepo: ImageRepo) : ImageInteractor {

    override suspend fun uploadImage(image: Image): Result<ResponseWrapper<UploadImageResponse>> {
        return imageRepo.uploadImage(image)
    }
}