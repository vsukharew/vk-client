package vsukharew.vkclient.features.presentation

import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vsukharew.vkclient.account.domain.interactor.AccountInteractor
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.auth.domain.model.AuthType.APP
import vsukharew.vkclient.auth.domain.model.AuthType.BROWSER
import vsukharew.vkclient.common.DispatchersProvider
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right
import vsukharew.vkclient.common.extension.doIfLeft
import vsukharew.vkclient.common.extension.fold
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseViewModel
import vsukharew.vkclient.common.presentation.loadstate.FeaturesResetType
import vsukharew.vkclient.common.presentation.loadstate.FeaturesResetType.MAIN_LOADING
import vsukharew.vkclient.common.presentation.loadstate.FeaturesResetType.SWIPE_REFRESH
import vsukharew.vkclient.common.presentation.loadstate.ProfileInfoUiState
import vsukharew.vkclient.common.presentation.loadstate.FeaturesScreenAction
import vsukharew.vkclient.common.presentation.loadstate.ShortNameAvailabilityState
import vsukharew.vkclient.features.presentation.FeaturesUiState.LoadingState
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.screenname.model.ScreenNameAvailability.*

class FeaturesViewModel(
    private val accountInteractor: AccountInteractor,
    private val authInteractor: AuthInteractor,
    private val sessionInteractor: SessionInteractor,
    private val savedState: SavedStateHandle,
    private val dispatchers: DispatchersProvider,
    imageInteractor: ImageInteractor
) : BaseViewModel(sessionInteractor) {

    private val mutableUiState =
        MutableStateFlow(savedState[FeaturesUiState.KEY_SAVED_STATE] ?: FeaturesUiState.DEFAULT)
    val uiState = mutableUiState.asStateFlow()

    private val profileInfoAction =
        MutableLiveData<FeaturesScreenAction>(FeaturesScreenAction.InitialLoading)
    val profileUiState = profileInfoAction.switchMap(::loadProfileInfoLegacy)

    private var currentShortName: String? = savedState[KEY_SHORT_NAME]

    val signOutEvent = MutableLiveData<SingleLiveEvent<Unit>>()
    val signOutDialogEvent = MutableLiveData<Unit>()
    val signOutDialogClosedEvent = MutableLiveData<SingleLiveEvent<Unit>>()
    val postPublishedEvent = imageInteractor.observePublishedPosts()
        .asLiveData(context = viewModelScope.coroutineContext)
        .map { SingleLiveEvent(it) }

    init {
        savedState.get<FeaturesUiState>(FeaturesUiState.KEY_SAVED_STATE)
            ?: loadProfileInfo(MAIN_LOADING)
    }

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
        loadProfileInfo(MAIN_LOADING)
    }

    fun refreshProfileInfo() {
        loadProfileInfo(SWIPE_REFRESH)
    }

    suspend fun onShortNameChanged(shortName: String) {
        rewriteProfileInfo(shortName)
        checkShortNameAvailability(shortName)
    }

    fun saveCursorPosition(position: Int) {
        savedState[KEY_SELECTION_STATE_INFO] = position
    }

    private fun loadProfileInfo(resetType: FeaturesResetType) {
        when (resetType) {
            MAIN_LOADING -> LoadingState.MainLoading
            SWIPE_REFRESH -> LoadingState.SwipeRefresh(
                isEnabled = true,
                isRefreshing = true
            )
        }.let { state -> mutableUiState.update { it.copy(loadingState = state) } }
        viewModelScope.launch {
            when (val profileInfo = accountInteractor.getProfileInfo()) {
                is Right -> {
                    mutableUiState.update {
                        val currentShortName = profileInfo.data.screenName
                        val shortNameAvailability =
                            (currentShortName?.let { CURRENT_USER_NAME } ?: EMPTY)
                                .let(ShortNameAvailabilityState::Success)
                        FeaturesUiState(
                            LoadingState.Loaded,
                            profileInfo.data,
                            currentShortName = currentShortName,
                            shortNameAvailabilityState = shortNameAvailability,
                        )
                    }
                }

                is Left -> {
                    when (resetType) {
                        MAIN_LOADING -> LoadingState.Error
                        SWIPE_REFRESH -> LoadingState.SwipeRefreshError
                    }.let { state ->
                        mutableUiState.update {
                            it.copy(loadingState = state).save()
                        }
                    }
                    val errorEvent = SingleLiveEvent(profileInfo)
                    errorLiveData.value = errorEvent
                }
            }
        }
    }

    private fun loadProfileInfoLegacy(action: FeaturesScreenAction): LiveData<ProfileInfoUiState> {
        return liveData {
            val scope = this
            val loadingState = when (action) {
                FeaturesScreenAction.Retry, FeaturesScreenAction.InitialLoading -> ProfileInfoUiState.LoadingProgress
                FeaturesScreenAction.SwipeRefresh -> ProfileInfoUiState.SwipeRefreshProgress
                else -> return@liveData
            }
            emit(loadingState)
            when (action) {
                FeaturesScreenAction.SwipeRefresh, FeaturesScreenAction.Retry -> {
                    withContext(dispatchers.io) {
                        handleProfileInfoResult(scope, accountInteractor.getProfileInfo(), action)
                    }
                }

                else -> {
                    savedState.get<ProfileInfo>(KEY_PROFILE_INFO)?.let {
                        emit(ProfileInfoUiState.Success(it))
                    } ?: run {
                        withContext(dispatchers.io) {
                            handleProfileInfoResult(
                                scope,
                                accountInteractor.getProfileInfo(),
                                action
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun handleProfileInfoResult(
        scope: LiveDataScope<ProfileInfoUiState>,
        info: Either<AppError, ProfileInfo>,
        action: FeaturesScreenAction
    ) {
        withContext(dispatchers.main) {
            scope.emit(
                when (info) {
                    is Right -> {
                        currentShortName =
                            info.data.screenName.also { savedState[KEY_SHORT_NAME] = it }
                        val data = info.data.copy(screenName = currentShortName)
                        savedState[KEY_PROFILE_INFO] = data
                        ProfileInfoUiState.Success(data)
                    }

                    is Left -> {
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
    }

    private suspend fun checkShortNameAvailability(shortName: String) {
        with(mutableUiState) {
            val shortNameAvailabilityState = when {
                shortName == value.initialShortName -> ShortNameAvailabilityState.Success(
                    CURRENT_USER_NAME
                )
                // restore saved state case
                shortName == value.currentShortName -> value.shortNameAvailabilityState
                shortName.isEmpty() -> ShortNameAvailabilityState.Success(EMPTY)
                else -> ShortNameAvailabilityState.LoadingProgress
            }
            update { it.copy(currentShortName = shortName).save() }
            update {
                if (shortNameAvailabilityState is ShortNameAvailabilityState.LoadingProgress) {
                    it.copy(shortNameAvailabilityState = shortNameAvailabilityState)
                } else {
                    it.copy(shortNameAvailabilityState = shortNameAvailabilityState).save()
                }
            }
            when (shortNameAvailabilityState) {
                ShortNameAvailabilityState.LoadingProgress -> {
                    val state = withContext(dispatchers.io) {
                        accountInteractor.doesShortNameExist(shortName)
                    }.doIfLeft(::handleError)
                        .fold(
                            ifLeft = { ShortNameAvailabilityState.Error },
                            ifRight = { doesExist ->
                                val availability = when {
                                    doesExist -> UNAVAILABLE
                                    else -> AVAILABLE
                                }
                                ShortNameAvailabilityState.Success(availability)
                            }
                        )
                    update { it.copy(shortNameAvailabilityState = state).save() }
                }

                else -> {
                }
            }
        }
    }

    private fun rewriteProfileInfo(currentShortName: String) {
        val currentProfileInfo = savedState.get<ProfileInfo>(KEY_PROFILE_INFO)
            ?.copy(screenName = currentShortName)
        savedState[KEY_PROFILE_INFO] = currentProfileInfo
    }

    private fun FeaturesUiState.save(): FeaturesUiState {
        return also { save(savedState) }
    }

    private companion object {
        private const val KEY_SHORT_NAME = "short_name"
        private const val KEY_PROFILE_INFO = "profile_info"
        private const val KEY_SELECTION_STATE_INFO = "selection_state"
    }
}