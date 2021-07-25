package vsukharew.vkclient.common.network.calladapter.responsewrapper

import retrofit2.Call
import retrofit2.CallAdapter
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.common.domain.model.Either
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
class ResultResponseWrapperAdapter<T>(
    private val type: Type
) : CallAdapter<ResponseWrapper<T>, Call<Either<ResponseWrapper<T>>>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<ResponseWrapper<T>>): Call<Either<ResponseWrapper<T>>> =
        ResultResponseWrapperCall(call)
}