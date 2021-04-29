package vsukharew.vkclient.publishimage.navigation

import androidx.navigation.NavController
import vsukharew.vkclient.R
import vsukharew.vkclient.publishimage.navigation.PublishImageFlowStage.*

class PublishImageCoordinator(private val navigator: PublishImageNavigator) {
    val attachImageStage = AttachImageStage(this)
    val captionStage = CaptionStage(this)
    var currentStage: PublishImageFlowStage = attachImageStage

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

    fun exitFlow() {
        rootNavController?.popBackStack(R.id.features_fragment, false)
    }

    fun openCaptionScreen() {
        flowNavController?.navigate(R.id.action_attachImageFragment_to_captionFragment)
    }

    fun goBackToImageAttachStage() {
        flowNavController?.popBackStack(R.id.attachImageFragment, false)
    }
}