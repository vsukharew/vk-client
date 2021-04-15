package vsukharew.vkclient.features.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vsukharew.vkclient.account.data.AccountRepo
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.auth.domain.model.AuthType.APP
import vsukharew.vkclient.auth.domain.model.AuthType.BROWSER
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.loadstate.UIAction
import vsukharew.vkclient.common.presentation.loadstate.UIState

class FeaturesViewModel(
    private val accountRepo: AccountRepo,
    private val authInteractor: AuthInteractor,
    private val sessionInteractor: SessionInteractor
) : ViewModel() {

    private val profileInfoAction = MutableLiveData<UIAction>(UIAction.InitialLoading)
    val profileUiState = Transformations.switchMap(profileInfoAction, ::loadProfileInfo)

    val signOutEvent = MutableLiveData<SingleLiveEvent<Unit>>()
    val signOutDialogEvent = MutableLiveData<Unit>()
    val signOutDialogClosedEvent = MutableLiveData<SingleLiveEvent<Unit>>()

    fun onSignOutClick() {
        viewModelScope.launch {
            when (authInteractor.getAuthType()) {
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

    fun retryLoadProfileInfo() {
        profileInfoAction.value = UIAction.Retry
    }

    fun refreshProfileInfo() {
        profileInfoAction.value = UIAction.SwipeRefresh
    }

    private fun loadProfileInfo(action: UIAction): LiveData<UIState<ProfileInfo>> {
        return liveData {
            val loadingState = when (action) {
                UIAction.Retry, UIAction.InitialLoading -> UIState.LoadingProgress
                UIAction.SwipeRefresh -> UIState.SwipeRefreshProgress
                else -> return@liveData
            }
            emit(loadingState)
            val info = withContext(Dispatchers.IO) { accountRepo.getProfileInfo() }
            emit(
                when (info) {
                    is Result.Success -> UIState.Success(info)
                    is Result.Error -> {
                        val error = SingleLiveEvent(info)
                        when (action) {
                            is UIAction.SwipeRefresh -> UIState.SwipeRefreshError(error)
                            else -> UIState.Error(error)
                        }
                    }
                }
            )
        }
    }
}