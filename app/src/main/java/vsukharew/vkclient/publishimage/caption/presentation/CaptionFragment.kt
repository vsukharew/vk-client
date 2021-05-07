package vsukharew.vkclient.publishimage.caption.presentation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.extension.snackBarIndefinite
import vsukharew.vkclient.common.extension.snackBarLong
import vsukharew.vkclient.common.extension.systemSettings
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentCaptionBinding
import vsukharew.vkclient.publishimage.caption.di.CaptionScopeCreator
import vsukharew.vkclient.publishimage.caption.presentation.state.CaptionUIState
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

class CaptionFragment : BaseFragment<FragmentCaptionBinding>(R.layout.fragment_caption) {
    private val flowCoordinator: PublishImageCoordinator by inject()
    private val viewModel: CaptionViewModel by viewModel()
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>

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
        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            ::handleLocationPermissionResult
        )
    }

    private fun setListeners() {
        binding.apply {
            captionText.doOnTextChanged { text, _, _, _ ->
                publish.isEnabled = text?.isNotEmpty() ?: false
                text?.toString()?.let(viewModel::onCaptionChanged)
            }
            publish.setOnClickListener {
                viewModel.suggestToAddLocationToPost()
            }
        }
    }

    private fun observeData() {
        viewModel.apply {
            publishingState.observe(viewLifecycleOwner, ::observePublishingState)
            shouldShowAddLocationDialog.observe(
                viewLifecycleOwner,
                ::observeShouldShowAddLocationDialog
            )
            requestLocationPermissionEvent.observe(
                viewLifecycleOwner,
                ::observeRequestLocationPermission
            )
        }
    }

    private fun observePublishingState(state: CaptionUIState) {
        binding.apply {
            progressBar.isVisible = state is CaptionUIState.LoadingProgress
            publish.isVisible = state !is CaptionUIState.LoadingProgress
            if (state is CaptionUIState.Error) {
                when (state.error.peekContent) {
                    is Result.Error.DomainError.LocationNotReceivedError -> {
                        showLocationNotReceivedDialog()
                    }
                    else -> state.error.getContentIfNotHandled()?.let(::handleError)
                }
            }
        }
    }

    private fun observeShouldShowAddLocationDialog(shouldShow: Boolean) {
        if (!shouldShow) return
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.caption_fragment_location_dialog_message_text)
            .setNegativeButton(R.string.caption_fragment_location_dialog_publish_without_location_text) { _, _ ->
                viewModel.publishPost()
            }
            .setPositiveButton(R.string.caption_fragment_location_dialog_add_location_text) { _, _ ->
                viewModel.requestLocationPermission()
            }
            .create()
            .show()
    }

    private fun observeRequestLocationPermission(event: SingleLiveEvent<Unit>) {
        event.getContentIfNotHandled()?.let {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showLocationNotReceivedDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.caption_fragment_failed_to_receive_location_text)
            .setNegativeButton(
                R.string.caption_fragment_location_dialog_publish_without_location_text) { _, _ ->
                viewModel.publishPost()
            }
            .setPositiveButton(R.string.retry_btn) { _, _ -> viewModel.requestLocationPermission() }
            .create()
            .show()
    }

    private fun handleLocationPermissionResult(isGranted: Boolean) {
        viewModel.apply {
            when {
                isGranted -> {
                    if (isGpsEnabled()) {
                        onLocationRequested()
                    } else {
                        snackBarLong(R.string.caption_fragment_turn_on_gps_text)
                    }
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    snackBarIndefinite(
                        R.string.caption_fragment_location_dialog_publish_permission_is_required_text,
                        R.string.got_it_text
                    )
                }
                else -> {
                    snackBarLong(
                        R.string.caption_fragment_the_app_is_forbidden_location_access_text,
                        R.string.settings_text
                    ) { startActivity(Intent().systemSettings()) }
                }
            }
        }
    }
}