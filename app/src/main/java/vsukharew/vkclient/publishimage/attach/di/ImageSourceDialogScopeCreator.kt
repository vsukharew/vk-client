package vsukharew.vkclient.publishimage.attach.di

import androidx.fragment.app.Fragment
import org.koin.core.qualifier.qualifier
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.extension.fragmentRetainedScope
import vsukharew.vkclient.common.presentation.BaseFragment

class ImageSourceDialogScopeCreator(parentFlowFragment: Fragment) : ScopeCreator() {
    override val parentScopes: List<ScopeData> = listOf(
        ScopeData(
            parentFlowFragment.fragmentRetainedScope().value.id,
            qualifier<BaseFragment<*>>(),
            shouldCloseOnBackNavigation = true,
            shouldCloseOnForwardNavigation = false
        )
    )
}