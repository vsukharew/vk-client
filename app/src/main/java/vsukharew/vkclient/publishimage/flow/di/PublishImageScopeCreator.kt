package vsukharew.vkclient.publishimage.flow.di

import org.koin.core.qualifier.named
import vsukharew.vkclient.common.di.DIScopes
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.di.ScopesIds

object PublishImageScopeCreator : ScopeCreator() {
    override val parentScopes: List<ScopeData> = listOf(
        ScopeData(
            ScopesIds.PUBLISHING_POST_DATA_SCOPE_ID,
            named(DIScopes.PUBLISHING_POST_DATA),
            shouldCloseOnBackNavigation = false,
            shouldCloseOnForwardNavigation = false
        )
    )
}