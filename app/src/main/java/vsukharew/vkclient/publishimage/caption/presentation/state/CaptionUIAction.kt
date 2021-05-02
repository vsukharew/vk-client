package vsukharew.vkclient.publishimage.caption.presentation.state

sealed class CaptionUIAction {
    data class Publish(val message: String) : CaptionUIAction()
}