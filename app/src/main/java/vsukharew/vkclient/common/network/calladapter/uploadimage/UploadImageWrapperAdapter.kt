package vsukharew.vkclient.common.network.calladapter.uploadimage

import retrofit2.Call
import retrofit2.CallAdapter
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.publishimage.attach.data.model.UploadedImageWrapper
import java.lang.reflect.Type

/**
 * Adapter for calls of type
 * ```
 *  UploadImageWrapper
 * ```
 *
 * @see [Result]
 * @see [ResponseWrapper]
 */
class UploadImageWrapperAdapter(
    private val type: Type
) : CallAdapter<UploadedImageWrapper, Call<UploadedImageWrapper>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<UploadedImageWrapper>): Call<UploadedImageWrapper> =
        UploadImageWrapperCall(call)
}