package vsukharew.vkclient.auth.navigation

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import vsukharew.vkclient.auth.data.model.AuthParams

class AuthCoordinator(private val authNavigator: AuthNavigator) {
    var vkActivityLauncher: ActivityResultLauncher<Intent>? = null
    set(value) {
        field = value
        value?.let { authNavigator.vkActivityLauncher = it  }
    }

    fun onLoginClick(context: Context, authParams: AuthParams) {
        authNavigator.onLoginClick(context, authParams)
    }
}