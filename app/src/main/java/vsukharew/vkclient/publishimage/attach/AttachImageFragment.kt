package vsukharew.vkclient.publishimage.attach

import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentAttachImageBinding
import vsukharew.vkclient.publishimage.attach.di.AttachImageScopeCreator

class AttachImageFragment :
    BaseFragment<FragmentAttachImageBinding>(R.layout.fragment_attach_image) {

    override val scopeCreator: ScopeCreator = AttachImageScopeCreator
    override val binding by fragmentViewBinding(FragmentAttachImageBinding::bind)

}