package vsukharew.vkclient.publishimage.caption.presentation.state

import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.livedata.SingleLiveEvent

sealed class CaptionUIState {
    object LoadingProgress : CaptionUIState()
    data class Success(val postId: Int) : CaptionUIState()
    data class Error(val error: SingleLiveEvent<Either.Left<AppError>>) : CaptionUIState()
}