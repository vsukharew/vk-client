package vsukharew.vkclient.auth.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.*
import vsukharew.vkclient.auth.data.model.AuthParams
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.auth.domain.model.AuthType
import vsukharew.vkclient.auth.domain.model.Token
import vsukharew.vkclient.auth.navigation.BrowserAuthScreen
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseViewModel
import vsukharew.vkclient.common.utils.IntentChecker
import vsukharew.vkclient.common.navigation.NavigationComponentIntroScreen

class AuthViewModel(
    private val authInteractor: AuthInteractor,
    private val router: Router,
    private val intentChecker: IntentChecker
) : BaseViewModel() {
    val openExternalAuthScreenEvent = MutableLiveData<SingleLiveEvent<AuthParams>>()

    fun onLoginClick() {
        val authParams = AuthParams()
        when {
            intentChecker.isIntentAvailable(VK_APP_PACKAGE_NAME, VK_APP_AUTH_ACTION, null) -> {
                openExternalAuthScreenEvent.value = SingleLiveEvent(authParams)
            }
            else -> router.navigateTo(BrowserAuthScreen(authParams.completeUrl))
        }
    }

    fun onLoginSuccess(authResponse: Map<String, String>, authType: AuthType) {
        viewModelScope.launch {
            val context = Dispatchers.IO
            awaitAll(
                async(context = context) { authInteractor.saveAuthType(authType) },
                async(context = context) { authInteractor.saveToken(Token(authResponse)) }
            )
            router.replaceScreen(NavigationComponentIntroScreen())
        }
    }

    companion object {
        private const val VK_APP_PACKAGE_NAME = "com.vkontakte.android"
        private const val VK_APP_AUTH_ACTION = "com.vkontakte.android.action.SDK_AUTH"
    }
}