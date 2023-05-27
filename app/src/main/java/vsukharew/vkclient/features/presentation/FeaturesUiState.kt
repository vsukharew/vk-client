package vsukharew.vkclient.features.presentation

import android.graphics.Color
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import kotlinx.parcelize.Parcelize
import vsukharew.vkclient.R
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.presentation.loadstate.ShortNameAvailabilityState
import vsukharew.vkclient.features.presentation.FeaturesUiState.LoadingState.*
import vsukharew.vkclient.screenname.model.ScreenNameAvailability

@Suppress("PROPERTY_WONT_BE_SERIALIZED")
@Parcelize
data class FeaturesUiState(
    val loadingState: LoadingState,
    val profileInfo: ProfileInfo?,
    val currentShortName: String?,
    val shortNameAvailabilityState: ShortNameAvailabilityState,
    val shouldNavigateTo: NavigateTo
): Parcelable {

    val initialShortName = profileInfo?.screenName.orEmpty()
    val isShortNameHintVisible = with(loadingState) {
        this is Loaded || this is SwipeRefresh || this is SwipeRefreshError
    }
    val isSwipeLayoutEnabled = when (loadingState) {
        is MainLoading -> false
        is Error -> false
        is SwipeRefresh -> true
        is Loaded -> true
        is SwipeRefreshError -> true
    }
    val isSwipeLayoutRefreshing = loadingState is SwipeRefresh
    val isRetryVisible = loadingState is Error
    val isPublishImageButtonVisible = when (loadingState) {
        is MainLoading -> false
        is Error -> false
        is SwipeRefresh -> true
        is Loaded -> true
        is SwipeRefreshError -> true
    }
    val isSignOutVisible = when (loadingState) {
        is MainLoading -> false
        is Error -> false
        is SwipeRefresh -> true
        is Loaded -> true
        is SwipeRefreshError -> true
    }
    val isShortNameInputVisible = when (loadingState) {
        is MainLoading -> false
        is Error -> false
        is SwipeRefresh -> true
        is Loaded -> true
        is SwipeRefreshError -> true
    }
    val shortNameInputStrokeColor = when (shortNameAvailabilityState) {
        is ShortNameAvailabilityState.LoadingProgress -> Color.BLUE
        is ShortNameAvailabilityState.Success -> {
            when (shortNameAvailabilityState.data) {
                ScreenNameAvailability.AVAILABLE -> Color.GREEN
                ScreenNameAvailability.UNAVAILABLE -> Color.RED
                else -> Color.BLUE
            }
        }
        is ShortNameAvailabilityState.Error -> Color.RED
    }
    val shortNameAvailabilityText = when (shortNameAvailabilityState) {
        is ShortNameAvailabilityState.LoadingProgress -> R.string.features_fragment_username_is_being_checked_text
        is ShortNameAvailabilityState.Success -> {
            when (shortNameAvailabilityState.data) {
                ScreenNameAvailability.AVAILABLE -> R.string.features_fragment_username_available_text
                ScreenNameAvailability.UNAVAILABLE -> R.string.features_fragment_username_busy_text
                else -> R.string.empty
            }
        }
        is ShortNameAvailabilityState.Error -> R.string.features_fragment_username_error_hint
    }

    fun save(savedStateHandle: SavedStateHandle) {
        savedStateHandle[KEY_SAVED_STATE] = this
    }

    @Parcelize
    sealed class LoadingState : Parcelable {
        object MainLoading : LoadingState()
        object Error : LoadingState()
        data class SwipeRefresh(val isEnabled: Boolean, val isRefreshing: Boolean) : LoadingState()
        object Loaded : LoadingState()
        object SwipeRefreshError : LoadingState()
    }

    @Parcelize
    sealed class NavigateTo : Parcelable {
        object Nothing : NavigateTo()
        object SignInScreen : NavigateTo()
    }

    companion object {
        val DEFAULT = FeaturesUiState(
            loadingState = MainLoading,
            profileInfo = null,
            currentShortName = null,
            shortNameAvailabilityState = ShortNameAvailabilityState.Success(ScreenNameAvailability.EMPTY),
            shouldNavigateTo = NavigateTo.Nothing
        )
        const val KEY_SAVED_STATE = "saved_state"
    }
}