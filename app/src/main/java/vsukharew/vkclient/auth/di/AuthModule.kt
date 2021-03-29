package vsukharew.vkclient.auth.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import vsukharew.vkclient.auth.navigation.AuthCoordinator
import vsukharew.vkclient.auth.navigation.AuthNavigator
import vsukharew.vkclient.auth.presentation.AuthViewModel

const val AUTH_SCREEN_SCOPE = "auth_screen_scope"

val authScreenModule = module {
    scope(named(AUTH_SCREEN_SCOPE)) {
        scoped { AuthNavigator() }
        scoped { AuthCoordinator(get()) }
    }
    viewModel { AuthViewModel() }
}