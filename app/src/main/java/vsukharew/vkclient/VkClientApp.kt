package vsukharew.vkclient

import android.app.Application
import com.google.crypto.tink.aead.AeadConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import vsukharew.vkclient.auth.di.authDataModule
import vsukharew.vkclient.auth.di.authScreenModule
import vsukharew.vkclient.splash.di.splashModule

class VkClientApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@VkClientApp)
            modules(listOf(authScreenModule, authDataModule, splashModule))
        }
        AeadConfig.register()
    }
}