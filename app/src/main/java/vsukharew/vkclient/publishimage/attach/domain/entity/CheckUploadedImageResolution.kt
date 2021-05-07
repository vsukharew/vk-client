package vsukharew.vkclient.publishimage.attach.domain.entity

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.DomainContentResolver
import vsukharew.vkclient.publishimage.attach.domain.model.Image

class CheckUploadedImageResolution(private val contentResolver: DomainContentResolver) {

    fun checkUploadedImageResolution(image: Image): Result<Unit> {
        val imageResolution = contentResolver.getImageResolution(image.uri)
        val maxOverallImageResolution = 14000 // width + height
        return with(imageResolution) {
            if (width + height > maxOverallImageResolution) {
                Result.Error.DomainError.ImageResolutionTooLargeError
            } else {
                Result.SuccessNoBody
            }
        }
    }
}