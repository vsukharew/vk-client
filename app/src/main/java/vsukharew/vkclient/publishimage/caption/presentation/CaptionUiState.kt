package vsukharew.vkclient.publishimage.caption.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class CaptionUiState(
    val isLoadingInProgress: Boolean = false,
    val caption: String? = null,
    val shouldNavigateTo: NavigateTo = NavigateTo.Nothing,
    val shouldStartPermissionLauncher: Boolean = false,
) {
    val isPublishButtonEnabled: Boolean = !caption.isNullOrBlank()

    @Parcelize
    sealed class NavigateTo : Parcelable {
        object Nothing : NavigateTo()
        object SystemSettings : NavigateTo()
        object LocationSettings : NavigateTo()
    }
}
