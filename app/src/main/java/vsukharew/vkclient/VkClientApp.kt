package vsukharew.vkclient

import android.app.Application
import com.google.crypto.tink.aead.AeadConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import vsukharew.vkclient.account.di.accountDataModule
import vsukharew.vkclient.auth.di.authDataModule
import vsukharew.vkclient.auth.di.authScreenModule
import vsukharew.vkclient.common.di.appModule
import vsukharew.vkclient.common.network.networkModule
import vsukharew.vkclient.features.di.featuresScreenModule
import vsukharew.vkclient.splash.di.splashModule

class VkClientApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@VkClientApp)
            modules(
                listOf(
                    appModule,
                    networkModule,
                    authDataModule,
                    authScreenModule,
                    splashModule,
                    accountDataModule,
                    featuresScreenModule
                )
            )
        }
        AeadConfig.register()
    }
}