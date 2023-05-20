package vsukharew.vkclient.common.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import vsukharew.vkclient.R
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.AppError.*
import vsukharew.vkclient.common.domain.model.AppError.RemoteError.ServerError
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.extension.snackBar
import vsukharew.vkclient.common.extension.toast

class ErrorHandler(
    private val sessionInteractor: SessionInteractor
) : CoroutineScope by CoroutineScope(Dispatchers.Main) {

    fun handleEvent(fragment: BaseFragment<*>, viewModel: EventsViewModel, event: OneTimeEvent) {
        with(fragment) {
            when (event) {
                is OneTimeEvent.Perform.SignOut -> {
                    navController.navigate(R.id.global_action_to_authFragment)
                    viewModel.signOutCompleted()
                }
                is OneTimeEvent.Perform.SnackBar.Message -> {
                    snackBar(event.message)
                    viewModel.snackBarHidden()
                }
                is OneTimeEvent.Perform.SnackBar.StringResource -> {
                    snackBar(event.resId)
                    viewModel.snackBarHidden()
                }
                is OneTimeEvent.Perform.Toast.Message -> {
                    toast(event.message)
                    viewModel.toastHidden()
                }
                is OneTimeEvent.Perform.Toast.StringResource -> {
                    snackBar(event.resId)
                    viewModel.toastHidden()
                }
                is OneTimeEvent.Done -> {
                    return
                }
            }
        }
    }

    fun <T> handleError(fragment: BaseFragment<*>, error: Left<T>) {
        with(fragment) {
            when (error.data) {
                is AppError -> {
                    when (error.data) {
                        RemoteError.Unauthorized -> {
                            launch {
                                sessionInteractor.clearSessionData()
                                navController.navigate(R.id.global_action_to_authFragment)
                            }
                        }
                        is RemoteError.TooMuchRequestsPerSecond -> {
                            snackBar(error.data.errorBody.errorMsg)
                        }
                        is ServerError -> snackBar(R.string.unknown_server_error_text)
                        is NetworkError -> snackBar(R.string.network_error_text)
                        is UnknownError,
                        is DomainError -> fragment.snackBar(R.string.unknown_error_text)
                        RemoteError.UnknownError -> TODO()
                    }
                }
                else -> {
                    snackBar(R.string.network_error_text)
                }
            }
        }
    }

    fun cancelCoroutineScope() {
        cancel()
    }
}