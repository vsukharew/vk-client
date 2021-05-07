package vsukharew.vkclient.common.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import vsukharew.vkclient.R
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.domain.model.Result.Error.*
import vsukharew.vkclient.common.domain.model.Result.Error.HttpError.ClientError.OtherClientError
import vsukharew.vkclient.common.domain.model.Result.Error.HttpError.ClientError.UnauthorizedError
import vsukharew.vkclient.common.domain.model.Result.Error.HttpError.OtherHttpError
import vsukharew.vkclient.common.domain.model.Result.Error.HttpError.ServerError
import vsukharew.vkclient.common.extension.snackBar

class ErrorHandler(
    private val sessionInteractor: SessionInteractor
) : CoroutineScope by CoroutineScope(Dispatchers.Main) {

    fun handleError(fragment: BaseFragment<*>, error: Result.Error) {
        with(fragment) {
            when (error) {
                UnauthorizedError -> {
                    launch {
                        sessionInteractor.clearSessionData()
                        navController.navigate(R.id.global_action_to_authFragment)
                    }
                }
                is ServerError -> snackBar(R.string.unknown_server_error_text)
                is NetworkError -> snackBar(R.string.network_error_text)
                is UnknownError,
                is OtherClientError,
                is OtherHttpError ,
                is DomainError -> fragment.snackBar(R.string.unknown_error_text)
            }
        }
    }

    fun cancelCoroutineScope() {
        cancel()
    }
}