package vsukharew.vkclient.common.presentation.loadstate

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent

sealed class UIState<out T> {
    object LoadingProgress : UIState<Nothing>()
    object SwipeRefreshProgress : UIState<Nothing>()
    data class Success<T>(val data: T): UIState<T>()
    data class Error(val error: SingleLiveEvent<Result.Error>): UIState<Nothing>()
    data class SwipeRefreshError(val error: SingleLiveEvent<Result.Error>): UIState<Nothing>()
}