package vsukharew.vkclient.account.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import vsukharew.vkclient.common.extension.EMPTY

@Parcelize
data class ProfileInfo(
    val firstName: String,
    val lastName: String,
    val screenName: String?
) : Parcelable {
    companion object {
        val EMPTY = ProfileInfo(String.EMPTY, String.EMPTY, null)
    }
}
