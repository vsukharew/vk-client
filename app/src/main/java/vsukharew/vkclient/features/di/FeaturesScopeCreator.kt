package vsukharew.vkclient.features.di

import org.koin.core.Koin
import org.koin.core.qualifier.named
import vsukharew.vkclient.common.di.DIScopes
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.di.ScopesIds
import vsukharew.vkclient.features.presentation.FeaturesFragment

class FeaturesScopeCreator(
    fragment: FeaturesFragment,
    koin: Koin
) : ScopeCreator(fragment, koin) {
    override val parentScopes: List<ScopeData> = listOf(
        ScopeData(
            ScopesIds.AUTH_DATA_SCOPE_ID,
            named(DIScopes.AUTH_DATA),
            shouldCloseOnBackNavigation = true,
            shouldCloseOnForwardNavigation = true
        ),
        ScopeData(
            ScopesIds.ACCOUNT_SCOPE_ID,
            named(DIScopes.ACCOUNT),
            shouldCloseOnBackNavigation = true,
            shouldCloseOnForwardNavigation = true
        )
    )
}