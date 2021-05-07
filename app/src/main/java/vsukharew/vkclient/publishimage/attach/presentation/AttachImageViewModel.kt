package vsukharew.vkclient.publishimage.attach.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.domain.model.Result.Error.DomainError
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

    private val imagesStates = mutableMapOf<UIImage, ImageUIState>()
    private val imageAction =
        MutableLiveData<ImageEvent>(ImageEvent.SuccessfulLoading(UIImage.AddNewImagePlaceholder))
    val imagesStatesLiveData = Transformations.switchMap(
        imageAction,
        ::refreshImagesState
    )
    val isNextButtonAvailable = Transformations.map(imagesStatesLiveData) {
        with(it) { containsNotOnlyPlaceholder() && allImagesAreLoaded() }
    }

    val imageSourceChoice = MutableLiveData<SingleLiveEvent<Unit>>()
    val openCameraAction = MutableLiveData<SingleLiveEvent<Unit>>()

    init {
        viewModelScope.launch { pollPendingPhotos() }
    }

    override fun onCleared() {
        super.onCleared()
        imageInteractor.removeAllImages()
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
            imagesStates[uiImage] = ImageUIState.Pending(false)
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

    fun removeImage(item: Pair<UIImage.RealImage, ImageUIState>) {
        val (image, state) = item
        val isDomainError = state is ImageUIState.Error && state.error.peekContent is DomainError
        if (!isDomainError) {
            imageInteractor.removeUploadedImage(image.image)
        }
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
                        imageInteractor.uploadImage(image.image, isRetryLoading) {
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
                imagesStates.remove(action.image)
            } else {
                imagesStates[image] = state
            }
            emit(imagesStates)
        }
    }

    private suspend fun pollPendingPhotos() {
        while (true) {
            val isLoadingInProgress =
                imagesStates.values.any { it is ImageUIState.LoadingProgress }
            val delay = if (!isLoadingInProgress) 500L else 2000L
            if (!isLoadingInProgress) {
                for (state in imagesStates) {
                    if (state.value is ImageUIState.Pending) {
                        startLoading(
                            (state.key as UIImage.RealImage),
                            (state.value as ImageUIState.Pending).isAfterRetry,
                            ImageEvent.InitialLoading(state.key)
                        )
                        break
                    }
                }
            }
            delay(delay)
        }
    }

    private fun Map<UIImage, ImageUIState>.containsNotOnlyPlaceholder(): Boolean =
        size > 1 && keys.first() is UIImage.AddNewImagePlaceholder && keys.last() is UIImage.RealImage

    private fun Map<UIImage, ImageUIState>.allImagesAreLoaded(): Boolean =
        any { it.key is UIImage.RealImage } && all { it.value is ImageUIState.Success }
}