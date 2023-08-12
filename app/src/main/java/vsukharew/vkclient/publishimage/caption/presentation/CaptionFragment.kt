package vsukharew.vkclient.publishimage.caption.presentation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.extension.locationSettings
import vsukharew.vkclient.common.extension.setDistinctText
import vsukharew.vkclient.common.extension.systemSettings
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentCaptionBinding
import vsukharew.vkclient.publishimage.caption.di.CaptionScopeCreator
import vsukharew.vkclient.publishimage.navigation.PublishImageCoordinator

class CaptionFragment : BaseFragment<FragmentCaptionBinding>(R.layout.fragment_caption) {
    private val flowCoordinator: PublishImageCoordinator by inject()

    private var locationPermissionLauncher: ActivityResultLauncher<String>? = null
    private var reloadPhotosDialog: AlertDialog? = null
    private var attachLocationDialog: AlertDialog? = null
    private var locationNotReceivedDialog: AlertDialog? = null

    override val viewModel: CaptionViewModel by viewModel()
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

    override fun onDestroy() {
        super.onDestroy()
        reloadPhotosDialog?.dismiss()
        attachLocationDialog?.dismiss()
        locationNotReceivedDialog?.dismiss()
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
                text?.toString()?.let(viewModel::onCaptionChanged)
            }
            publish.setOnClickListener {
                viewModel.suggestToAddLocationToPostNew()
            }
        }
    }

    private fun observeData() {
        val lifecycle = viewLifecycleOwner.lifecycle
        viewModel.apply {
            lifecycleScope.apply {
                launch {
                    uiState.map(CaptionUiState::caption::get)
                        .flowWithLifecycle(lifecycle)
                        .collectLatest(binding.captionText::setDistinctText)
                }
                launch {
                    uiState.map(CaptionUiState::isLoadingInProgress::get)
                        .flowWithLifecycle(lifecycle)
                        .collectLatest(binding.progressBar::isVisible::set)
                }
                launch {
                    uiState.map(CaptionUiState::isLoadingInProgress::get)
                        .flowWithLifecycle(lifecycle)
                        .collectLatest(binding.group::isGone::set)
                }
                launch {
                    uiState.map(CaptionUiState::shouldNavigateTo::get)
                        .flowWithLifecycle(lifecycle)
                        .collectLatest(::collectDestination)
                }
                launch {
                    uiState
                        .map(CaptionUiState::shouldStartPermissionLauncher::get)
                        .flowWithLifecycle(lifecycle)
                        .filter { it }
                        .collectLatest { startLocationPermissionLauncher() }
                }
                launch {
                    uiState.map(CaptionUiState::isPublishButtonEnabled::get)
                        .flowWithLifecycle(lifecycle)
                        .collectLatest(::collectPublishButtonAvailability)
                }
            }
        }
    }

    private fun startLocationPermissionLauncher() {
        locationPermissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        viewModel.requestLocationRequested()
    }

    private fun collectDestination(navigateTo: CaptionUiState.NavigateTo) {
        when (navigateTo) {
            CaptionUiState.NavigateTo.SystemSettings -> {
                startActivity(Intent().systemSettings())
                viewModel.systemSettingsOpened()
            }
            CaptionUiState.NavigateTo.LocationSettings -> {
                startActivity(Intent().locationSettings())
                viewModel.locationSettingsOpened()
            }
            CaptionUiState.NavigateTo.Nothing -> {

            }
        }
    }

    private fun collectPublishButtonAvailability(isEnabled: Boolean) {
        binding.publish.isEnabled = isEnabled
    }

    private fun handleLocationPermissionResult(isGranted: Boolean) {
        viewModel.apply {
            when {
                isGranted -> {
                    locationPermissionGranted()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    explainWhyUserIsUnableToAddLocation()
                }
                else -> {
                    locationPermissionDenied()
                }
            }
        }
    }
}