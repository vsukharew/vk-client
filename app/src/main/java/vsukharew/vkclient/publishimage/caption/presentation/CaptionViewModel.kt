package vsukharew.vkclient.publishimage.caption.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vsukharew.vkclient.R
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.extension.doIfLeft
import vsukharew.vkclient.common.extension.sideEffect
import vsukharew.vkclient.common.location.LocationProvider
import vsukharew.vkclient.common.presentation.BaseViewModel
import vsukharew.vkclient.common.presentation.OneTimeEvent
import vsukharew.vkclient.common.presentation.OneTimeEvent.Perform.SnackBar
import vsukharew.vkclient.common.presentation.OneTimeEvent.Perform.SnackBar.Length.INDEFINITE
import vsukharew.vkclient.common.presentation.OneTimeEvent.Perform.SnackBar.Length.LONG
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.publishimage.caption.presentation.CaptionUiState.NavigateTo.SystemSettings
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage

class CaptionViewModel(
    private val imageInteractor: ImageInteractor,
    private val locationProvider: LocationProvider,
    private val flowStage: PublishImageFlowStage,
    sessionInteractor: SessionInteractor
) : BaseViewModel(sessionInteractor) {

    private val mutableUiState = MutableStateFlow(CaptionUiState())
    val uiState = mutableUiState.asStateFlow()

    fun suggestToAddLocationToPostNew() {
        if (locationProvider.areGooglePlayServicesEnabled()) {
            viewModelScope.launch {
                mutableEventsFlow.emit(OneTimeEvent.Perform.Alert(
                    messageRes = R.string.caption_fragment_location_dialog_message_text,
                    negativeButtonRes = R.string.caption_fragment_location_dialog_publish_without_location_text,
                    positiveButtonRes = R.string.caption_fragment_location_dialog_add_location_text,
                    negativeButtonListener = { publishPostNew() },
                    positiveButtonListener = { requestLocationPermission() }
                ))
            }
        } else {
            publishPostNew()
        }
    }

    fun requestLocationRequested() {
        mutableUiState.update { it.copy(shouldStartPermissionLauncher = false) }
    }

    fun onCaptionChanged(caption: String) {
        mutableUiState.update { it.copy(caption = caption) }
    }

    fun locationPermissionGranted() {
        if (isGpsEnabled()) {
            mutableUiState.update { it.copy(isLoadingInProgress = true) }
            locationRequestedNew()
        } else {
            viewModelScope.launch {
                mutableEventsFlow.emit(
                    SnackBar.StringResource(
                        actionTextResId = R.string.caption_fragment_turn_on_gps_text,
                        action = {
                            mutableUiState.update {
                                it.copy(shouldNavigateTo = CaptionUiState.NavigateTo.LocationSettings)
                            }
                        },
                        resId = R.string.caption_fragment_location_dialog_publish_permission_is_required_text,
                        length = LONG
                ))
            }
        }
    }

    fun explainWhyUserIsUnableToAddLocation() {
        viewModelScope.launch {
            SnackBar.StringResource(
                resId = R.string.caption_fragment_location_dialog_publish_permission_is_required_text,
                actionTextResId = R.string.got_it_text,
                length = INDEFINITE
            )
        }
    }

    fun locationPermissionDenied() {
        viewModelScope.launch {
            mutableEventsFlow.emit(
                SnackBar.StringResource(
                    resId = R.string.caption_fragment_the_app_is_forbidden_location_access_text,
                    actionTextResId = R.string.settings_text,
                    action = {
                        mutableUiState.update {
                            it.copy(shouldNavigateTo = SystemSettings)
                        }
                    },
                    length = LONG
                )
            )
        }
    }

    fun systemSettingsOpened() {
        mutableUiState.update {
            it.copy(shouldNavigateTo = CaptionUiState.NavigateTo.Nothing)
        }
    }

    fun locationSettingsOpened() {
        mutableUiState.update {
            it.copy(shouldNavigateTo = CaptionUiState.NavigateTo.Nothing)
        }
    }

    private fun requestLocationPermission() {
        mutableUiState.update { it.copy(shouldStartPermissionLauncher = true) }
    }

    private fun isGpsEnabled(): Boolean = locationProvider.isGpsEnabled()

    private fun locationRequestedNew() {
        locationProvider.requestCurrentLocation(
            { publishPostNew(it.latitude, it.longitude) },
            { onFailedRequestLocation(it) }
        )
    }

    private fun publishPostNew(latitude: Double? = null, longitude: Double? = null) {
        val text = uiState.value.caption ?: return
        mutableUiState.update { it.copy(isLoadingInProgress = true) }
        viewModelScope.launch {
            sideEffect {
                imageInteractor.postImagesOnWall(
                    text,
                    latitude,
                    longitude
                ).bind()
                flowStage.onForwardClick()
            }.also {
                mutableUiState.update { it.copy(isLoadingInProgress = false) }
            }.doIfLeft(::handleError)
        }
    }

    private fun onFailedRequestLocation(e: Throwable) {
        mutableUiState.update { it.copy(isLoadingInProgress = false) }
        viewModelScope.launch {
            OneTimeEvent.Perform.Alert(
                messageRes = R.string.caption_fragment_failed_to_receive_location_text,
                negativeButtonRes = R.string.caption_fragment_location_dialog_publish_without_location_text,
                positiveButtonRes = R.string.retry_btn,
                negativeButtonListener = { publishPostNew() },
                positiveButtonListener = { requestLocationPermission() }
            )
        }
    }
}