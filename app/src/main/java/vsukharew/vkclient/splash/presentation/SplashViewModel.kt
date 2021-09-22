package vsukharew.vkclient.splash.presentation

import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.auth.navigation.AuthScreen
import vsukharew.vkclient.common.presentation.BaseViewModel
import vsukharew.vkclient.common.navigation.NavigationComponentIntroScreen

class SplashViewModel(
    private val authInteractor: AuthInteractor,
    private val router: Router
) : BaseViewModel() {

    fun openNextScreen() {
        viewModelScope.launch {
            val screen = if (authInteractor.isAuthorized()) {
                NavigationComponentIntroScreen()
            } else {
                AuthScreen()
            }
            router.replaceScreen(screen)
        }
    }
}