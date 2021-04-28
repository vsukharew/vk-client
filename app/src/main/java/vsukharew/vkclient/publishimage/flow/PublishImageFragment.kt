package vsukharew.vkclient.publishimage.flow

import android.os.Bundle
import android.view.View
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFlowFragment
import vsukharew.vkclient.databinding.FragmentPublishImageBinding
import vsukharew.vkclient.publishimage.flow.di.PublishImageScopeCreator
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

class PublishImageFragment :
    BaseFlowFragment<FragmentPublishImageBinding>(R.layout.fragment_publish_image) {
    private val viewModel: PublishImageViewModel by viewModel()
    private val coordinator: PublishImageCoordinator by inject()

    override val fragmentContainerViewId: Int = R.id.publish_images_flow_container
    override val scopeCreator: ScopeCreator = PublishImageScopeCreator
    override val binding by fragmentViewBinding(FragmentPublishImageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateIfDestinationIsNotCreated(R.id.attachImageFragment)
        setListeners()
        observeData()
        coordinator.rootNavController = navController
    }

    private fun setListeners() {
        binding.apply { backBtn.setOnClickListener { coordinator.onBackClick() } }
    }

    private fun observeData() {
        viewModel.isNextButtonAvailable.observe(viewLifecycleOwner, ::observeNextButtonAvailability)
    }

    private fun observeNextButtonAvailability(isEnabled: Boolean) {
        binding.nextBtn.isEnabled = isEnabled
    }
}