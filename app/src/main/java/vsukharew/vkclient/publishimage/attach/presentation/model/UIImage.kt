package vsukharew.vkclient.publishimage.attach.presentation.model

import vsukharew.vkclient.publishimage.attach.domain.model.Image

sealed class UIImage {
    object AddNewImagePlaceholder : UIImage()
    data class RealImage(val image: Image) : UIImage()
}