package vsukharew.vkclient.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.common.presentation.BaseViewModel

class SplashViewModel(private val authInteractor: AuthInteractor) : BaseViewModel() {
    val isAuthorized = liveData {
        emit(authInteractor.isAuthorized())
    }
}