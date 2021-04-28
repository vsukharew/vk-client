package vsukharew.vkclient.publishimage.attach.di

import androidx.fragment.app.Fragment
import org.koin.core.component.getScopeId
import org.koin.core.qualifier.qualifier
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.publishimage.flow.PublishImageFragment

class AttachImageScopeCreator(parentFlowFragment: Fragment) : ScopeCreator() {
    override val parentScopes: List<ScopeData> = listOf(
        ScopeData(
            parentFlowFragment.getScopeId(),
            qualifier<PublishImageFragment>(),
            shouldCloseOnBackNavigation = true,
            shouldCloseOnForwardNavigation = false
        )
    )
}