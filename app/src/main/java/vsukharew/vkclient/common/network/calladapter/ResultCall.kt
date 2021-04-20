package vsukharew.vkclient.common.network.calladapter

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.network.response.ErrorResponse
import vsukharew.vkclient.common.network.response.ResponseWrapper
import java.io.IOException
import java.net.HttpURLConnection.HTTP_OK

class ResultCall<T>(private val delegate: Call<T>) : Call<Result<T>> {

    override fun clone(): Call<Result<T>> = ResultCall(delegate.clone())

    override fun execute(): Response<Result<T>> {
        TODO("Not supported")
    }

    override fun enqueue(callback: Callback<Result<T>>) {
        delegate.enqueue(ResponseConverter(this, callback))
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    /**
     * This class contains converting server responses logic to the client [Result]
     * Server sends [HTTP_OK] in response to each request even if an error occurred ¯\_(ツ)_/¯
     * So one need to use a model kind of [ResponseWrapper] in order to be ready for both successful
     * and unsuccessful server responses.
     *
     * This class, in case of error, takes into account the data from the [ErrorResponse] model
     * and wraps responses in the [Result] model which can represent the concrete HTTP error with
     * the appropriate code and other data
     *
     * What had occurred, success or error, can be determined by type checking the [Result] instance
     *
     * Example:
     * ```
     *      ErrorResponse(
     *              errorCode = [ServerErrorCodes.AUTHORIZATION_FAILED],
     *              errorMsg = "authorization failed",
     *              requestParams = [ ... ]
     *          )
     * ```
     * will be converted to [Result.Error.HttpError.ClientError.UnauthorizedError]
     */
    private class ResponseConverter<T>(
        private val resultCall: ResultCall<T>,
        private val callback: Callback<Result<T>>
    ) : Callback<T> {
        override fun onResponse(
            call: Call<T>,
            response: Response<T>
        ) {
            handleSuccessfulResponse(callback, response.body()!!)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            handleOnFailure(callback, t)
        }

        private fun handleSuccessfulResponse(
            callback: Callback<Result<T>>,
            responseBody: T
        ) {
            callback.onResponse(
                resultCall,
                Response.success(
                    Result.Success(responseBody)
                )
            )
        }

        private fun handleOnFailure(callback: Callback<Result<T>>, e: Throwable) {
            val error = when (e) {
                is IOException -> Result.Error.NetworkError(e)
                else -> Result.Error.UnknownError(e)
            }
            callback.onResponse(resultCall, Response.success(error))
        }
    }
}