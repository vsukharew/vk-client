package vsukharew.vkclient.publishimage.attach.presentation.state

import vsukharew.vkclient.publishimage.attach.presentation.model.ImageLoadingState
import vsukharew.vkclient.publishimage.attach.presentation.model.UIImage
import java.io.Serializable

data class AttachImageUIState(val images: List<UIImage.RealImage>) : Serializable {
    private val pendingImages = images.filter { it.loadingState !is ImageLoadingState.Success }
    val allImages = listOf(UIImage.AddNewImagePlaceholder) + images
    val isNextButtonAvailable = images.isNotEmpty() && pendingImages.isEmpty()
    companion object {
        val DEFAULT = AttachImageUIState(emptyList())
    }
}