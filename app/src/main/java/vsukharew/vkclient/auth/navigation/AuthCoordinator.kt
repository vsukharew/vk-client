package vsukharew.vkclient.auth.navigation

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.NavController
import vsukharew.vkclient.auth.data.model.AuthParams

class AuthCoordinator(private val authNavigator: AuthNavigator) {
    var vkActivityLauncher: ActivityResultLauncher<Intent>? = null
        set(value) {
            field = value
            authNavigator.vkActivityLauncher = value
        }

    var navController: NavController? = null
        set(value) {
            field = value
            authNavigator.navController = value
        }

    fun onLoginClick(context: Context, authParams: AuthParams) {
        authNavigator.onLoginClick(context, authParams)
    }

    fun openFeaturesScreen() {
        authNavigator.openFeaturesScreen()
    }
}