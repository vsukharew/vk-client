package vsukharew.vkclient.auth.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.scopedBy
import vsukharew.vkclient.auth.data.AuthRepo
import vsukharew.vkclient.auth.data.AuthRepository
import vsukharew.vkclient.auth.domain.interactor.AuthInteractor
import vsukharew.vkclient.auth.domain.interactor.AuthInteractorImpl
import vsukharew.vkclient.auth.navigation.AuthCoordinator
import vsukharew.vkclient.auth.navigation.AuthNavigator
import vsukharew.vkclient.auth.presentation.AuthFragment
import vsukharew.vkclient.auth.presentation.AuthViewModel
import vsukharew.vkclient.common.di.DIScopes

val authDataModule = module {
    scope(named(DIScopes.AUTH_DATA)) {
        scopedBy<AuthRepo, AuthRepository>()
        scopedBy<AuthInteractor, AuthInteractorImpl>()
    }
}

val authScreenModule = module {
    scope<AuthFragment> {
        scoped { AuthNavigator() }
        scoped { AuthCoordinator(get()) }
        viewModel { AuthViewModel(get()) }
    }
}