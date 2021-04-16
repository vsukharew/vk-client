package vsukharew.vkclient.features.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vsukharew.vkclient.account.domain.interactor.AccountInteractor
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.auth.domain.model.AuthType.APP
import vsukharew.vkclient.auth.domain.model.AuthType.BROWSER
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.loadstate.UIAction
import vsukharew.vkclient.common.presentation.loadstate.UIState
import vsukharew.vkclient.screenname.model.ScreenNameAvailability
import vsukharew.vkclient.screenname.model.ScreenNameAvailability.*

class FeaturesViewModel(
    private val accountInteractor: AccountInteractor,
    private val authInteractor: AuthInteractor,
    private val sessionInteractor: SessionInteractor
) : ViewModel() {

    private val profileInfoAction = MutableLiveData<UIAction>(UIAction.InitialLoading)
    val profileUiState = Transformations.switchMap(profileInfoAction, ::loadProfileInfo)

    private val shortNameAction = MutableLiveData<UIAction.Text>()
    val shortNameUiState = Transformations.switchMap(shortNameAction, ::checkShortNameAvailability)

    private var currentShortName: String? = null
    val shortNameTextState = MutableLiveData<String>()

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
            val info = withContext(Dispatchers.IO) { accountInteractor.getProfileInfo() }
            emit(
                when (info) {
                    is Result.Success -> UIState.Success(info.data)
                        .also { currentShortName = info.data.screenName }
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

    fun onShortNameChanged(shortName: String) {
        shortNameTextState.value = shortName
        with(shortNameTextState) {
            when (value) {
                shortNameAction.value?.text -> return
                else -> shortNameAction.value = UIAction.Text(shortName)
            }
        }
    }

    private fun checkShortNameAvailability(
        action: UIAction.Text
    ): LiveData<UIState<ScreenNameAvailability>> {
        return liveData {
            when(action.text) {
                currentShortName -> {
                    emit(UIState.Success(CURRENT_USER_NAME))
                    return@liveData
                }
                else -> emit(UIState.LoadingProgress)
            }
            val doesExist = withContext(Dispatchers.IO) {
                accountInteractor.doesShortNameExist(action.text)
            }
            emit(
                when (doesExist) {
                    is Result.Success -> {
                        val availability = when {
                            doesExist.data -> UNAVAILABLE
                            else -> AVAILABLE
                        }
                        UIState.Success(availability)
                    }
                    is Result.Error -> {
                        val error = SingleLiveEvent(doesExist)
                        UIState.Error(error)
                    }
                }
            )
        }
    }
}