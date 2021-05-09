package vsukharew.vkclient.features.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.extension.EMPTY
import vsukharew.vkclient.common.extension.snackBar
import vsukharew.vkclient.common.extension.textChangesSkipFirst
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.common.presentation.loadstate.UIState
import vsukharew.vkclient.databinding.FragmentFeaturesBinding
import vsukharew.vkclient.features.di.FeaturesScopeCreator
import vsukharew.vkclient.features.navigation.FeaturesCoordinator
import vsukharew.vkclient.screenname.model.ScreenNameAvailability
import vsukharew.vkclient.screenname.model.ScreenNameAvailability.*

@FlowPreview
class FeaturesFragment : BaseFragment<FragmentFeaturesBinding>(R.layout.fragment_features) {
    private val viewModel: FeaturesViewModel by viewModel()
    private val featuresCoordinator: FeaturesCoordinator by inject()

    override val binding by fragmentViewBinding(FragmentFeaturesBinding::bind)
    override val scopeCreator: ScopeCreator = FeaturesScopeCreator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setProperties()
        observeData()
        observeUiEvents()
    }

    override fun onDestroy() {
        super.onDestroy()
        nullifyProperties()
    }

    private fun observeData() {
        viewModel.apply {
            profileUiState.observe(viewLifecycleOwner, ::observeProfileUiState)
            shortNameUiState.observe(viewLifecycleOwner, ::observeShortNameUiState)
            shortNameTextState.observe(viewLifecycleOwner, ::observeShortNameTextState)
            signOutEvent.observe(viewLifecycleOwner, ::observeSignOutEvent)
            signOutDialogEvent.observe(viewLifecycleOwner, { observeSignOutDialogEvent() })
            signOutDialogClosedEvent.observe(viewLifecycleOwner, ::observeSignOutEvent)
            postPublishedEvent.observe(viewLifecycleOwner, ::observePublishedPosts)
        }
    }

    private fun observeUiEvents() {
        binding.shortNameText
            .textChangesSkipFirst()
            .debounce(500L)
            .onEach(viewModel::onShortNameChanged)
            .launchIn(viewModel.viewModelScope)
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

    private fun observeProfileUiState(state: UIState<ProfileInfo>) {
        when (state) {
            UIState.LoadingProgress -> renderLoadingProgress()
            UIState.SwipeRefreshProgress -> {
                // empty implementation
            }
            is UIState.Success -> renderSuccessState(state)
            is UIState.SwipeRefreshError -> renderSwipeRefreshErrorState(state)
            is UIState.Error -> renderErrorState(state)
        }
    }

    private fun observeShortNameUiState(state: UIState<ScreenNameAvailability>) {
        when (state) {
            UIState.LoadingProgress -> renderLoadingShortNameState()
            is UIState.Success -> renderSuccessShortNameState(state)
            is UIState.Error -> renderErrorShortNameState(state)
            else -> {

            }
        }
    }

    private fun observeShortNameTextState(text: String) {
        binding.shortNameText.apply {
            if (getText()?.toString() != text) {
                setText(text)
            }
        }
    }

    private fun observeSignOutDialogEvent() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.features_fragment_sign_out_dialog_text)
            .setPositiveButton(R.string.ok_text) { _, _ ->
                viewModel.onSignOutDialogClosed()
            }
            .create()
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

    private fun renderLoadingProgress() {
        binding.apply {
            shortNameHint.isVisible = false
            refreshLayout.isEnabled = false
            publishImage.isVisible = false
            userName.text = getString(R.string.features_fragment_user_name_loading_text)
            signOut.isVisible = false
            retry.isVisible = false
        }
    }

    private fun renderSuccessState(state: UIState.Success<ProfileInfo>) {
        binding.apply {
            refreshLayout.isEnabled = true
            refreshLayout.isRefreshing = false
            retry.isVisible = false
            publishImage.isVisible = true
            signOut.isVisible = true
            shortNameHint.isVisible = true
            with(state.data) {
                userName.text =
                    getString(
                        R.string.features_fragment_user_name_text,
                        "$firstName $lastName"
                    )
                shortNameText.setText(screenName)
            }
        }
    }

    private fun renderSwipeRefreshErrorState(state: UIState.SwipeRefreshError) {
        binding.refreshLayout.isRefreshing = false
        state.error.getContentIfNotHandled()?.let(::handleError)
    }

    private fun renderErrorState(state: UIState.Error) {
        binding.apply {
            userName.text = String.EMPTY
            signOut.isVisible = false
            retry.isVisible = true
            refreshLayout.isRefreshing = false
            shortNameHint.isVisible = false
            publishImage.isVisible = false
        }
        state.error.getContentIfNotHandled()?.let(::handleError)
    }

    private fun renderLoadingShortNameState() {
        binding.shortNameHint.apply {
            boxStrokeColor = Color.BLUE
            setHelperTextColor(ColorStateList.valueOf(boxStrokeColor))
            helperText = getString(R.string.features_fragment_username_is_being_checked_text)
        }
    }

    private fun renderSuccessShortNameState(state: UIState.Success<ScreenNameAvailability>) {
        binding.shortNameHint.apply {
            val (color, text) = when (state.data) {
                AVAILABLE -> Color.GREEN to R.string.features_fragment_username_available_text
                UNAVAILABLE -> Color.RED to R.string.features_fragment_username_busy_text
                else -> Color.BLUE to R.string.empty
            }
            boxStrokeColor = color
            setHelperTextColor(ColorStateList.valueOf(color))
            helperText = getString(text)
        }
    }

    private fun renderErrorShortNameState(state: UIState.Error) {
        binding.shortNameHint.apply {
            boxStrokeColor = Color.RED
            setHelperTextColor(ColorStateList.valueOf(boxStrokeColor))
            helperText = getString(R.string.features_fragment_username_error_hint)
        }
        state.error.getContentIfNotHandled()?.let(::handleError)
    }
}