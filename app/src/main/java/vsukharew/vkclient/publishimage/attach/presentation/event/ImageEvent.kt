package vsukharew.vkclient.publishimage.attach.presentation.event

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage

sealed class ImageEvent(val image: UIImage) {
    class InitialLoading(
        image: UIImage,
        val progressLoading: Int = 0
    ) : ImageEvent(image)
    class SuccessfulLoading(image: UIImage) : ImageEvent(image)
    class Pending(image: UIImage, val isRetryLoading: Boolean) : ImageEvent(image)
    class ErrorLoading(image: UIImage, val error: Result.Error) : ImageEvent(image)
    class Retry(image: UIImage) : ImageEvent(image)
    class Remove(image: UIImage) : ImageEvent(image)
}