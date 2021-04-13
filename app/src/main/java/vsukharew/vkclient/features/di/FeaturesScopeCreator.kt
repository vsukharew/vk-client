package vsukharew.vkclient.features.di

import org.koin.core.Koin
import vsukharew.vkclient.common.di.DIScopes
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.features.presentation.FeaturesFlowFragment

class FeaturesScopeCreator(
    fragment: FeaturesFlowFragment,
    koin: Koin
) : ScopeCreator(fragment, koin) {
    override val parentScopes: List<ScopeData> = listOf(
        ScopeData(
            DIScopes.AUTH_DATA,
            shouldCloseOnBackNavigation = true,
            shouldCloseOnForwardNavigation = true
        )
    )
}