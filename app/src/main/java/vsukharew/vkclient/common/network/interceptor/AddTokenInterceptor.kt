package vsukharew.vkclient.common.network.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import vsukharew.vkclient.auth.data.AuthStorage

class AddTokenInterceptor(private val authStorage: AuthStorage) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val newUrl = chain.request()
                .url()
                .newBuilder()
                .apply {
                    authStorage.getToken()
                        ?.accessToken
                        ?.let { addQueryParameter(QUERY_ACCESS_TOKEN_KEY, it) }
                        addQueryParameter("v", "5.90")
                }
                .build()

            val request = chain.request()
                .newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(request)
        }
    }

    private companion object {
        private const val QUERY_ACCESS_TOKEN_KEY = "access_token"
    }
}