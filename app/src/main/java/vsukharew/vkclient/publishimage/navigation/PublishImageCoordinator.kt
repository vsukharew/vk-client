package vsukharew.vkclient.publishimage.navigation

import androidx.navigation.NavController
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage.*

class PublishImageCoordinator(private val navigator: PublishImageNavigator) {
    private val attachImageStage = AttachImageStage(navigator)
    private val captionStage = CaptionStage(navigator)

    private var currentStage: PublishImageFlowStage = AttachImageStage(navigator)

    var rootNavController: NavController? = null
        set(value) {
            field = value
            navigator.rootNavController = value
        }
    var flowNavController: NavController? = null
        set(value) {
            field = value
            navigator.flowNavController = value
        }

    fun onBackClick() {
        currentStage.onBackClick()
        currentStage = attachImageStage
    }

    fun onForwardClick() {
        currentStage.onForwardClick()
        currentStage = captionStage
    }
}