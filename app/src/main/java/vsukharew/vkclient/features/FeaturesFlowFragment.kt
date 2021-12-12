package vsukharew.vkclient.features

import android.os.Bundle
import android.view.View
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFlowFragment
import vsukharew.vkclient.databinding.FragmentFlowFeaturesBinding

class FeaturesFlowFragment : BaseFlowFragment<FragmentFlowFeaturesBinding>(R.layout.fragment_flow_features) {
    override val fragmentContainerViewId: Int = R.id.fragment_container_view
    override val scopeCreator: ScopeCreator = FeaturesFlowScopeCreator
    override val binding by fragmentViewBinding(FragmentFlowFeaturesBinding::bind)
    override val viewModel: FeaturesFlowViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        navigateIfDestinationIsNotCreated(R.id.features_fragment)
    }
}