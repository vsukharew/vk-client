package vsukharew.vkclient.publishimage.caption.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import org.koin.android.ext.android.inject
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentCaptionBinding
import vsukharew.vkclient.publishimage.caption.di.CaptionScopeCreator
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

class CaptionFragment : BaseFragment<FragmentCaptionBinding>(R.layout.fragment_caption) {
    private val flowCoordinator: PublishImageCoordinator by inject()

    override val binding: FragmentCaptionBinding by fragmentViewBinding(FragmentCaptionBinding::bind)
    override val scopeCreator: ScopeCreator by lazy {
        CaptionScopeCreator(requireParentFragment().requireParentFragment())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    flowCoordinator.onBackClick()
                }
            })
    }
}