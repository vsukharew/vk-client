package vsukharew.vkclient.splash.presentation

import androidx.navigation.NavController

class SplashCoordinator(private val navController: NavController) {
    fun openNextScreen(isAuthorized: Boolean) {
        if (isAuthorized) {
            openFeaturesScreen()
        } else {
            openSignInScreen()
        }
    }

    private fun openSignInScreen() {
        navController.navigate(
            SplashFragmentDirections.actionSplashFragmentToAuthFragment()
        )
    }

    private fun openFeaturesScreen() {
        navController.navigate(
            SplashFragmentDirections.actionSplashFragmentToFeaturesGraph()
        )
    }
}