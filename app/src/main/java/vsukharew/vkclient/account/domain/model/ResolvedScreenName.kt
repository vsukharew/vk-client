package vsukharew.vkclient.account.domain.model

sealed class ScreenName {
    object UnresolvedScreenName : ScreenName()
    object ResolvedScreenName : ScreenName()
}
