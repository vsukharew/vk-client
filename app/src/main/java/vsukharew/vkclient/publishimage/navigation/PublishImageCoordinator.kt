package vsukharew.vkclient.publishimage.navigation

import androidx.navigation.NavController

class PublishImageCoordinator(private val navigator: PublishImageNavigator) {
    private val attachImageStage = object : PublishImageFlowStage {
        override fun onBackClick() {
            navigator.exitFlow()
        }

        override fun onForwardClick() {

        }
    }

    private var currentStage: PublishImageFlowStage = attachImageStage

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
    }

    fun onForwardClick() {
        currentStage.onForwardClick()
    }
}