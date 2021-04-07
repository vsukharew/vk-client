package vsukharew.vkclient.splash.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import vsukharew.vkclient.common.di.SURVIVE_CONFIG_CHANGES_SCOPE
import vsukharew.vkclient.splash.presentation.SplashCoordinator
import vsukharew.vkclient.splash.presentation.SplashNavigator
import vsukharew.vkclient.splash.presentation.SplashViewModel

val splashModule = module {
    scope(named(SURVIVE_CONFIG_CHANGES_SCOPE)) {
        scoped { SplashNavigator() }
        scoped { SplashCoordinator(get()) }
    }
    viewModel { SplashViewModel(get()) }
}