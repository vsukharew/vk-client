package vsukharew.vkclient.common.network.calladapter.uploadimage

import retrofit2.Call
import retrofit2.CallAdapter
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.publishimage.attach.data.model.UploadedImageWrapper
import java.lang.reflect.Type

/**
 * Adapter for calls of type
 * ```
 *  UploadImageWrapper
 * ```
 *
 * @see [Either]
 * @see [ResponseWrapper]
 */
class EitherUploadImageWrapperAdapter(
    private val type: Type
) : CallAdapter<UploadedImageWrapper, Call<Either<UploadedImageWrapper, AppError>>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<UploadedImageWrapper>): Call<Either<UploadedImageWrapper, AppError>> =
        UploadImageWrapperCall(call)
}