package vsukharew.vkclient.common.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import vsukharew.vkclient.auth.data.AuthRepo
import vsukharew.vkclient.auth.data.AuthRepository
import vsukharew.vkclient.auth.data.AuthStorage
import vsukharew.vkclient.auth.data.SharedPrefsAuthStorage
import vsukharew.vkclient.auth.data.storage.FileAuthStorage
import vsukharew.vkclient.common.domain.interactor.SessionInteractor
import vsukharew.vkclient.common.domain.interactor.SessionInteractorImpl
import vsukharew.vkclient.common.presentation.ErrorHandler

val appModule = module {
    singleBy<AuthStorage, SharedPrefsAuthStorage>(named("prefs"))
    singleBy<AuthStorage, FileAuthStorage>(named("file"))
    singleBy<SessionInteractor, SessionInteractorImpl>()
    single<AuthRepo> {
        AuthRepository(
            sharedPrefsStorage = get(named("prefs")),
            fileStorage = get(named("file"))
        )
    }
    single { ErrorHandler(get()) }
}