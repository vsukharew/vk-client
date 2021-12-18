package vsukharew.vkclient.common.network.calladapter.responsewrapper

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import vsukharew.vkclient.common.network.response.ResponseWrapper
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResultResponseWrapperAdapterFactory : CallAdapter.Factory() {
    private val lazyMessage = { "return type must be parameterized" }

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) {
            return null
        }

        check(returnType is ParameterizedType, lazyMessage)
        val outerResponseType = getParameterUpperBound(0, returnType)
        check(outerResponseType is ParameterizedType, lazyMessage)
        val innerResponseType = getParameterUpperBound(0, outerResponseType)
        if (getRawType(innerResponseType) != ResponseWrapper::class.java) {
            return null
        }

        check(innerResponseType is ParameterizedType) { "response type must be parameterized" }
        return ResultResponseWrapperAdapter<ResponseWrapper<Any>>(innerResponseType)
    }
}