package vsukharew.vkclient.publishimage.attach.presentation.model

import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import java.io.Serializable

sealed class UIImage : Serializable {
    object AddNewImagePlaceholder : UIImage()
    data class RealImage(
        val image: Image,
        val loadingState: ImageLoadingState
    ) : UIImage()
}

sealed class ImageLoadingState : Serializable {
    object Pending : ImageLoadingState()
    data class LoadingProgress(val progress: Int = 0) : ImageLoadingState()
    object Success : ImageLoadingState(), Serializable
    data class Error(val error: AppError) : ImageLoadingState()
}