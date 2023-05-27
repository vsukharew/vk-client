package vsukharew.vkclient.common.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import vsukharew.vkclient.R
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.livedata.SingleLiveEvent

abstract class BaseViewModel(
    private val sessionInteractor: SessionInteractor,
) : ViewModel(), EventsViewModel {
    protected val mutableEventsFlow = MutableSharedFlow<OneTimeEvent>(replay = 1)
    val errorLiveData = MutableLiveData<SingleLiveEvent<Left<AppError>>>()
    val eventsFlow = mutableEventsFlow.asSharedFlow()

    protected fun handleError(error: AppError) {
        viewModelScope.launch {
            val event = if (error is AppError.RemoteError.Unauthorized) {
                sessionInteractor.clearSessionData()
                OneTimeEvent.Perform.SignOut
            } else {
                when (error) {
                    is AppError.RemoteError.TooMuchRequestsPerSecond -> {
                        OneTimeEvent.Perform.SnackBar.Message(error.errorBody.errorMsg)
                    }
                    is AppError.RemoteError.ServerError -> OneTimeEvent.Perform.Toast.StringResource(R.string.unknown_server_error_text)
                    is AppError.NetworkError -> OneTimeEvent.Perform.SnackBar.StringResource(R.string.network_error_text)
                    is AppError.UnknownError,
                    is AppError.DomainError,
                    is AppError.RemoteError.UnknownError -> OneTimeEvent.Perform.SnackBar.StringResource(R.string.unknown_error_text)
                    AppError.RemoteError.Unauthorized -> return@launch
                }
            }
            mutableEventsFlow.emit(event)
        }
    }

    override fun toastHidden() {
        viewModelScope.launch { mutableEventsFlow.emit(OneTimeEvent.Done.Toast) }
    }

    override fun alertHidden() {
        viewModelScope.launch { mutableEventsFlow.emit(OneTimeEvent.Done.Alert) }
    }

    override fun signOutCompleted() {
        viewModelScope.launch { mutableEventsFlow.emit(OneTimeEvent.Done.SignOut) }
    }

    override fun snackBarHidden() {
        viewModelScope.launch { mutableEventsFlow.emit(OneTimeEvent.Done.SnackBar) }
    }
}