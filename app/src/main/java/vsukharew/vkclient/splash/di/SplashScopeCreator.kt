package vsukharew.vkclient.splash.di

import androidx.fragment.app.Fragment
import org.koin.core.Koin
import vsukharew.vkclient.common.di.DIScopes
import vsukharew.vkclient.common.di.ScopeCreator

class SplashScopeCreator(
    fragment: Fragment,
    koin: Koin
) : ScopeCreator(fragment, koin) {
    override val parentScopes: List<ScopeData> = listOf(
        ScopeData(
            DIScopes.AUTH_DATA,
            shouldCloseOnBackNavigation = true,
            shouldCloseOnForwardNavigation = false
        )
    )
}