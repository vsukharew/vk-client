package vsukharew.vkclient.common.network.calladapter.responsewrapper

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right
import vsukharew.vkclient.common.network.calladapter.utils.responseErrorToDomainError
import vsukharew.vkclient.common.network.response.ErrorResponse
import vsukharew.vkclient.common.network.response.ResponseWrapper
import java.io.IOException

class EitherCall<T>(
    private val delegate: Call<ResponseWrapper<T>>
) : Call<Either<AppError, ResponseWrapper<T>>> {

    override fun clone(): Call<Either<AppError, ResponseWrapper<T>>> =
        EitherCall(delegate.clone())

    override fun execute(): Response<Either<AppError, ResponseWrapper<T>>> {
        TODO("Not supported")
    }

    override fun enqueue(callback: Callback<Either<AppError, ResponseWrapper<T>>>) {
        delegate.enqueue(ResponseConverter(this, callback))
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    private class ResponseConverter<T>(
        private val resultCall: EitherCall<T>,
        private val callback: Callback<Either<AppError, ResponseWrapper<T>>>
    ) : Callback<ResponseWrapper<T>> {
        override fun onResponse(
            call: Call<ResponseWrapper<T>>,
            response: Response<ResponseWrapper<T>>
        ) {
            val body = response.body()
            when {
                // When request had completed successfully response is always non-null
                body?.response != null -> handleSuccessfulResponse(callback, body.response)

                // So if it has a null value, then response is handled as an unsuccessful one
                body?.errorResponse != null -> handleUnsuccessfulResponse(callback, body.errorResponse)

                else -> handleEmptyResponse(callback)
            }
        }

        override fun onFailure(call: Call<ResponseWrapper<T>>, t: Throwable) {
            handleOnFailure(callback, t)
        }

        private fun handleSuccessfulResponse(
            callback: Callback<Either<AppError, ResponseWrapper<T>>>,
            responseBody: T
        ) {
            callback.onResponse(
                resultCall,
                Response.success(
                    Right(
                        ResponseWrapper(responseBody, null)
                    )
                )
            )
        }

        private fun handleUnsuccessfulResponse(
            callback: Callback<Either<AppError, ResponseWrapper<T>>>,
            errorBody: ErrorResponse
        ) {
            val error = responseErrorToDomainError(errorBody)
            callback.onResponse(resultCall, Response.success(Left(error)))
        }

        private fun handleEmptyResponse(
            callback: Callback<Either<AppError, ResponseWrapper<T>>>
        ) {
            val error = AppError.RemoteError.UnknownError
            callback.onResponse(resultCall, Response.success(Left(error)))
        }

        private fun handleOnFailure(
            callback: Callback<Either<AppError, ResponseWrapper<T>>>,
            e: Throwable
        ) {
            val error = Left(
                when (e) {
                    is IOException -> AppError.NetworkError(e)
                    else -> AppError.UnknownError(e)
                }
            )
            callback.onResponse(resultCall, Response.success(error))
        }
    }
}