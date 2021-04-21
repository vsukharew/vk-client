package vsukharew.vkclient.publishimage.attach.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.loadstate.UIState
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.UriProvider
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.presentation.event.ImageEvent
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage

class AttachImageViewModel(
    private val imageInteractor: ImageInteractor,
    private val uriProvider: UriProvider
) : ViewModel() {

    private val photosStates = mutableMapOf<UIImage, UIState<UIImage>>()
    private val imageAction =
        MutableLiveData<ImageEvent>(ImageEvent.SuccessfulLoading(UIImage.AddNewImagePlaceholder))
    val imagesStatesLiveData = Transformations.switchMap(
        imageAction,
        ::refreshImagesState
    )

    fun startLoading(uri: String) {
        val image = UIImage.RealImage(Image(uri))
        imageAction.value = ImageEvent.InitialLoading(image)
        getUploadAddress(image)
    }

    fun getUriForFutureImage(): String {
        return uriProvider.createFileForWallImage()
    }

    private fun getUploadAddress(image: UIImage.RealImage) {
        viewModelScope.launch {
            delay(7000L)
            imageAction.value =
                when (val uploadResult =
                    withContext(Dispatchers.IO) { imageInteractor.uploadImage(image.image) }) {
                    is Result.Success -> {
                        ImageEvent.SuccessfulLoading(image)
                    }
                    is Result.Error -> {
                        ImageEvent.ErrorLoading(image, uploadResult)
                    }
                }
        }
    }

    private fun refreshImagesState(action: ImageEvent): LiveData<Map<UIImage, UIState<UIImage>>> {
        return liveData {
            val image = action.image
            val state = when (action) {
                is ImageEvent.InitialLoading,
                is ImageEvent.Retry,
                is ImageEvent.Remove -> UIState.LoadingProgress
                is ImageEvent.SuccessfulLoading -> UIState.Success(action.image)
                is ImageEvent.ErrorLoading -> UIState.Error(SingleLiveEvent(action.error))
            }
            photosStates[image] = state
            emit(photosStates)
        }
    }
}