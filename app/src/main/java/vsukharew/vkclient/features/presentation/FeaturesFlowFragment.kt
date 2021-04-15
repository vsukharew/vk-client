package vsukharew.vkclient.features.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.getKoin
import vsukharew.vkclient.R
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.extension.EMPTY
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.common.presentation.loadstate.UIState
import vsukharew.vkclient.databinding.FragmentFeaturesBinding
import vsukharew.vkclient.features.di.FeaturesScopeCreator
import vsukharew.vkclient.features.navigation.FeaturesCoordinator

class FeaturesFlowFragment : BaseFragment<FragmentFeaturesBinding>(R.layout.fragment_features) {
    private val viewModel: FeaturesViewModel by viewModel()
    private val featuresCoordinator: FeaturesCoordinator by inject()

    override val binding by fragmentViewBinding(FragmentFeaturesBinding::bind)
    override val scopeCreator: ScopeCreator = FeaturesScopeCreator(this, getKoin())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setProperties()
        viewModel.apply {
            profileUiState.observe(viewLifecycleOwner, ::observeUiState)
            signOutEvent.observe(viewLifecycleOwner, ::observeSignOutEvent)
            signOutDialogEvent.observe(viewLifecycleOwner, ::observeSignOutDialogEvent)
            signOutDialogClosedEvent.observe(viewLifecycleOwner, ::observeSignOutEvent)
        }
    }

    private fun setListeners() {
        binding.apply {
            signOut.setOnClickListener { viewModel.onSignOutClick() }
            retry.setOnClickListener { viewModel.retryLoadProfileInfo() }
            refreshLayout.setOnRefreshListener { viewModel.refreshProfileInfo() }
        }
    }

    private fun setProperties() {
        featuresCoordinator.navController = navController
    }

    private fun nullifyProperties() {
        featuresCoordinator.navController = null
    }

    private fun observeUiState(state: UIState<ProfileInfo>) {
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

    private fun observeSignOutDialogEvent(event: Unit) {
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

    private fun renderLoadingProgress() {
        binding.apply {
            refreshLayout.isEnabled = false
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
            signOut.isVisible = true
            userName.text = with(state.data) {
                getString(
                    R.string.features_fragment_user_name_text,
                    "${data.firstName} ${data.lastName}"
                )
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
        }
        state.error.getContentIfNotHandled()?.let(::handleError)
    }

    override fun onDestroy() {
        super.onDestroy()
        nullifyProperties()
    }
}