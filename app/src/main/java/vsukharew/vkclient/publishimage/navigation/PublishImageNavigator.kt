package vsukharew.vkclient.publishimage.navigation

import androidx.navigation.NavController
import vsukharew.vkclient.R
import vsukharew.vkclient.common.navigation.BaseNavigator

class PublishImageNavigator : BaseNavigator() {
    var rootNavController: NavController? = null
    var flowNavController: NavController? = null

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