package vsukharew.vkclient.splash.presentation

import androidx.navigation.NavController

class SplashCoordinator(private val splashNavigator: SplashNavigator) {
    var navController: NavController? = null
        set(value) {
            field = value
            splashNavigator.navController = value
        }

    fun openNextScreen(isAuthorized: Boolean) {
        with(splashNavigator) {
            if (isAuthorized) {
                openFeaturesScreen()
            } else {
                openSignInScreen()
            }
        }
    }
}