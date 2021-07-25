package vsukharew.vkclient.common.network.calladapter.responsewrapper

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import vsukharew.vkclient.common.domain.model.Either
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResultResponseWrapperAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) {
            return null
        }

        check(returnType is ParameterizedType) {
            "return type must be parameterized"
        }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != Either::class.java) {
            return null
        }

        check(responseType is ParameterizedType) { "response type must be parameterized" }

        // type of the data that is inside response type
        val dataType = getParameterUpperBound(0, responseType)
        return ResultResponseWrapperAdapter<Any>(dataType)
    }
}