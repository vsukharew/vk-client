package vsukharew.vkclient.features.presentation

import android.content.DialogInterface
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
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.extension.EMPTY
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseFragment
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
            isLoading.observe(viewLifecycleOwner, ::observeLoading)
            profileInfo.observe(viewLifecycleOwner, ::observeProfileInfo)
            signOutButtonVisible.observe(viewLifecycleOwner, ::observeSignOutEvent)
            signOutEvent.observe(viewLifecycleOwner, ::observeSignOutEvent)
            signOutDialogEvent.observe(viewLifecycleOwner, ::observeSignOutDialogEvent)
        }
    }

    private fun setListeners() {
        binding.signOut.setOnClickListener { viewModel.onSignOutClick() }
    }

    private fun setProperties() {
        featuresCoordinator.navController = navController
    }

    private fun nullifyProperties() {
        featuresCoordinator.navController = null
    }

    private fun observeProfileInfo(event: SingleLiveEvent<Result<ProfileInfo>>) {
        with(event) {
            when (peekContent) {
                is Result.Success -> {
                    binding.userName.text =
                        getString(
                            R.string.features_fragment_user_name_text,
                            "${peekContent.data.firstName} ${peekContent.data.lastName}"
                        )
                }
                is Result.Error -> {
                    if (!isHandled) { // check so that snackbars or other actions aren't fired twice
                        isHandled = true
                        handleError(peekContent)
                    }
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun observeLoading(isLoading: Boolean) {
        binding.userName.apply {
            text = if (isLoading) {
                getString(R.string.features_fragment_user_name_loading_text)
            } else {
                String.EMPTY
            }
        }
    }

    private fun observeSignOutEvent(isVisible: Boolean) {
        binding.signOut.isVisible = isVisible
    }

    private fun observeSignOutEvent(event: SingleLiveEvent<Unit>) {
        event.getContentIfNotHandled()?.let {
            featuresCoordinator.onSignOutClick()
        }
    }

    private fun observeSignOutDialogEvent(event: SingleLiveEvent<Unit>) {
        event.getContentIfNotHandled()?.let {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.features_fragment_sign_out_dialog_text)
                .setPositiveButton(R.string.ok_text) { _, _ ->
                    featuresCoordinator.onSignOutClick()
                }
                .create()
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        nullifyProperties()
    }
}