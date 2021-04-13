package vsukharew.vkclient.common.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vsukharew.vkclient.BuildConfig
import vsukharew.vkclient.auth.data.AuthStorage
import vsukharew.vkclient.common.network.calladapter.ResultAdapterFactory
import vsukharew.vkclient.common.network.interceptor.AddTokenInterceptor
import java.util.concurrent.TimeUnit

private fun provideOkHttpClient(authStorage: AuthStorage): OkHttpClient {
    return OkHttpClient.Builder()
        .apply {
            addInterceptor(AddTokenInterceptor(authStorage))
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
            }
            callTimeout(2, TimeUnit.MINUTES)
        }
        .build()
}

private fun provideRetrofit(authStorage: AuthStorage): Retrofit {
    return Retrofit.Builder()
        .baseUrl(ServerUrls.BASE_URL)
        .client(provideOkHttpClient(authStorage))
        .addCallAdapterFactory(ResultAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

val networkModule = module {
    single { provideRetrofit(get()) }
}