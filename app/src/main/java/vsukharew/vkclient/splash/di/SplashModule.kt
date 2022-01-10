package vsukharew.vkclient.splash.di

import androidx.navigation.NavController
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vsukharew.vkclient.splash.presentation.*

val splashModule = module {
    scope<SplashFragment> {
        scoped { (navController: NavController) -> SplashCoordinator(navController) }
        viewModel { SplashViewModel(get()) }
    }
}