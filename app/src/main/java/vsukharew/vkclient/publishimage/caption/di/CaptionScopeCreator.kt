package vsukharew.vkclient.publishimage.caption.di

import androidx.fragment.app.Fragment
import org.koin.core.qualifier.qualifier
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.extension.fragmentRetainedScope
import vsukharew.vkclient.publishimage.flow.PublishImageFragment

class CaptionScopeCreator(parentFlowFragment: Fragment) : ScopeCreator() {
    override val parentScopes: List<ScopeData> = listOf(
        ScopeData(
            parentFlowFragment.fragmentRetainedScope().value.id,
            qualifier<PublishImageFragment>(),
            shouldCloseOnBackNavigation = false,
            shouldCloseOnForwardNavigation = false
        )
    )
}