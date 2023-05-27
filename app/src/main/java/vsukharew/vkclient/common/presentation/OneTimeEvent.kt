package vsukharew.vkclient.common.presentation

import androidx.annotation.StringRes
import vsukharew.vkclient.common.extension.EMPTY

/**
 * Event that can be occurred only once
 */
sealed class OneTimeEvent {
    sealed class Perform : OneTimeEvent() {
        data class Alert(
            val title: String = String.EMPTY,
            val message: String = String.EMPTY,
            val isCancelable: Boolean = true,
            val positiveButton: String = String.EMPTY,
            val negativeButton: String = String.EMPTY,
            @StringRes val titleRes: Int? = null,
            @StringRes val messageRes: Int? = null,
            @StringRes val positiveButtonRes: Int? = null,
            @StringRes val negativeButtonRes: Int? = null,
            val positiveButtonListener: () -> Unit = {},
            val negativeButtonListener: () -> Unit = {}
        ) : OneTimeEvent()

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
        object Alert : Done()
    }
}