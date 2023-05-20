package vsukharew.vkclient.common.presentation.loadstate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class FeaturesScreenAction {
    object InitialLoading : FeaturesScreenAction()
    object SwipeRefresh : FeaturesScreenAction()
    object Retry : FeaturesScreenAction()
    @Parcelize
    data class Text(val text: String) : FeaturesScreenAction(), Parcelable
}

enum class FeaturesResetType {
    MAIN_LOADING,
    SWIPE_REFRESH
}