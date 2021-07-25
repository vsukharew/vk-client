package vsukharew.vkclient.publishimage.attach.domain.entity

import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.NoBody
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.DomainContentResolver
import vsukharew.vkclient.publishimage.attach.domain.model.Image

class CheckUploadedImageResolution(private val contentResolver: DomainContentResolver) {

    fun checkUploadedImageResolution(image: Image): Either<NoBody> {
        val imageResolution = contentResolver.getImageResolution(image.uri)
        val maxOverallImageResolution = 14000 // width + height
        return with(imageResolution) {
            if (width + height > maxOverallImageResolution) {
                Either.Error.DomainError.ImageResolutionTooLargeError
            } else {
                Either.Success(NoBody)
            }
        }
    }
}