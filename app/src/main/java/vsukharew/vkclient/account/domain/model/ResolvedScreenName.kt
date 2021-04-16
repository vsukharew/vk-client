package vsukharew.vkclient.account.domain.model

sealed class ScreenName {
    object UnresolvedScreenName : ScreenName()
    data class ResolvedScreenName(
        val objectId: Int,
        val type: String
    ) : ScreenName()
}
