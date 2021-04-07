package vsukharew.vkclient.common.network.calladapter

import retrofit2.Call
import retrofit2.CallAdapter
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.common.domain.model.Result
import java.lang.reflect.Type

class ResultAdapter<T>(
    private val type: Type
) : CallAdapter<ResponseWrapper<T>, Call<Result<ResponseWrapper<T>>>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<ResponseWrapper<T>>): Call<Result<ResponseWrapper<T>>> =
        ResultCall(call)
}