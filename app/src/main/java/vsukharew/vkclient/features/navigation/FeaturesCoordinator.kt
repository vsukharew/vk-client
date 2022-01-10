package vsukharew.vkclient.features.navigation

import androidx.navigation.NavController
import vsukharew.vkclient.R

class FeaturesCoordinator(private val navController: NavController) {
    fun onSignOutClick() {
        navController.navigate(R.id.global_action_to_authFragment)
    }

    fun onPublishImageClick() {
        navController.navigate(R.id.action_featuresFragment_to_publish_image_graph)
    }
}