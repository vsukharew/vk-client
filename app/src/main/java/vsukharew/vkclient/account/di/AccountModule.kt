package vsukharew.vkclient.account.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.scopedBy
import retrofit2.Retrofit
import vsukharew.vkclient.account.data.AccountRepo
import vsukharew.vkclient.account.data.AccountRepository
import vsukharew.vkclient.account.data.network.AccountApi
import vsukharew.vkclient.account.domain.interactor.AccountInteractor
import vsukharew.vkclient.account.domain.interactor.AccountInteractorImpl
import vsukharew.vkclient.common.di.DIScopes

private fun provideAccountApi(retrofit: Retrofit) = retrofit.create(AccountApi::class.java)

val accountDataModule = module {
    scope(named(DIScopes.ACCOUNT)) {
        scoped { provideAccountApi(get()) }
        scopedBy<AccountRepo, AccountRepository>()
        scopedBy<AccountInteractor, AccountInteractorImpl>()
    }
}