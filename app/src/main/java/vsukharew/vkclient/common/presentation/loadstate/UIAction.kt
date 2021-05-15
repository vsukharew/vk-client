package vsukharew.vkclient.common.presentation.loadstate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class UIAction {
    object InitialLoading : UIAction()
    object Click : UIAction()
    object SwipeRefresh : UIAction()
    object Retry : UIAction()
    @Parcelize
    data class Text(val text: String) : UIAction(), Parcelable
}