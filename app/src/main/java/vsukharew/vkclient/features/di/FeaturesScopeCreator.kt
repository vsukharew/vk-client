package vsukharew.vkclient.features.di

import org.koin.core.Koin
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.features.presentation.FeaturesFlowFragment

class FeaturesScopeCreator(
    fragment: FeaturesFlowFragment,
    koin: Koin
) : ScopeCreator(fragment, koin) {
    override val parentScopes: List<ScopeData> = emptyList()
}