package vsukharew.vkclient.screenname.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ScreenNameAvailability : Parcelable {
    AVAILABLE,
    UNAVAILABLE,
    CURRENT_USER_NAME,
    EMPTY
}