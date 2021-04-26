package vsukharew.vkclient.publishimage.attach.domain.interactor

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.data.ImageRepo
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.UploadedImage

class ImageInteractorImpl(private val imageRepo: ImageRepo) : ImageInteractor {

    override suspend fun uploadImage(
        image: Image,
        onProgressUpdated: (Double) -> Unit
    ): Result<UploadedImage> {
        return imageRepo.uploadImage(image, onProgressUpdated)
    }
}