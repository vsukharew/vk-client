package vsukharew.vkclient.publishimage.flow

import android.os.Bundle
import android.view.View
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFlowFragment
import vsukharew.vkclient.databinding.FragmentPublishImageBinding
import vsukharew.vkclient.publishimage.flow.di.PublishImageScopeCreator

class PublishImageFragment :
    BaseFlowFragment<FragmentPublishImageBinding>(R.layout.fragment_publish_image) {

    override val fragmentContainerViewId: Int = R.id.publish_images_flow_container
    override val scopeCreator: ScopeCreator = PublishImageScopeCreator
    override val binding by fragmentViewBinding(FragmentPublishImageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flowNavController.navigate(R.id.attachImageFragment)
    }
}