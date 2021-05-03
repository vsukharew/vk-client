package vsukharew.vkclient.publishimage.caption.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentCaptionBinding
import vsukharew.vkclient.publishimage.caption.di.CaptionScopeCreator
import vsukharew.vkclient.publishimage.caption.presentation.state.CaptionUIState
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

class CaptionFragment : BaseFragment<FragmentCaptionBinding>(R.layout.fragment_caption) {
    private val flowCoordinator: PublishImageCoordinator by inject()
    private val viewModel: CaptionViewModel by viewModel()

    override val binding: FragmentCaptionBinding by fragmentViewBinding(FragmentCaptionBinding::bind)
    override val scopeCreator: ScopeCreator by lazy {
        CaptionScopeCreator(requireParentFragment().requireParentFragment())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerCallbacks()
        setListeners()
        observeData()
    }

    private fun registerCallbacks() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    flowCoordinator.currentStage.onBackClick()
                }
            })
    }

    private fun setListeners() {
        binding.apply {
            captionText.doOnTextChanged { text, _, _, _ ->
                publish.isEnabled = text?.isNotEmpty() ?: false
            }
            publish.setOnClickListener {
                captionText.text?.toString()?.let(viewModel::publish)
            }
        }
    }

    private fun observeData() {
        viewModel.apply {
            publishingState.observe(viewLifecycleOwner, ::observePublishingState)
        }
    }

    private fun observePublishingState(state: CaptionUIState) {
        binding.apply {
            progressBar.isVisible = state is CaptionUIState.LoadingProgress
            publish.isVisible = state !is CaptionUIState.LoadingProgress
            if (state is CaptionUIState.Error) {
                state.error.getContentIfNotHandled()?.let(::handleError)
            }
        }
    }
}