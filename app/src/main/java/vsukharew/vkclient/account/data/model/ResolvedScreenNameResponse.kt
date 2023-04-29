package vsukharew.vkclient.account.data.model

sealed class ScreenNameResponse {
    object UnresolvedScreenNameResponse : ScreenNameResponse()
    object ResolvedScreenNameResponse : ScreenNameResponse()
}
