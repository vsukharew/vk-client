package vsukharew.vkclient.publishimage.attach.domain.entity

import vsukharew.vkclient.common.domain.model.*
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.DomainContentResolver
import vsukharew.vkclient.publishimage.attach.domain.model.Image

class CheckUploadedImageResolution(private val contentResolver: DomainContentResolver) {

    fun checkUploadedImageResolution(image: Image): Either<AppError, NoBody> {
        val imageResolution = contentResolver.getImageResolution(image.uri)
        val maxOverallImageResolution = 14000 // width + height
        return with(imageResolution) {
            if (width + height > maxOverallImageResolution) {
                Left(AppError.DomainError.ImageResolutionTooLargeError)
            } else {
                Right(NoBody)
            }
        }
    }
}