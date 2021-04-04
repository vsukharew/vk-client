package vsukharew.vkclient.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor

class SplashViewModel(private val authInteractor: AuthInteractor) : ViewModel() {
    val isAuthorized = liveData {
        emit(authInteractor.isAuthorized())
    }
}