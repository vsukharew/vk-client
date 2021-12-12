package vsukharew.vkclient.auth.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.github.terrakok.cicerone.androidx.ActivityScreen
import vsukharew.vkclient.auth.data.model.AuthParams

class ExternalActivityAuthScreen(private val authParams: AuthParams) : ActivityScreen {
    override fun createIntent(context: Context): Intent {
        return Intent(VK_APP_AUTH_ACTION, null).apply {
            setPackage(VK_APP_PACKAGE_NAME)
            putExtras(
                Bundle().apply {
                    putInt(VK_EXTRA_CLIENT_ID, authParams.clientId)
                    putBoolean(VK_EXTRA_REVOKE, authParams.revoke)
                    putString(VK_EXTRA_SCOPE, authParams.scopes)
                    putString(VK_EXTRA_REDIRECT_URL, authParams.redirectUrl)
                }
            )
        }
    }

    companion object {
        private const val VK_APP_PACKAGE_NAME = "com.vkontakte.android"
        private const val VK_APP_AUTH_ACTION = "com.vkontakte.android.action.SDK_AUTH"
        private const val VK_EXTRA_CLIENT_ID = "client_id"
        private const val VK_EXTRA_SCOPE = "scope"
        private const val VK_EXTRA_REVOKE = "revoke"
        private const val VK_EXTRA_REDIRECT_URL = "redirect_url"
    }
}