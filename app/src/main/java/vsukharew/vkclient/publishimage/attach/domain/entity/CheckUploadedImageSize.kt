package vsukharew.vkclient.publishimage.attach.domain.entity

import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.NoBody
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.DomainContentResolver
import vsukharew.vkclient.publishimage.attach.domain.model.Image

class CheckUploadedImageSize(
    private val contentResolver: DomainContentResolver
) {
    fun checkUploadedImageSize(image: Image): Either<NoBody, AppError> {
        val fileSize = contentResolver.getFileSize(image.uri)
        val maxImageSize = 1024 * 1000 * 50  // 50MB
        return if (fileSize?.compareTo(maxImageSize) == 1) {
            Either.Right(AppError.DomainError.FileTooLargeError)
        } else {
            Either.Left(NoBody)
        }
    }
}