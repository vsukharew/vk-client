package vsukharew.vkclient.common.presentation

import androidx.lifecycle.ViewModel

/**
 * [ViewModel] that is able to handle [OneTimeEvent]
 */
interface EventsViewModel {
    fun toastHidden()
    fun signOutCompleted()
    fun snackBarHidden()
}