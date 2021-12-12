package vsukharew.vkclient.common.navigation

interface DeepLinkEndPoint {
    val pattern: Regex
    val blockToRunIfPatternMatches: () -> Unit
}