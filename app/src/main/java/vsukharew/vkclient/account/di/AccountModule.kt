package vsukharew.vkclient.account.di

import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import retrofit2.Retrofit
import vsukharew.vkclient.account.data.AccountRepo
import vsukharew.vkclient.account.data.AccountRepository
import vsukharew.vkclient.account.data.network.AccountApi

private fun provideAccountApi(retrofit: Retrofit) = retrofit.create(AccountApi::class.java)

val accountDataModule = module {
    single { provideAccountApi(get()) }
    singleBy<AccountRepo, AccountRepository>()
}