package vsukharew.vkclient.common.network.calladapter.responsewrapper

import retrofit2.Call
import retrofit2.CallAdapter
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.AppError
import java.lang.reflect.Type

/**
 * Adapter for calls of type
 * ```
 *  Result<ResponseWrapper<T>>
 * ```
 *
 * @see [Either]
 * @see [ResponseWrapper]
 */
class EitherResponseWrapperAdapter<S>(
    private val type: Type
) : CallAdapter<ResponseWrapper<S>, Call<Either<AppError, ResponseWrapper<S>>>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<ResponseWrapper<S>>): Call<Either<AppError, ResponseWrapper<S>>> =
        EitherCall(call)
}