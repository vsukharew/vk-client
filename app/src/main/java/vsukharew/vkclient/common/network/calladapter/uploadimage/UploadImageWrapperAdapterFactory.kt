package vsukharew.vkclient.common.network.calladapter.uploadimage

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import vsukharew.vkclient.publishimage.attach.data.model.UploadedImageWrapper
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class UploadImageWrapperAdapterFactory : CallAdapter.Factory() {

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
        if (getRawType(responseType) != UploadedImageWrapper::class.java) {
            return null
        }

        return UploadImageWrapperAdapter(responseType)
    }
}