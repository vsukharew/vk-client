package vsukharew.vkclient.publishimage.navigation

import androidx.navigation.NavController
import vsukharew.vkclient.R
import vsukharew.vkclient.common.navigation.BaseNavigator

class PublishImageNavigator : BaseNavigator() {
    var rootNavController: NavController? = null
    var flowNavController: NavController? = null

    fun exitFlow() {
        rootNavController?.navigate(R.id.global_action_to_featuresFragment)
    }
}