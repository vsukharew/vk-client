package vsukharew.vkclient.common.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vsukharew.vkclient.BuildConfig
import vsukharew.vkclient.account.data.model.ScreenNameResponse
import vsukharew.vkclient.auth.data.AuthRepo
import vsukharew.vkclient.common.network.calladapter.responsewrapper.EitherResponseWrapperAdapterFactory
import vsukharew.vkclient.common.network.calladapter.uploadimage.UploadImageWrapperAdapterFactory
import vsukharew.vkclient.common.network.deserializer.ResolvedScreenNameDeserializer
import vsukharew.vkclient.common.network.interceptor.AddTokenInterceptor
import java.util.concurrent.TimeUnit

private fun provideGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(ScreenNameResponse::class.java, ResolvedScreenNameDeserializer())
        .create()
}

private fun provideOkHttpClient(authRepo: AuthRepo): OkHttpClient {
    return OkHttpClient.Builder()
        .apply {
            addInterceptor(AddTokenInterceptor(authRepo))
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

private fun provideRetrofit(authRepo: AuthRepo): Retrofit {
    return Retrofit.Builder()
        .baseUrl(ServerUrls.BASE_URL)
        .client(provideOkHttpClient(authRepo))
        .addCallAdapterFactory(EitherResponseWrapperAdapterFactory())
        .addCallAdapterFactory(UploadImageWrapperAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create(provideGson()))
        .build()
}

val networkModule = module {
    single {
        provideRetrofit(get())
    }
}