package vsukharew.vkclient.publishimage.caption.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.AppError.DomainError.LocationNotReceivedError
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.location.LocationProvider
import vsukharew.vkclient.common.presentation.BaseViewModel
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.publishimage.caption.presentation.state.CaptionUIAction
import vsukharew.vkclient.publishimage.caption.presentation.state.CaptionUIState
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage

class CaptionViewModel(
    private val imageInteractor: ImageInteractor,
    private val locationProvider: LocationProvider,
    private val flowStage: PublishImageFlowStage
) : BaseViewModel() {

    private val captionLiveData = MutableLiveData<String>()
    private val publishingAction = MutableLiveData<CaptionUIAction>()
    val publishingState = Transformations.switchMap(publishingAction, ::mapUiAction)
    val shouldShowAddLocationDialog = MutableLiveData<Boolean>()
    val showReloadImagesDialog = MutableLiveData<Unit>()
    val requestLocationPermissionEvent = MutableLiveData<SingleLiveEvent<Unit>>()
    val locationNotReceivedEvent = MutableLiveData<Unit>()
    val askToReloadPhotosEvent = MutableLiveData<Unit>()

    init {
        restorePossiblePhotosLoss()
    }

    fun suggestToAddLocationToPost() {
        if (locationProvider.areGooglePlayServicesEnabled()) {
            shouldShowAddLocationDialog.value = true
        } else {
            publishPost()
        }
    }

    fun requestLocationPermission() {
        shouldShowAddLocationDialog.value = false
        requestLocationPermissionEvent.value = SingleLiveEvent(Unit)
    }

    fun onCaptionChanged(caption: String) {
        captionLiveData.value = caption
    }

    fun isGpsEnabled(): Boolean = locationProvider.isGpsEnabled()

    fun onLocationRequested() {
        publishingAction.value = CaptionUIAction.LocationRequested
    }

    fun publishPost(latitude: Double? = null, longitude: Double? = null) {
        captionLiveData.value?.let {
            publishingAction.value = CaptionUIAction.Publish(it, latitude, longitude)
        }
    }

    private fun restorePossiblePhotosLoss() {
        // Made for simplicity. If there's no saved photos exist at this stage, process got killed
        // by the system and user need to get back to previous screen in order to reload photos

        // Otherwise one have to create database and save all the objects received during the upload
        // process
        if (!imageInteractor.doSavedImagesExist()) {
            showReloadImagesDialog.value = Unit
        }
    }

    private fun onFailedRequestLocation(e: Throwable) {
        publishingAction.value = CaptionUIAction.FailedToRequestLocation(e)
    }

    private fun mapUiAction(action: CaptionUIAction): LiveData<CaptionUIState> {
        return liveData {
            when (action) {
                is CaptionUIAction.LocationRequested -> {
                    emit(CaptionUIState.LoadingProgress)
                    locationProvider.requestCurrentLocation(
                        { publishPost(it.latitude, it.longitude) },
                        { onFailedRequestLocation(it) }
                    )
                }
                is CaptionUIAction.FailedToRequestLocation -> {
                    locationNotReceivedEvent.value = Unit
                    emit(
                        CaptionUIState.Error(
                            SingleLiveEvent(
                                Left(
                                    LocationNotReceivedError(
                                        action.e
                                    )
                                )
                            )
                        )
                    )
                }
                is CaptionUIAction.Publish -> {
                    emit(CaptionUIState.LoadingProgress)
                    when (val result =
                        withContext(Dispatchers.IO) {
                            imageInteractor.postImagesOnWall(
                                action.message,
                                action.latitude,
                                action.longitude
                            )
                        }) {
                        is Right -> {
                            emit(CaptionUIState.Success(result.data))
                            flowStage.onForwardClick()
                        }
                        is Left -> {
                            when (result.data) {
                                AppError.DomainError.NoPhotosToPostError -> {
                                    askToReloadPhotosEvent.value = Unit
                                }
                                else -> {
                                    val event = SingleLiveEvent(result)

                                    emit(CaptionUIState.Error(event))
                                    errorLiveData.value = event
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}