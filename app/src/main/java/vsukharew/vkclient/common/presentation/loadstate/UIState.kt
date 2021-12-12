package vsukharew.vkclient.common.presentation.loadstate

import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.livedata.SingleLiveEvent

sealed class UIState<out T> {
    object LoadingProgress : UIState<Nothing>()
    object SwipeRefreshProgress : UIState<Nothing>()
    data class Success<T>(val data: T): UIState<T>()
    data class Error(val error: SingleLiveEvent<Either.Right<AppError>>): UIState<Nothing>()
    data class SwipeRefreshError<T>(
        val currentData: T,
        val error: SingleLiveEvent<Either.Right<AppError>>
    ): UIState<T>()
}