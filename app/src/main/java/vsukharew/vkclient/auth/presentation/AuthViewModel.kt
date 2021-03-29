package vsukharew.vkclient.auth.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vsukharew.vkclient.auth.data.model.AuthParams
import vsukharew.vkclient.common.livedata.SingleLiveData
import vsukharew.vkclient.common.livedata.SingleLiveEvent

class AuthViewModel : ViewModel() {
    val openBrowserForAuthEvent = MutableLiveData<SingleLiveEvent<AuthParams>>()
    val openFunctionScreenEvent = MutableLiveData<SingleLiveEvent<Unit>>()

    fun onLoginClick() {
        openBrowserForAuthEvent.value = SingleLiveEvent(AuthParams())
    }

    fun onLoginSuccess() {
        openFunctionScreenEvent.value = SingleLiveEvent(Unit)
    }
}