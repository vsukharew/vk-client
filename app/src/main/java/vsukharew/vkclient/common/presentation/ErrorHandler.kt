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

class ErrorHandler(
    private val sessionInteractor: SessionInteractor
) : CoroutineScope by CoroutineScope(Dispatchers.Main) {

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