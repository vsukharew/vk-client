package vsukharew.vkclient.publishimage.caption.presentation.state

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent

sealed class CaptionUIState {
    object LoadingProgress : CaptionUIState()
    data class Success(val postId: Int) : CaptionUIState()
    data class Error(val error: SingleLiveEvent<Result.Error>) : CaptionUIState()
}