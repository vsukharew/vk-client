package vsukharew.vkclient.features.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vsukharew.vkclient.R
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
import vsukharew.vkclient.common.extension.resolve
import vsukharew.vkclient.common.livedata.SingleLiveEvent
import vsukharew.vkclient.common.presentation.BaseViewModel
import vsukharew.vkclient.common.presentation.OneTimeEvent
import vsukharew.vkclient.common.presentation.loadstate.FeaturesResetType
import vsukharew.vkclient.common.presentation.loadstate.FeaturesResetType.MAIN_LOADING
import vsukharew.vkclient.common.presentation.loadstate.FeaturesResetType.SWIPE_REFRESH
import vsukharew.vkclient.common.presentation.loadstate.FeaturesScreenAction
import vsukharew.vkclient.common.presentation.loadstate.ProfileInfoUiState
import vsukharew.vkclient.common.presentation.loadstate.ShortNameAvailabilityState
import vsukharew.vkclient.features.presentation.FeaturesUiState.LoadingState
import vsukharew.vkclient.features.presentation.FeaturesUiState.NavigateTo.SignInScreen
import vsukharew.vkclient.publishimage.attach.domain.interactor.ImageInteractor
import vsukharew.vkclient.screenname.model.ScreenNameAvailability.AVAILABLE
import vsukharew.vkclient.screenname.model.ScreenNameAvailability.CURRENT_USER_NAME
import vsukharew.vkclient.screenname.model.ScreenNameAvailability.EMPTY
import vsukharew.vkclient.screenname.model.ScreenNameAvailability.UNAVAILABLE

@FlowPreview
@ExperimentalCoroutinesApi
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
                    mutableEventsFlow.emit(OneTimeEvent.Perform.Alert(
                        messageRes = R.string.features_fragment_sign_out_dialog_text,
                        positiveButtonRes = R.string.ok_text,
                        positiveButtonListener = { onSignOutDialogClosed() }
                    ))
                }

                BROWSER -> {
                    mutableEventsFlow.emit(OneTimeEvent.Perform.Alert(
                        messageRes = R.string.features_fragment_sign_out_dialog_text,
                        positiveButtonRes = R.string.ok_text,
                        positiveButtonListener = { onSignOutDialogClosed() }
                    ))
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
            mutableUiState.update { it.copy(shouldNavigateTo = SignInScreen) }
        }
    }

    fun onSignOutComplete() {
        mutableUiState.update { it.copy(shouldNavigateTo = FeaturesUiState.NavigateTo.Nothing) }
    }

    fun retryLoadProfileInfo() {
        loadProfileInfo(MAIN_LOADING)
    }

    fun refreshProfileInfo() {
        loadProfileInfo(SWIPE_REFRESH)
    }

    fun onShortNameChanged(shortNameFlow: Flow<String>) {
        shortNameFlow.debounce(DELAY_MILLIS)
            .map(::shortNameAndAvailabilityState)
            .onEach { (shortName, state) ->
                mutableUiState.update {
                    it.copy(
                        currentShortName = shortName,
                        shortNameAvailabilityState = state
                    ).save()
                }
            }
            .filter { (_, shortNameState) -> shortNameState is ShortNameAvailabilityState.LoadingProgress }
            .mapLatest { (shortName, _) -> checkShortNameAvailability(shortName) }
            .onEach { state ->
                mutableUiState.update { it.copy(shortNameAvailabilityState = state).save() }
            }
            .launchIn(viewModelScope)
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
            accountInteractor.getProfileInfo()
                .resolve(
                    ifLeft = { error -> profileInfoError(resetType, error) },
                    ifRight = { profileInfo -> profileInfoSuccess(profileInfo) }
                )
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

    private fun shortNameAndAvailabilityState(shortName: String): Pair<String, ShortNameAvailabilityState> {
        val state = when {
            shortName == uiState.value.initialShortName -> ShortNameAvailabilityState.Success(
                CURRENT_USER_NAME
            )
            // restore saved state case
            shortName == uiState.value.currentShortName -> uiState.value.shortNameAvailabilityState
            shortName.isEmpty() -> ShortNameAvailabilityState.Success(EMPTY)
            else -> ShortNameAvailabilityState.LoadingProgress
        }
        return shortName to state
    }

    private suspend fun checkShortNameAvailability(shortName: String): ShortNameAvailabilityState {
        return withContext(dispatchers.io) {
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
    }

    private fun profileInfoSuccess(profileInfo: ProfileInfo) {
        mutableUiState.update {
            val currentShortName = profileInfo.screenName
            val shortNameAvailability =
                (currentShortName?.let { CURRENT_USER_NAME } ?: EMPTY)
                    .let(ShortNameAvailabilityState::Success)
            FeaturesUiState(
                LoadingState.Loaded,
                profileInfo,
                currentShortName = currentShortName,
                shortNameAvailabilityState = shortNameAvailability,
                it.shouldNavigateTo
            )
        }
    }

    private fun profileInfoError(
        resetType: FeaturesResetType,
        error: AppError
    ) {
        when (resetType) {
            MAIN_LOADING -> LoadingState.Error
            SWIPE_REFRESH -> LoadingState.SwipeRefreshError
        }.let { state ->
            mutableUiState.update {
                it.copy(loadingState = state).save()
            }
        }
        handleError(error)
    }

    private fun FeaturesUiState.save(): FeaturesUiState {
        return also { save(savedState) }
    }

    private companion object {
        private const val KEY_SHORT_NAME = "short_name"
        private const val KEY_PROFILE_INFO = "profile_info"
        private const val KEY_SELECTION_STATE_INFO = "selection_state"
        private const val DELAY_MILLIS = 500L
    }
}