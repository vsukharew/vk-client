package vsukharew.vkclient.publishimage.attach.domain.model

import java.io.Serializable

data class Image(
    val uri: String,
    val source: ImageSource
) : Serializable