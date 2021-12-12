package vsukharew.vkclient.common.navigation

import androidx.fragment.app.FragmentManager
import vsukharew.vkclient.auth.navigation.AuthScreen
import vsukharew.vkclient.auth.presentation.AuthFragment

class AuthScreenDeepLinkEndPoint(
    private val fragmentManager: FragmentManager,
    private val screen: AuthScreen
) : DeepLinkEndPoint {
    override val pattern: Regex
        get() = Regex("https://oauth\\.vk\\.com/blank\\.html.*")
    override val blockToRunIfPatternMatches: () -> Unit
        get() = {
            val authFragment = fragmentManager.findFragmentByTag(screen.screenKey) as? AuthFragment
            authFragment?.apply { activity?.intent?.let(::handleBrowserRedirect) }
        }
}