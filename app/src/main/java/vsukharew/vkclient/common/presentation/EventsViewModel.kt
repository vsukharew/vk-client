package vsukharew.vkclient.common.presentation

import androidx.lifecycle.ViewModel

/**
 * [ViewModel] that is able to handle [OneTimeEvent]
 */
interface EventsViewModel {
    fun toastHidden()
    fun alertHidden()
    fun signOutCompleted()
    fun snackBarHidden()
}