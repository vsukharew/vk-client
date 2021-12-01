package vsukharew.vkclient.common.presentation.loadstate

import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent

sealed class UIState<out T> {
    object LoadingProgress : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val error: SingleLiveEvent<Result.Error>) : UIState<Nothing>()
}

sealed class ProfileInfoUiState {
    data class SwipeRefreshState(val isEnabled: Boolean, val isRefreshing: Boolean)

    abstract val isShortNameHintVisible: Boolean
    abstract val isPublishImageVisible: Boolean
    abstract val isSignOutVisible: Boolean
    abstract val isRetryVisible: Boolean
    abstract val swipeRefreshState: SwipeRefreshState

    object LoadingProgress : ProfileInfoUiState() {
        override val isShortNameHintVisible: Boolean = false
        override val isPublishImageVisible: Boolean = false
        override val isSignOutVisible: Boolean = false
        override val isRetryVisible: Boolean = false
        override val swipeRefreshState: SwipeRefreshState =
            SwipeRefreshState(isEnabled = false, isRefreshing = false)
    }

    object SwipeRefreshProgress : ProfileInfoUiState() {
        override val swipeRefreshState: SwipeRefreshState
            get() = TODO("Not yet implemented")
        override val isShortNameHintVisible: Boolean
            get() = TODO("Not yet implemented")
        override val isPublishImageVisible: Boolean
            get() = TODO("Not yet implemented")
        override val isSignOutVisible: Boolean
            get() = TODO("Not yet implemented")
        override val isRetryVisible: Boolean
            get() = TODO("Not yet implemented")
    }

    data class Success(val data: ProfileInfo) : ProfileInfoUiState() {
        override val isShortNameHintVisible: Boolean = true
        override val isPublishImageVisible: Boolean = true
        override val isSignOutVisible: Boolean = true
        override val isRetryVisible: Boolean = false
        override val swipeRefreshState: SwipeRefreshState = SwipeRefreshState(
            isEnabled = true,
            isRefreshing = false
        )
    }

    data class Error(val error: SingleLiveEvent<Result.Error>) : ProfileInfoUiState() {
        override val isShortNameHintVisible: Boolean = false
        override val isPublishImageVisible: Boolean = false
        override val isSignOutVisible: Boolean = false
        override val isRetryVisible: Boolean = true
        override val swipeRefreshState: SwipeRefreshState = SwipeRefreshState(
            isEnabled = true,
            isRefreshing = false
        )
    }

    data class SwipeRefreshError(
        val currentData: ProfileInfo,
        val error: SingleLiveEvent<Result.Error>
    ) : ProfileInfoUiState() {
        override val isShortNameHintVisible: Boolean = true
        override val isPublishImageVisible: Boolean = true
        override val isSignOutVisible: Boolean = true
        override val isRetryVisible: Boolean = false
        override val swipeRefreshState: SwipeRefreshState = SwipeRefreshState(
            isEnabled = true,
            isRefreshing = false
        )
    }
}