package vsukharew.vkclient.common.presentation

import androidx.annotation.StringRes

/**
 * Event that can be occurred only once
 */
sealed class OneTimeEvent {
    sealed class Perform : OneTimeEvent() {
        sealed class Toast : OneTimeEvent() {
            data class StringResource(@StringRes val resId: Int) : Toast()
            data class Message(val message: String) : Toast()
        }

        sealed class SnackBar : OneTimeEvent() {
            data class StringResource(@StringRes val resId: Int) : SnackBar()
            data class Message(val message: String) : SnackBar()
        }

        object SignOut : OneTimeEvent()
    }

    sealed class Done : OneTimeEvent() {
        object Toast : Done()
        object SnackBar : Done()
        object SignOut : Done()
    }
}