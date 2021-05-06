package vsukharew.vkclient.features.navigation

import androidx.navigation.NavController

class FeaturesCoordinator(
    private val featuresNavigator: FeaturesNavigator
) {
    var navController: NavController? = null
        set(value) {
            field = value
            featuresNavigator.navController = value
        }

    fun onSignOutClick() {
        featuresNavigator.onSignOutClick()
    }

    fun onPublishImageClick() {
        featuresNavigator.navigateToPublishImageFlow()
    }
}