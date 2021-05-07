package vsukharew.vkclient.publishimage.caption.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.domain.model.Result.Error.DomainError.LocationNotReceivedError
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.location.LocationProvider
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.publishimage.caption.presentation.state.CaptionUIAction
import vsukharew.vkclient.publishimage.caption.presentation.state.CaptionUIState
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage

class CaptionViewModel(
    private val imageInteractor: ImageInteractor,
    private val locationProvider: LocationProvider,
    private val flowStage: PublishImageFlowStage
) : ViewModel() {

    private val captionLiveData = MutableLiveData<String>()
    private val publishingAction = MutableLiveData<CaptionUIAction>()
    val publishingState = Transformations.switchMap(publishingAction, ::mapUiAction)
    val shouldShowAddLocationDialog = MutableLiveData<Boolean>()
    val requestLocationPermissionEvent = MutableLiveData<SingleLiveEvent<Unit>>()

    fun suggestToAddLocationToPost() {
        shouldShowAddLocationDialog.value = true
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
                is CaptionUIAction.FailedToRequestLocation ->
                    emit(CaptionUIState.Error(SingleLiveEvent(LocationNotReceivedError(action.e))))
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
                        is Result.Success -> {
                            emit(CaptionUIState.Success(result.data))
                            flowStage.onForwardClick()
                        }
                        is Result.Error -> {
                            emit(CaptionUIState.Error(SingleLiveEvent(result)))
                        }
                    }
                }
            }
        }
    }
}