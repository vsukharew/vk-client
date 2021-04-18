package vsukharew.vkclient.publishimage.attach.presentation

import android.os.Bundle
import android.view.View
import vsukharev.anytypeadapter.adapter.AnyTypeAdapter
import vsukharev.anytypeadapter.adapter.AnyTypeCollection
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentAttachImageBinding
import vsukharew.vkclient.publishimage.attach.di.AttachImageScopeCreator
import vsukharew.vkclient.publishimage.attach.presentation.delegate.AddNewImageDelegate

class AttachImageFragment :
    BaseFragment<FragmentAttachImageBinding>(R.layout.fragment_attach_image) {

    private val anyTypeAdapter = AnyTypeAdapter()

    override val scopeCreator: ScopeCreator = AttachImageScopeCreator
    override val binding by fragmentViewBinding(FragmentAttachImageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        AnyTypeCollection.Builder()
            .add(AddNewImageDelegate())
            .build()
            .let { anyTypeAdapter.setCollection(it) }
    }

    private fun initRecycler() {
        binding.attachedImages.apply {
            adapter = anyTypeAdapter
        }
    }
}