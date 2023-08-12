package vsukharew.vkclient.publishimage.attach.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.extension.fold
import vsukharew.vkclient.common.extension.replace
import vsukharew.vkclient.common.presentation.BaseViewModel
import vsukharew.vkclient.publishimage.attach.domain.infrastructure.DomainContentResolver
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.publishimage.attach.domain.model.Image
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource
import vsukharew.vkclient.publishimage.attach.domain.model.ImageSource.GALLERY
import vsukharew.vkclient.publishimage.attach.domain.model.SavedWallImage
import vsukharew.vkclient.publishimage.attach.presentation.model.ImageLoadingState
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage
import vsukharew.vkclient.publishimage.attach.presentation.state.AttachImageUIState
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage

class AttachImageViewModel(
    private val imageInteractor: ImageInteractor,
    private val contentResolver: DomainContentResolver,
    private val flowStage: PublishImageFlowStage,
    private val savedState: SavedStateHandle,
    sessionInteractor: SessionInteractor
) : BaseViewModel(sessionInteractor) {

    private val mutableUiState =
        MutableStateFlow(savedState[KEY_STATE] ?: AttachImageUIState.DEFAULT)
    val uiState = mutableUiState.asStateFlow()

    fun loadGalleryImages(uris: List<String>) {
        viewModelScope.launch {
            uris.map {
                async { startLoadingNew(it, GALLERY) }
            }.awaitAll()
        }
    }

    fun loadCameraImage(uri: String) {
        viewModelScope.launch {
            startLoadingNew(uri, GALLERY)
        }
    }

    fun retryLoadingNew(image: UIImage.RealImage) {
        uiState.value
            .images
            .find { it == image }
            ?.copy(loadingState = ImageLoadingState.Pending)
            ?.let { pendingImage ->
                uiState.value.images.replace(
                    { it.image.uri == pendingImage.image.uri },
                    pendingImage
                ).let { images ->
                    mutableUiState.update { state -> state.copy(images = images).save() }
                }.let {
                    viewModelScope.launch {
                        uploadImage(pendingImage, false)
                            .fold(
                                ifLeft = ImageLoadingState::Error,
                                ifRight = { ImageLoadingState.Success }
                            )
                            .let { pendingImage.copy(loadingState = it) }
                            .let { image -> uiState.value.images.replaceByUri(image) }
                            .let { images ->
                                mutableUiState.update { it.copy(images = images).save() }
                            }
                    }
                }
            }
    }

    fun removeImage(image: UIImage.RealImage) {
        mutableUiState.update { state ->
            state.copy(
                images = state.images.filter { it != image }
            ).save()
        }
    }

    fun getUriForFutureImage(): String {
        return contentResolver.createFileForWallImage()
    }

    fun goToNextStage() {
        flowStage.onForwardClick()
    }

    private suspend fun startLoadingNew(uri: String, imageSource: ImageSource) {
        val newImage = UIImage.RealImage(Image(uri, imageSource), ImageLoadingState.Pending)
        mutableUiState.update {
            it.copy(
                images = it.images + listOf(newImage),
            ).save()
        }
        startLoadingInternal(newImage)
    }

    private suspend fun startLoadingInternal(
        newImage: UIImage.RealImage
    ) {
        uploadImage(newImage, false)
            .fold(
                ifLeft = {
                    if (it is AppError.RemoteError.Unauthorized) {
                        handleError(it)
                    }
                    ImageLoadingState.Error(it)
                },
                ifRight = { ImageLoadingState.Success }
            )
            .let { state -> newImage.copy(loadingState = state) }
            .let { uiState.value.images.replaceByUri(it) }
            .let { images -> mutableUiState.update { it.copy(images = images).save() } }
    }

    private suspend fun uploadImage(
        image: UIImage.RealImage,
        isRetryLoading: Boolean
    ): Either<AppError, SavedWallImage> {
        val loadingImage = image.copy(loadingState = ImageLoadingState.LoadingProgress())
        return imageInteractor.uploadImage(image.image, isRetryLoading) {
            mutableUiState.update { state ->
                val progress = (it * 100).toInt()
                val newProgressImage = loadingImage.copy(
                    loadingState = ImageLoadingState.LoadingProgress(progress)
                )
                state.copy(
                    images = state.images.replaceByUri(newProgressImage)
                ).save()
            }
        }
    }

    private fun List<UIImage.RealImage>.replaceByUri(with: UIImage.RealImage) =
        replace({ it.image.uri == with.image.uri }, with)

    private fun AttachImageUIState.save(): AttachImageUIState {
        return also { savedState[KEY_STATE] = this }
    }

    private companion object {
        private const val KEY_STATE = "state"
    }
}