package vsukharew.vkclient.publishimage.attach.domain.model

data class UploadedImage(
    val server : Int,
    val photosList : String,
    val aid : Int?,
    val hash : String
)