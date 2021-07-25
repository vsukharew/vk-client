package vsukharew.vkclient.publishimage.attach.presentation.state

import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage

sealed class ImageUIState {
    data class Pending(val isAfterRetry: Boolean) : ImageUIState()
    data class LoadingProgress(val progress: Int = 0) : ImageUIState()
    data class Success (val data: UIImage): ImageUIState()
    data class Error(val error: SingleLiveEvent<Either.Error>): ImageUIState()
}