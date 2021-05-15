package vsukharew.vkclient.auth.di

import org.koin.core.qualifier.named
import vsukharew.vkclient.common.di.DIScopes
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.di.ScopesIds

object AuthScopeCreator : ScopeCreator() {
    override val parentScopes: List<ScopeData> = listOf(
        ScopeData(
            ScopesIds.AUTH_DATA_SCOPE_ID,
            named(DIScopes.AUTH_DATA),
            shouldCloseOnBackNavigation = true,
            shouldCloseOnForwardNavigation = false
        )
    )
}