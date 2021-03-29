package vsukharew.vkclient

import android.app.Application
import org.koin.core.context.startKoin
import vsukharew.vkclient.auth.di.authScreenModule

class VkClientApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin { modules(listOf(authScreenModule)) }
    }
}