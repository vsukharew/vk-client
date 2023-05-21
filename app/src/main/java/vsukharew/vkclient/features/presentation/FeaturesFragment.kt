package vsukharew.vkclient.features.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.extension.setDistinctText
import vsukharew.vkclient.common.extension.snackBar
import vsukharew.vkclient.common.extension.textChangesSkipFirst
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.common.presentation.loadstate.ShortNameAvailabilityState
import vsukharew.vkclient.databinding.FragmentFeaturesBinding
import vsukharew.vkclient.features.di.FeaturesScopeCreator
import vsukharew.vkclient.features.navigation.FeaturesCoordinator

@OptIn(ExperimentalCoroutinesApi::class)
@FlowPreview
class FeaturesFragment : BaseFragment<FragmentFeaturesBinding>(R.layout.fragment_features) {
    private val featuresCoordinator: FeaturesCoordinator by inject()
    private var signOutDialog: AlertDialog? = null

    override val viewModel: FeaturesViewModel by stateViewModel()
    override val binding by fragmentViewBinding(FragmentFeaturesBinding::bind)
    override val scopeCreator: ScopeCreator = FeaturesScopeCreator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setProperties()
        observeData()
        observeUiEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.saveCursorPosition(binding.shortNameText.selectionEnd)
    }

    override fun onDestroy() {
        super.onDestroy()
        nullifyProperties()
        signOutDialog?.dismiss()
    }

    private fun observeData() {
        val uiState = viewModel.uiState
        val lifecycleScope = lifecycleScope
        val lifecycle = viewLifecycleOwner.lifecycle
        lifecycleScope.apply {
            viewModel.apply {
                launch {
                    uiState
                        .flowWithLifecycle(lifecycle)
                        .collectLatest(::collectUserNameText)
                }
                launch {
                    uiState
                        .map(FeaturesUiState::isShortNameHintVisible::get)
                        .flowWithLifecycle(lifecycle)
                        .collectLatest(binding.shortNameHint::isVisible::set)
                }
                launch {
                    uiState
                        .map(FeaturesUiState::isSwipeLayoutEnabled::get)
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collectLatest { binding.refreshLayout.isEnabled = it }
                }
                launch {
                    uiState
                        .map(FeaturesUiState::isSwipeLayoutRefreshing::get)
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collectLatest { binding.refreshLayout.isRefreshing = it }
                }
                launch {
                    uiState
                        .map(FeaturesUiState::isRetryVisible::get)
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collectLatest(binding.retry::isVisible::set)
                }
                launch {
                    uiState
                        .map(FeaturesUiState::isRetryVisible::get)
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collectLatest(binding.retry::isVisible::set)
                }
                launch {
                    uiState
                        .map(FeaturesUiState::isPublishImageButtonVisible::get)
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collectLatest(binding.publishImage::isVisible::set)
                }
                launch {
                    uiState
                        .map(FeaturesUiState::isSignOutVisible::get)
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collectLatest(binding.signOut::isVisible::set)
                }
                launch {
                    uiState
                        .map(FeaturesUiState::isShortNameInputVisible::get)
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collectLatest(binding.shortNameHint::isVisible::set)
                }
                launch {
                    uiState
                        .map(FeaturesUiState::currentShortName::get)
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collectLatest(binding.shortNameText::setDistinctText)
                }
                launch {
                    uiState
                        .map { it.shortNameInputStrokeColor to it.shortNameAvailabilityText }
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collectLatest(::collectShortNameInputUiState)
                }
                launch {
                    uiState
                        .map(FeaturesUiState::shortNameAvailabilityState::get)
                        .filterIsInstance<ShortNameAvailabilityState.Error>()
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                        .collectLatest { }
                }
                signOutEvent.observe(viewLifecycleOwner, ::observeSignOutEvent)
                signOutDialogEvent.observe(viewLifecycleOwner) { observeSignOutDialogEvent() }
                signOutDialogClosedEvent.observe(viewLifecycleOwner, ::observeSignOutEvent)
                postPublishedEvent.observe(viewLifecycleOwner, ::observePublishedPosts)
            }
        }
    }

    private fun collectUserNameText(uiState: FeaturesUiState) {
        with(uiState) {
            when (loadingState) {
                is FeaturesUiState.LoadingState.Error -> {
                    getString(R.string.empty)
                }
                is FeaturesUiState.LoadingState.MainLoading -> {
                    getString(R.string.features_fragment_user_name_loading_text)
                }
                else -> {
                    val formatArgs = uiState.profileInfo?.run { "$firstName $lastName" }.orEmpty()
                    getString(R.string.features_fragment_user_name_text, formatArgs)
                }
            }.let(binding.userName::setText)
        }
    }

    private fun observeUiEvents() {
        viewModel.onShortNameChanged(binding.shortNameText.textChangesSkipFirst())
    }

    private fun setListeners() {
        binding.apply {
            signOut.setOnClickListener { viewModel.onSignOutClick() }
            retry.setOnClickListener { viewModel.retryLoadProfileInfo() }
            publishImage.setOnClickListener { featuresCoordinator.onPublishImageClick() }
            refreshLayout.setOnRefreshListener { viewModel.refreshProfileInfo() }
        }
    }

    private fun setProperties() {
        featuresCoordinator.navController = navController
    }

    private fun nullifyProperties() {
        featuresCoordinator.navController = null
    }

    private fun collectShortNameInputUiState(resources: Pair<Int, Int>) {
        val (color, textResource) = resources
        binding.shortNameHint.apply {
            boxStrokeColor = color
            setHelperTextColor(ColorStateList.valueOf(color))
            helperText = getString(textResource)
        }
    }

    private fun observeSignOutDialogEvent() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.features_fragment_sign_out_dialog_text)
            .setPositiveButton(R.string.ok_text) { _, _ ->
                viewModel.onSignOutDialogClosed()
            }
            .create().also { signOutDialog = it }
            .show()
    }

    private fun observeSignOutEvent(event: SingleLiveEvent<Unit>) {
        event.getContentIfNotHandled()?.let {
            featuresCoordinator.onSignOutClick()
        }
    }

    private fun observePublishedPosts(event: SingleLiveEvent<Int?>) {
        event.getContentIfNotHandled()
            ?.let { snackBar(R.string.features_fragment_post_published_text) }
    }
}