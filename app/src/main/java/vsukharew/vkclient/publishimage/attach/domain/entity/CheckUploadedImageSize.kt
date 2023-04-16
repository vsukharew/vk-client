package vsukharew.vkclient.publishimage.attach.domain.entity

import vsukharew.vkclient.common.domain.model.*
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.DomainContentResolver
import vsukharew.vkclient.publishimage.attach.domain.model.Image

class CheckUploadedImageSize(
    private val contentResolver: DomainContentResolver
) {
    fun checkUploadedImageSize(image: Image): Either<AppError, NoBody> {
        val fileSize = contentResolver.getFileSize(image.uri)
        val maxImageSize = 1024 * 1000 * 50  // 50MB
        return if (fileSize?.compareTo(maxImageSize) == 1) {
            Left(AppError.DomainError.FileTooLargeError)
        } else {
            Right(NoBody)
        }
    }
}