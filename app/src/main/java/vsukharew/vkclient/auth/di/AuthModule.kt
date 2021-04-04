package vsukharew.vkclient.auth.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import vsukharew.vkclient.auth.data.AuthRepo
import vsukharew.vkclient.auth.data.AuthRepository
import vsukharew.vkclient.auth.data.AuthStorage
import vsukharew.vkclient.auth.data.SharedPrefsAuthStorage
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.auth.domain.interactor.AuthInteractorImpl
import vsukharew.vkclient.auth.navigation.AuthCoordinator
import vsukharew.vkclient.auth.navigation.AuthNavigator
import vsukharew.vkclient.auth.presentation.AuthViewModel
import vsukharew.vkclient.common.di.SURVIVE_CONFIG_CHANGES_SCOPE

val authDataModule = module {
    single<AuthStorage> { SharedPrefsAuthStorage(androidContext()) }
    singleBy<AuthInteractor, AuthInteractorImpl>()
    singleBy<AuthRepo, AuthRepository>()
}

val authScreenModule = module {
    scope(named(SURVIVE_CONFIG_CHANGES_SCOPE)) {
        scoped { AuthNavigator() }
        scoped { AuthCoordinator(get()) }
    }
    viewModel { AuthViewModel(get()) }
}