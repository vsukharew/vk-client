package vsukharew.vkclient.features.presentation

import org.koin.java.KoinJavaComponent.getKoin
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentFeaturesBinding
import vsukharew.vkclient.features.di.FeaturesScopeCreator

class FeaturesFlowFragment : BaseFragment<FragmentFeaturesBinding>(R.layout.fragment_features) {
    override val binding by fragmentViewBinding(FragmentFeaturesBinding::bind)
    override val scopeCreator: ScopeCreator = FeaturesScopeCreator(this, getKoin())
}