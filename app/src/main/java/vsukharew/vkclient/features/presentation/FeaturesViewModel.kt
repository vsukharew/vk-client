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
import vsukharew.vkclient.common.extension.EMPTY
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseViewModel
import vsukharew.vkclient.common.presentation.loadstate.ProfileInfoUiState
import vsukharew.vkclient.common.presentation.loadstate.FeaturesScreenAction
import vsukharew.vkclient.common.presentation.loadstate.ShortNameAvailabilityState
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.screenname.model.ScreenNameAvailability.*

class FeaturesViewModel(
    private val accountInteractor: AccountInteractor,
    private val authInteractor: AuthInteractor,
    private val sessionInteractor: SessionInteractor,
    private val savedState: SavedStateHandle,
    imageInteractor: ImageInteractor
) : BaseViewModel() {

    private val profileInfoAction = MutableLiveData<FeaturesScreenAction>(FeaturesScreenAction.InitialLoading)
    val profileUiState = Transformations.switchMap(profileInfoAction, ::loadProfileInfo)

    private val shortNameAction = MutableLiveData<FeaturesScreenAction.Text>()
    val shortNameUiState = Transformations.switchMap(shortNameAction, ::checkShortNameAvailability)

    private var currentShortName: String? = savedState[KEY_SHORT_NAME]
    val shortNameTextState = MutableLiveData<String>()

    val selectionState = savedState.getLiveData<Int>(KEY_SELECTION_STATE_INFO)
    val signOutEvent = MutableLiveData<SingleLiveEvent<Unit>>()
    val signOutDialogEvent = MutableLiveData<Unit>()
    val signOutDialogClosedEvent = MutableLiveData<SingleLiveEvent<Unit>>()
    val postPublishedEvent = imageInteractor.observePublishedPosts()
        .asLiveData(context = viewModelScope.coroutineContext)
        .map { SingleLiveEvent(it) }

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
        profileInfoAction.value = FeaturesScreenAction.Retry
    }

    fun refreshProfileInfo() {
        profileInfoAction.value = FeaturesScreenAction.SwipeRefresh
    }

    fun onShortNameChanged(shortName: String) {
        rewriteProfileInfo(shortName)
        shortNameTextState.value = shortName
        with(shortNameTextState) {
            when (value) {
                shortNameAction.value?.text -> {
                    return
                }
                else -> {
                    shortNameAction.value = FeaturesScreenAction.Text(shortName)
                }
            }
        }
    }

    fun saveCursorPosition(position: Int) {
        savedState[KEY_SELECTION_STATE_INFO] = position
    }

    private fun loadProfileInfo(action: FeaturesScreenAction): LiveData<ProfileInfoUiState> {
        return liveData {
            val loadingState = when (action) {
                FeaturesScreenAction.Retry, FeaturesScreenAction.InitialLoading -> ProfileInfoUiState.LoadingProgress
                FeaturesScreenAction.SwipeRefresh -> ProfileInfoUiState.SwipeRefreshProgress
                else -> return@liveData
            }
            emit(loadingState)
            when (action) {
                FeaturesScreenAction.SwipeRefresh, FeaturesScreenAction.Retry -> {
                    handleProfileInfoResult(this, accountInteractor.getProfileInfo(), action)
                }
                else -> {
                    savedState.get<ProfileInfo>(KEY_PROFILE_INFO)?.let {
                        emit(ProfileInfoUiState.Success(it))
                    } ?: handleProfileInfoResult(this, accountInteractor.getProfileInfo(), action)
                }
            }
        }
    }

    private suspend fun handleProfileInfoResult(
        scope: LiveDataScope<ProfileInfoUiState>,
        info: Result<ProfileInfo>,
        action: FeaturesScreenAction
    ) {
        scope.emit(
            when (info) {
                is Result.Success -> {
                    currentShortName = info.data.screenName.also { savedState[KEY_SHORT_NAME] = it }
                    val data = info.data.copy(screenName = currentShortName)
                    savedState[KEY_PROFILE_INFO] = data
                    ProfileInfoUiState.Success(data)
                }
                is Result.Error -> {
                    val errorEvent = SingleLiveEvent(info)
                    errorLiveData.value = errorEvent
                    when (action) {
                        is FeaturesScreenAction.SwipeRefresh -> {
                            val currentData = savedState.get<ProfileInfo>(KEY_PROFILE_INFO)!!
                            ProfileInfoUiState.SwipeRefreshError(currentData, errorEvent)
                        }
                        else -> ProfileInfoUiState.Error(errorEvent)
                    }
                }
            }
        )
    }

    private fun checkShortNameAvailability(
        action: FeaturesScreenAction.Text
    ): LiveData<ShortNameAvailabilityState> {
        return liveData {
            when (action.text) {
                currentShortName -> {
                    emit(ShortNameAvailabilityState.Success(CURRENT_USER_NAME))
                    return@liveData
                }
                String.EMPTY -> {
                    emit(ShortNameAvailabilityState.Success(EMPTY))
                    return@liveData
                }
                else -> emit(ShortNameAvailabilityState.LoadingProgress)
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
                        ShortNameAvailabilityState.Success(availability)
                    }
                    is Result.Error -> {
                        val error = SingleLiveEvent(doesExist)
                        ShortNameAvailabilityState.Error(error)
                    }
                }
            )
        }
    }

    private fun rewriteProfileInfo(currentShortName: String) {
        val currentProfileInfo = savedState.get<ProfileInfo>(KEY_PROFILE_INFO)
            ?.copy(screenName = currentShortName)
        savedState[KEY_PROFILE_INFO] = currentProfileInfo
    }

    private companion object {
        private const val KEY_SHORT_NAME = "short_name"
        private const val KEY_PROFILE_INFO = "profile_info"
        private const val KEY_SELECTION_STATE_INFO = "selection_state"
    }
}