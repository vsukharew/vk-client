package vsukharew.vkclient.publishimage.attach.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.DomainContentResolver
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.CAMERA
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.GALLERY
import vsukharew.vkclient.publishimage.attach.presentation.event.ImageEvent
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage
import vsukharew.vkclient.publishimage.attach.presentation.state.ImageUIState
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage

class AttachImageViewModel(
    private val imageInteractor: ImageInteractor,
    private val contentResolver: DomainContentResolver,
    private val flowStage: PublishImageFlowStage
) : ViewModel() {

    private val photosStates = mutableMapOf<UIImage, ImageUIState>()
    private val imageAction =
        MutableLiveData<ImageEvent>(ImageEvent.SuccessfulLoading(UIImage.AddNewImagePlaceholder))
    val imagesStatesLiveData = Transformations.switchMap(
        imageAction,
        ::refreshImagesState
    )
    val isNextButtonAvailable = imageInteractor.observePublishingReadiness()
        .debounce { if (it) 500L else 0L }
        .asLiveData()
    val imageSourceChoice = MutableLiveData<SingleLiveEvent<Unit>>()
    val openCameraAction = MutableLiveData<SingleLiveEvent<Unit>>()

    init {
        viewModelScope.launch { pollPendingPhotos() }
    }

    fun openCamera() {
        openCameraAction.value = SingleLiveEvent(Unit)
    }

    fun chooseImageSource() {
        imageSourceChoice.value = SingleLiveEvent(Unit)
    }

    fun startLoading(uris: List<String>) {
        uris.forEach {
            val domainImage = Image(it, GALLERY)
            val uiImage = UIImage.RealImage(domainImage)
            photosStates[uiImage] = ImageUIState.Pending(false)
        }
    }

    fun startLoading(image: UIImage.RealImage, isRetryLoading: Boolean, event: ImageEvent? = null) {
        startLoadingInternal(image, isRetryLoading, event)
    }

    fun startLoading(uri: String, isRetryLoading: Boolean, event: ImageEvent? = null) {
        val domainImage = Image(uri, CAMERA)
        val image = UIImage.RealImage(domainImage)
        startLoadingInternal(image, isRetryLoading, event)
    }

    fun removeImage(image: UIImage.RealImage) {
        imageInteractor.removeUploadedImage(image.image)
        imageAction.value = ImageEvent.Remove(image)
    }

    fun getUriForFutureImage(): String {
        return contentResolver.createFileForWallImage()
    }

    fun goToNextStage() {
        flowStage.onForwardClick()
    }

    private fun startLoadingInternal(
        image: UIImage.RealImage,
        isRetryLoading: Boolean,
        event: ImageEvent? = null
    ) {
        imageAction.value = event ?: ImageEvent.Pending(image, isRetryLoading)
        event?.let { getUploadAddress(image, isRetryLoading) }
    }

    private fun getUploadAddress(image: UIImage.RealImage, isRetryLoading: Boolean) {
        viewModelScope.launch {
            imageAction.value =
                when (val uploadResult =
                    withContext(Dispatchers.IO) {
                        imageInteractor.uploadImage(
                            image.image,
                            isRetryLoading
                        ) {
                            launch {
                                withContext(Dispatchers.Main) {
                                    val progress = (it * 100).toInt()
                                    imageAction.value = ImageEvent.InitialLoading(image, progress)
                                }
                            }
                        }
                    }) {
                    is Result.Success -> {
                        ImageEvent.SuccessfulLoading(image)
                    }
                    is Result.Error -> {
                        ImageEvent.ErrorLoading(image, uploadResult)
                    }
                }
        }
    }

    private fun refreshImagesState(action: ImageEvent): LiveData<Map<UIImage, ImageUIState>> {
        return liveData {
            val image = action.image
            val state = when (action) {
                is ImageEvent.Pending -> ImageUIState.Pending(action.isRetryLoading)
                is ImageEvent.InitialLoading -> ImageUIState.LoadingProgress(action.progressLoading)
                is ImageEvent.Retry -> ImageUIState.LoadingProgress()
                is ImageEvent.Remove -> ImageUIState.Success(action.image)
                is ImageEvent.SuccessfulLoading -> ImageUIState.Success(action.image)
                is ImageEvent.ErrorLoading -> ImageUIState.Error(SingleLiveEvent(action.error))
            }
            if (action is ImageEvent.Remove) {
                photosStates.remove(action.image)
            } else {
                photosStates[image] = state
            }
            emit(photosStates)
        }
    }

    private suspend fun pollPendingPhotos() {
        while (true) {
            val isLoadingInProgress =
                photosStates.values.any { it is ImageUIState.LoadingProgress }
            if (!isLoadingInProgress) {
                photosStates.forEach {
                    if (it.value is ImageUIState.Pending) {
                        startLoading(
                            (it.key as UIImage.RealImage),
                            (it.value as ImageUIState.Pending).isAfterRetry,
                            ImageEvent.InitialLoading(it.key)
                        )
                        return@forEach
                    }
                }
            }
            delay(2000)
        }
    }
}