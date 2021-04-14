package vsukharew.vkclient.features.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vsukharew.vkclient.account.data.AccountRepo
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.auth.domain.model.AuthType.APP
import vsukharew.vkclient.auth.domain.model.AuthType.BROWSER
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent

class FeaturesViewModel(
    accountRepo: AccountRepo,
    private val authInteractor: AuthInteractor,
    private val sessionInteractor: SessionInteractor
) : ViewModel() {
    val profileInfo = liveData {
        isLoading.value = true
        signOutButtonVisible.value = false
        val info = accountRepo.getProfileInfo()
        isLoading.value = false
        if (info is Result.Success) {
            signOutButtonVisible.value = true
        }
        emit(SingleLiveEvent(info))
    }
    val signOutButtonVisible = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    val signOutEvent = MutableLiveData<SingleLiveEvent<Unit>>()
    val signOutDialogEvent = MutableLiveData<Unit>()
    val signOutDialogClosedEvent = MutableLiveData<SingleLiveEvent<Unit>>()

    fun onSignOutClick() {
        viewModelScope.launch {
            when(authInteractor.getAuthType()) {
                APP -> {
                    sessionInteractor.clearSessionData()
                    signOutEvent.value = SingleLiveEvent(Unit)
                }
                BROWSER -> {
                    signOutDialogEvent.value = Unit
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    fun onSignOutDialogClosed() {
        viewModelScope.launch {
            sessionInteractor.clearSessionData()
            signOutDialogClosedEvent.value = SingleLiveEvent(Unit)
        }
    }
}