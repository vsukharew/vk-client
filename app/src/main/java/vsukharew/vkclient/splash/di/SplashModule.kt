package vsukharew.vkclient.splash.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vsukharew.vkclient.splash.presentation.SplashCoordinator
import vsukharew.vkclient.splash.presentation.SplashFragment
import vsukharew.vkclient.splash.presentation.SplashNavigator
import vsukharew.vkclient.splash.presentation.SplashViewModel

val splashModule = module {
    scope<SplashFragment> {
        scoped { SplashNavigator() }
        scoped { SplashCoordinator(get()) }
        viewModel { SplashViewModel(get()) }
    }
}