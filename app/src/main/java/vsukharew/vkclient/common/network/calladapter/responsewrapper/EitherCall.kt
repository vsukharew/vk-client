package vsukharew.vkclient.common.network.calladapter.responsewrapper

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.network.calladapter.utils.responseErrorToDomainError
import vsukharew.vkclient.common.network.response.ErrorResponse
import vsukharew.vkclient.common.network.response.ResponseWrapper
import java.io.IOException
import java.net.HttpURLConnection.HTTP_OK

class EitherCall<S>(
    private val delegate: Call<ResponseWrapper<S>>
) : Call<Either<ResponseWrapper<S>, AppError>> {

    override fun clone(): Call<Either<ResponseWrapper<S>, AppError>> =
        EitherCall(delegate.clone())

    override fun execute(): Response<Either<ResponseWrapper<S>, AppError>> {
        TODO("Not supported")
    }

    override fun enqueue(callback: Callback<Either<ResponseWrapper<S>, AppError>>) {
        delegate.enqueue(ResponseConverter(this, callback))
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    /**
     * This class contains converting server responses logic to the client [Either]
     * Server sends [HTTP_OK] in response to each request even if an error occurred ¯\_(ツ)_/¯
     * So one need to use a model kind of [ResponseWrapper] in order to be ready for both successful
     * and unsuccessful server responses.
     *
     * This class, in case of error, takes into account the data from the [ErrorResponse] model
     * and wraps responses in the [Either] model which can represent the concrete HTTP error with
     * the appropriate code and other data
     *
     * What had occurred, success or error, can be determined by type checking the [Either] instance
     *
     * Example:
     * ```
     *      ErrorResponse(
     *              errorCode = [ServerErrorCodes.AUTHORIZATION_FAILED],
     *              errorMsg = "authorization failed",
     *              requestParams = [ ... ]
     *          )
     * ```
     * will be converted to [Either.Right.HttpError.ClientError.UnauthorizedError]
     */
    private class ResponseConverter<S>(
        private val resultCall: EitherCall<S>,
        private val callback: Callback<Either<ResponseWrapper<S>, AppError>>
    ) : Callback<ResponseWrapper<S>> {
        override fun onResponse(
            call: Call<ResponseWrapper<S>>,
            response: Response<ResponseWrapper<S>>
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

        override fun onFailure(call: Call<ResponseWrapper<S>>, t: Throwable) {
            handleOnFailure(callback, t)
        }

        private fun handleSuccessfulResponse(
            callback: Callback<Either<ResponseWrapper<S>, AppError>>,
            responseBody: S
        ) {
            callback.onResponse(
                resultCall,
                Response.success(
                    Either.Left(
                        ResponseWrapper(responseBody, null)
                    )
                )
            )
        }

        private fun handleUnsuccessfulResponse(
            callback: Callback<Either<ResponseWrapper<S>, AppError>>,
            errorBody: ErrorResponse
        ) {
            val error = responseErrorToDomainError(errorBody)
            callback.onResponse(resultCall, Response.success(Either.Right(error)))
        }

        private fun handleEmptyResponse(
            callback: Callback<Either<ResponseWrapper<S>, AppError>>
        ) {
            val error = AppError.RemoteError.UnknownError
            callback.onResponse(resultCall, Response.success(Either.Right(error)))
        }

        private fun handleOnFailure(
            callback: Callback<Either<ResponseWrapper<S>, AppError>>,
            e: Throwable
        ) {
            val error = Either.Right(
                when (e) {
                    is IOException -> AppError.NetworkError(e)
                    else -> AppError.UnknownError(e)
                }
            )
            callback.onResponse(resultCall, Response.success(error))
        }

        private val errorBodyResponseToDomain: (response: ErrorResponse) -> AppError.RemoteError.ErrorBody =
            { response ->
                response.run {
                    AppError.RemoteError.ErrorBody(
                        errorCode,
                        errorMsg,
                        requestParams.map {
                            it.run { AppError.RemoteError.ErrorBody.RequestParam(key, value) }
                        }
                    )
                }
            }
    }
}