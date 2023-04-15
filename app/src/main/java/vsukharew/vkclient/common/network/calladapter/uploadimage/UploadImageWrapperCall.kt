package vsukharew.vkclient.common.network.calladapter.uploadimage

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.network.calladapter.utils.responseErrorToDomainError
import vsukharew.vkclient.common.network.response.ErrorResponse
import vsukharew.vkclient.publishimage.attach.data.model.UploadedImageWrapper
import java.io.IOException

class UploadImageWrapperCall(
    private val delegate: Call<UploadedImageWrapper>
) : Call<Either<UploadedImageWrapper, AppError>> {

    override fun clone(): Call<Either<UploadedImageWrapper, AppError>> = UploadImageWrapperCall(delegate.clone())

    override fun execute(): Response<Either<UploadedImageWrapper, AppError>> {
        TODO("Not supported")
    }

    override fun enqueue(callback: Callback<Either<UploadedImageWrapper, AppError>>) {
        delegate.enqueue(ResponseConverter(this, callback))
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    private class ResponseConverter(
        private val resultCall: UploadImageWrapperCall,
        private val callback: Callback<Either<UploadedImageWrapper, AppError>>
    ) : Callback<UploadedImageWrapper> {

        override fun onResponse(
            call: Call<UploadedImageWrapper>,
            response: Response<UploadedImageWrapper>
        ) {
            val body = response.body()
            // When request had completed successfully response is always non-null
            when {
                body?.server != null -> handleSuccessfulResponse(callback, body)
                body?.errorResponse != null -> handleUnsuccessfulResponse(callback, body.errorResponse)
                else -> handleEmptyResponse(callback)
            }
        }

        override fun onFailure(call: Call<UploadedImageWrapper>, t: Throwable) {
            handleOnFailure(callback, t)
        }

        private fun handleSuccessfulResponse(
            callback: Callback<Either<UploadedImageWrapper, AppError>>,
            wrapper: UploadedImageWrapper
        ) {
            callback.onResponse(
                resultCall,
                Response.success(Either.Left(wrapper))
            )
        }

        private fun handleUnsuccessfulResponse(
            callback: Callback<Either<UploadedImageWrapper, AppError>>,
            response: ErrorResponse
        ) {
            val error = responseErrorToDomainError(response)
            callback.onResponse(resultCall, Response.success(Either.Right(error)))
        }

        private fun handleEmptyResponse(
            callback: Callback<Either<UploadedImageWrapper, AppError>>,
        ) {
            val error = AppError.RemoteError.UnknownError
            callback.onResponse(resultCall, Response.success(Either.Right(error)))
        }

        private fun handleOnFailure(callback: Callback<Either<UploadedImageWrapper, AppError>>, e: Throwable) {
            val error = when (e) {
                is IOException -> AppError.NetworkError(e)
                else -> AppError.UnknownError(e)
            }
            callback.onResponse(resultCall, Response.success(Either.Right(error)))
        }
    }
}