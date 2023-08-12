package vsukharew.vkclient.common.presentation

import androidx.appcompat.app.AlertDialog
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
import vsukharew.vkclient.common.extension.snackBarIndefinite
import vsukharew.vkclient.common.extension.snackBarLong
import vsukharew.vkclient.common.extension.toast
import vsukharew.vkclient.common.presentation.OneTimeEvent.Perform.SnackBar.Length.INDEFINITE
import vsukharew.vkclient.common.presentation.OneTimeEvent.Perform.SnackBar.Length.LONG
import vsukharew.vkclient.common.presentation.OneTimeEvent.Perform.SnackBar.Length.SHORT

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
                is OneTimeEvent.Perform.Alert -> {
                    AlertDialog.Builder(fragment.requireContext())
                        .setTitle(event)
                        .setMessage(event)
                        .setNegativeButton(event, viewModel)
                        .setPositiveButton(event, viewModel)
                        .setCancelable(event.isCancelable)
                        .create()
                        .show()
                }
                is OneTimeEvent.Perform.SnackBar.Message -> {
                    snackBar(event.message)
                    viewModel.snackBarHidden()
                }
                is OneTimeEvent.Perform.SnackBar.StringResource -> {
                    event.run {
                        when (length) {
                            SHORT -> snackBar(resId)
                            LONG -> snackBarLong(resId, actionTextResId, action)
                            INDEFINITE -> snackBarIndefinite(resId, actionTextResId, action)
                        }
                    }
                    viewModel.snackBarHidden()
                }
                is OneTimeEvent.Perform.Toast.Message -> {
                    toast(event.message)
                    viewModel.toastHidden()
                }
                is OneTimeEvent.Perform.Toast.StringResource -> {
                    toast(event.resId)
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

    private fun AlertDialog.Builder.setTitle(event: OneTimeEvent.Perform.Alert): AlertDialog.Builder {
        return event.run { titleRes?.let { setTitle(it) } ?: setTitle(title) }
    }

    private fun AlertDialog.Builder.setMessage(event: OneTimeEvent.Perform.Alert): AlertDialog.Builder {
        return event.run { messageRes?.let { setMessage(it) } ?: setMessage(message) }
    }

    private fun AlertDialog.Builder.setNegativeButton(
        event: OneTimeEvent.Perform.Alert,
        viewModel: EventsViewModel
    ): AlertDialog.Builder {
        return event.run {
            negativeButtonRes?.let {
                setNegativeButton(it) { _, _ ->
                    viewModel.alertHidden()
                    negativeButtonListener.invoke()
                }
            } ?: setNegativeButton(event.negativeButton) { _, _ ->
                viewModel.alertHidden()
                negativeButtonListener.invoke()
            }
        }
    }

    private fun AlertDialog.Builder.setPositiveButton(
        event: OneTimeEvent.Perform.Alert,
        viewModel: EventsViewModel
    ): AlertDialog.Builder {
        return event.run {
            positiveButtonRes?.let {
                setPositiveButton(it) { _, _ ->
                    viewModel.alertHidden()
                    positiveButtonListener.invoke()
                }
            } ?: setPositiveButton(event.positiveButton) { _, _ ->
                viewModel.alertHidden()
                positiveButtonListener.invoke()
            }
        }
    }
}