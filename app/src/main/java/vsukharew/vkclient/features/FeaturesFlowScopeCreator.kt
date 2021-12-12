package vsukharew.vkclient.features

import vsukharew.vkclient.common.di.ScopeCreator

object FeaturesFlowScopeCreator : ScopeCreator() {
    override val parentScopes: List<ScopeData> = emptyList()
}