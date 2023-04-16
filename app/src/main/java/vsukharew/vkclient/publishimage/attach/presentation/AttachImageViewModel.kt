package vsukharew.vkclient.publishimage.attach.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.AppError.DomainError
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseViewModel
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
    private val flowStage: PublishImageFlowStage,
    private val savedState: SavedStateHandle
) : BaseViewModel() {

    private val isNextButtonAvailableFlow = MutableStateFlow(false)
    private val imagesStates = mutableMapOf<UIImage, ImageUIState>()
    private val imageAction = savedState.get<List<String>>(KEY_IMAGES_URIS)?.let {
        MutableLiveData<ImageEvent>()
    } ?: MutableLiveData<ImageEvent>(ImageEvent.SuccessfulLoading(UIImage.AddNewImagePlaceholder))
    val imagesStatesLiveData = Transformations.switchMap(imageAction, ::refreshImagesState)
    val isNextButtonAvailable = MutableLiveData<Boolean>()

    init {
        isNextButtonAvailableFlow.debounce(500L)
            .onEach { isNextButtonAvailable.value = it }
            .launchIn(viewModelScope)
    }

    val imageSourceChoice = MutableLiveData<SingleLiveEvent<Unit>>()
    val openCameraAction = MutableLiveData<SingleLiveEvent<Unit>>()

    init {
        viewModelScope.apply {
            launch { pollPendingPhotos() }
            savedState.get<List<String>>(KEY_IMAGES_URIS)?.let {
                launch { restoreImagesForUploading(it) }
            }
        }
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
            saveImageUri(uiImage)
        }
    }

    fun startLoading(
        image: UIImage.RealImage,
        isRetryLoading: Boolean,
        event: ImageEvent? = null,
        shouldSaveImageUri: Boolean = true
    ) {
        startLoadingInternal(image, isRetryLoading, event, shouldSaveImageUri)
    }

    fun startLoading(uri: String, isRetryLoading: Boolean, event: ImageEvent? = null) {
        val domainImage = Image(uri, CAMERA)
        val image = UIImage.RealImage(domainImage)
        startLoadingInternal(image, isRetryLoading, event)
    }

    fun removeImage(item: Pair<UIImage.RealImage, ImageUIState>) {
        val (image, state) = item
        val isDomainError = state is ImageUIState.Error && state.error.peekContent.data is DomainError
        if (!isDomainError) {
            imageInteractor.removeUploadedImage(image.image)
        }
        imageAction.value = ImageEvent.Remove(image)
        deleteUriFromSaved(image)
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
        event: ImageEvent? = null,
        shouldSaveImageUri: Boolean = true
    ) {
        imageAction.value = event ?: ImageEvent.Pending(image, isRetryLoading)
        event?.let { getUploadAddress(image, isRetryLoading) }
        if (shouldSaveImageUri) {
            saveImageUri(image)
        }
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
                    is Right -> {
                        ImageEvent.SuccessfulLoading(image)
                    }
                    is Left -> {
                        ImageEvent.ErrorLoading(image, uploadResult)
                    }
                }
        }
    }

    private fun refreshImagesState(action: ImageEvent): LiveData<Map<UIImage, ImageUIState>> {
        return liveData {
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
                imagesStates[action.image] = state
            }
            emit(imagesStates)
            isNextButtonAvailableFlow.value = with(imagesStates) {
                containsNotOnlyPlaceholder() && allImagesAreLoaded()
            }
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
                            ImageEvent.InitialLoading(state.key),
                            false
                        )
                        break
                    }
                }
            }
            delay(delay)
        }
    }

    private fun saveImageUri(image: UIImage.RealImage) {
        val savedImages = savedState.get<List<String>>(KEY_IMAGES_URIS)
        savedState[KEY_IMAGES_URIS] = (savedImages ?: emptyList()) + listOf(image.image.uri)
    }

    private suspend fun restoreImagesForUploading(imagesUris: List<String>) {
        imagesUris.also {
            savedState.remove<List<String>>(KEY_IMAGES_URIS)
            imageAction.value =
                ImageEvent.SuccessfulLoading(UIImage.AddNewImagePlaceholder)
            delay(100)
        }.let(::startLoading)
    }

    private fun deleteUriFromSaved(image: UIImage.RealImage) {
        val savedImages = savedState.get<List<String>>(KEY_IMAGES_URIS)
        savedState[KEY_IMAGES_URIS] = savedImages?.filter { it != image.image.uri }
    }

    private fun Map<UIImage, ImageUIState>.containsNotOnlyPlaceholder(): Boolean =
        size > 1 && keys.first() is UIImage.AddNewImagePlaceholder && keys.last() is UIImage.RealImage

    private fun Map<UIImage, ImageUIState>.allImagesAreLoaded(): Boolean =
        any { it.key is UIImage.RealImage } && all { it.value is ImageUIState.Success }

    private companion object {
        private const val KEY_IMAGES_URIS = "images_uris"
    }
}