package vsukharew.vkclient.publishimage.attach.presentation.event

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage

sealed class ImageEvent(open val image: UIImage) {
    data class InitialLoading(
        override val image: UIImage,
        val progressLoading: Int = 0
    ) : ImageEvent(image)
    data class SuccessfulLoading(override val image: UIImage) : ImageEvent(image)
    data class Pending(override val image: UIImage, val isRetryLoading: Boolean) : ImageEvent(image)
    data class ErrorLoading(override val image: UIImage, val error: Result.Error) : ImageEvent(image)
    data class Retry(override val image: UIImage) : ImageEvent(image)
    data class Remove(override val image: UIImage) : ImageEvent(image)
}