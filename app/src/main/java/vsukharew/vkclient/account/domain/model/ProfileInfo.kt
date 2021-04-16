package vsukharew.vkclient.account.domain.model

import vsukharew.vkclient.common.extension.EMPTY

data class ProfileInfo(
    val firstName: String,
    val lastName: String,
    val screenName: String?
) {
    companion object {
        val EMPTY = ProfileInfo(String.EMPTY, String.EMPTY, null)
    }
}
