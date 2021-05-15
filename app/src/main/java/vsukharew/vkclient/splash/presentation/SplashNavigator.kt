package vsukharew.vkclient.splash.presentation

import androidx.navigation.NavController

class SplashNavigator {
    var navController: NavController? = null

    fun openSignInScreen() {
        navController?.navigate(
            SplashFragmentDirections.actionSplashFragmentToAuthFragment()
        )
    }

    fun openFeaturesScreen() {
        navController?.navigate(
            SplashFragmentDirections.actionSplashFragmentToFeaturesGraph()
        )
    }
}