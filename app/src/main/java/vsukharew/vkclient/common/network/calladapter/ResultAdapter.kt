package vsukharew.vkclient.common.network.calladapter

import retrofit2.Call
import retrofit2.CallAdapter
import vsukharew.vkclient.common.domain.model.Result
import java.lang.reflect.Type

class ResultAdapter<T>(private val type: Type) : CallAdapter<T, Call<Result<T>>> {

    override fun responseType(): Type = type

    override fun adapt(call: Call<T>): Call<Result<T>> =
        ResultCall(call)
}