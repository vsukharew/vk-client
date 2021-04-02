package vsukharew.vkclient.auth.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import vsukharew.vkclient.R
import vsukharew.vkclient.auth.data.model.AuthParams
import vsukharew.vkclient.auth.presentation.AuthFragmentDirections
import vsukharew.vkclient.common.navigation.BaseNavigator

class AuthNavigator : BaseNavigator() {
    var vkActivityLauncher: ActivityResultLauncher<Intent>? = null
    var navController: NavController? = null

    fun onLoginClick(context: Context, authParams: AuthParams) {
        if (isIntentAvailable(context, VK_APP_PACKAGE_NAME, VK_APP_AUTH_ACTION, null)) {
            openVkActivity(authParams)
        } else {
            openBrowser(context, authParams.completeUrl)
        }
    }

    fun openFeaturesScreen() {
        navController?.navigate(
            AuthFragmentDirections.actionAuthFragmentToFunctionalGraph(),
            NavOptions.Builder()
                .setPopUpTo(R.id.authFragment, true)
                .build()
        )
    }

    private fun openVkActivity(authParams: AuthParams) {
        vkActivityLauncher?.launch(
            Intent(VK_APP_AUTH_ACTION, null).apply {
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
        )
    }

    private fun openBrowser(context: Context, url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
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