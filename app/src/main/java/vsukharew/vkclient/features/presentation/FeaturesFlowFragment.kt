package vsukharew.vkclient.features.presentation

import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentFeaturesBinding

class FeaturesFlowFragment : BaseFragment<FragmentFeaturesBinding>(R.layout.fragment_features) {
    override val binding by fragmentViewBinding(FragmentFeaturesBinding::bind)
}