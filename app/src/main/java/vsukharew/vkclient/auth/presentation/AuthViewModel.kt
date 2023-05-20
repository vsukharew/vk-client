package vsukharew.vkclient.auth.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import vsukharew.vkclient.auth.data.model.AuthParams
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.auth.domain.model.AuthType
import vsukharew.vkclient.auth.domain.model.Token
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseViewModel
import kotlin.coroutines.CoroutineContext

class AuthViewModel(
    private val authInteractor: AuthInteractor,
    sessionInteractor: SessionInteractor,
) : BaseViewModel(sessionInteractor) {
    val openBrowserForAuthEvent = MutableLiveData<SingleLiveEvent<AuthParams>>()
    val openFunctionScreenEvent = MutableLiveData<SingleLiveEvent<Unit>>()

    fun onLoginClick() {
        openBrowserForAuthEvent.value = SingleLiveEvent(AuthParams())
    }

    fun onLoginSuccess(authResponse: Map<String, String>, authType: AuthType) {
        viewModelScope.launch {
            val context = Dispatchers.IO
            awaitAll(
                async(context = context) { authInteractor.saveAuthType(authType) },
                async(context = context) { authInteractor.saveToken(Token(authResponse)) }
            )
            openFunctionScreenEvent.value = SingleLiveEvent(Unit)
        }
    }
}