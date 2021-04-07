package vsukharew.vkclient.common.network

import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vsukharew.vkclient.auth.data.AuthStorage
import vsukharew.vkclient.common.network.calladapter.ResultAdapterFactory
import vsukharew.vkclient.common.network.interceptor.AddTokenInterceptor

private fun provideOkHttpClient(authStorage: AuthStorage): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(AddTokenInterceptor(authStorage))
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