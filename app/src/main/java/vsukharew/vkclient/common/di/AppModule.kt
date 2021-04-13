package vsukharew.vkclient.common.di

import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import vsukharew.vkclient.auth.data.AuthStorage
import vsukharew.vkclient.auth.data.SharedPrefsAuthStorage
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.interactor.SessionInteractorImpl
import vsukharew.vkclient.common.presentation.ErrorHandler

val appModule = module {
    singleBy<AuthStorage, SharedPrefsAuthStorage>()
    singleBy<SessionInteractor, SessionInteractorImpl>()
    single { ErrorHandler(get()) }
}