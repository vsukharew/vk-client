package vsukharew.vkclient.features.navigation

import androidx.navigation.NavController
import vsukharew.vkclient.R
import vsukharew.vkclient.common.navigation.BaseNavigator

class FeaturesNavigator : BaseNavigator() {
    var navController: NavController? = null

    fun onSignOutClick() {
        navController?.navigate(R.id.global_action_to_authFragment)
    }

    fun navigateToPublishImageFlow() {
        navController?.navigate(R.id.action_featuresFragment_to_publish_image_graph)
    }
}