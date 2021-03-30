package vsukharew.vkclient.auth.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vsukharew.vkclient.auth.data.model.AuthParams
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.auth.domain.model.Token
import vsukharew.vkclient.common.livedata.SingleLiveEvent

class AuthViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {
    val openBrowserForAuthEvent = MutableLiveData<SingleLiveEvent<AuthParams>>()
    val openFunctionScreenEvent = MutableLiveData<SingleLiveEvent<Unit>>()

    fun onLoginClick() {
        openBrowserForAuthEvent.value = SingleLiveEvent(AuthParams())
    }

    fun onLoginSuccess(authResponse: Map<String, String>) {
        viewModelScope.launch {
            authInteractor.saveToken(Token(authResponse))
            openFunctionScreenEvent.value = SingleLiveEvent(Unit)
        }
    }
}