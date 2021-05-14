package vsukharew.vkclient.publishimage.flow

import android.os.Bundle
import android.view.View
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.presentation.BaseFlowFragment
import vsukharew.vkclient.databinding.FragmentPublishImageBinding
import vsukharew.vkclient.publishimage.flow.PublishImageViewModel.PublishImageStage
import vsukharew.vkclient.publishimage.flow.PublishImageViewModel.PublishImageStage.ATTACH_IMAGE
import vsukharew.vkclient.publishimage.flow.PublishImageViewModel.PublishImageStage.CAPTION
import vsukharew.vkclient.publishimage.flow.di.PublishImageScopeCreator
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

class PublishImageFragment :
    BaseFlowFragment<FragmentPublishImageBinding>(R.layout.fragment_publish_image) {
    private val coordinator: PublishImageCoordinator by inject()
    private val viewModel: PublishImageViewModel by stateViewModel()

    override val fragmentContainerViewId: Int = R.id.publish_images_flow_container
    override val scopeCreator: PublishImageScopeCreator = PublishImageScopeCreator
    override val binding by fragmentViewBinding(FragmentPublishImageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNavigation()
        setProperties()
        observeData()
    }

    private fun observeData() {
        viewModel.apply {
            currentStageLiveData.observe(viewLifecycleOwner, ::observeCurrentStage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        nullifyProperties()
    }

    private fun configureNavigation() {
        navigateIfDestinationIsNotCreated(R.id.attachImageFragment)
        flowNavController.addOnDestinationChangedListener { _, destination, _ ->
            val (stage, title) = when (destination.id) {
                R.id.attachImageFragment -> ATTACH_IMAGE to  R.string.attach_image_fragment_title
                R.id.captionFragment -> CAPTION to R.string.caption_fragment_title
                else -> ATTACH_IMAGE to R.string.app_name
            }
            requireActivity().title = getString(title)
            viewModel.onStageChanged(stage)
        }
    }

    private fun observeCurrentStage(stage: PublishImageStage) {
        coordinator.setCurrentStage(stage)
    }

    private fun setProperties() {
        coordinator.let {
            it.rootNavController = navController
            it.flowNavController = flowNavController
        }
    }

    private fun nullifyProperties() {
        coordinator.apply {
            rootNavController = null
            flowNavController = null
        }
    }
}