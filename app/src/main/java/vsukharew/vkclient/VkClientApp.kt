package vsukharew.vkclient

import android.app.Application
import com.google.crypto.tink.aead.AeadConfig
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import vsukharew.vkclient.account.di.accountDataModule
import vsukharew.vkclient.auth.di.authDataModule
import vsukharew.vkclient.auth.di.authScreenModule
import vsukharew.vkclient.common.di.appModule
import vsukharew.vkclient.common.network.ServerUrls
import vsukharew.vkclient.common.network.networkModule
import vsukharew.vkclient.common.network.provideOkHttpClient
import vsukharew.vkclient.features.di.featuresScreenModule
import vsukharew.vkclient.publishimage.attach.di.attachImageScreenModule
import vsukharew.vkclient.publishimage.attach.di.chooseImageSourceDialogModule
import vsukharew.vkclient.publishimage.caption.di.captionScreenModule
import vsukharew.vkclient.publishimage.flow.di.publishImageFlowModule
import vsukharew.vkclient.splash.di.splashModule

class VkClientApp : Application() {
    @FlowPreview
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@VkClientApp)
            modules(
                listOf(
                    appModule,
                    authDataModule,
                    authScreenModule,
                    splashModule,
                    accountDataModule,
                    featuresScreenModule,
                    publishImageFlowModule,
                    attachImageScreenModule,
                    captionScreenModule,
                    chooseImageSourceDialogModule
                )
            )
            modules(networkModule(ServerUrls.BASE_URL, provideOkHttpClient(get())))
        }
        AeadConfig.register()
    }
}