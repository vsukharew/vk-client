package vsukharew.vkclient.common.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import vsukharew.vkclient.auth.data.AuthStorage
import vsukharew.vkclient.auth.data.SharedPrefsAuthStorage

val appModule = module {
    single<AuthStorage> { SharedPrefsAuthStorage(androidContext()) }
}