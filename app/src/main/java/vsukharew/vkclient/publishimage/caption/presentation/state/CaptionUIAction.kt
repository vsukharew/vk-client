package vsukharew.vkclient.publishimage.caption.presentation.state

sealed class CaptionUIAction {
    object LocationRequested : CaptionUIAction()
    data class FailedToRequestLocation(val e: Throwable) : CaptionUIAction()
    data class Publish(
        val message: String,
        val latitude: Double? = null,
        val longitude: Double? = null
    ) : CaptionUIAction()
}