package vsukharew.vkclient.common.network.calladapter.uploadimage

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.network.calladapter.utils.HttpErrorMapper
import vsukharew.vkclient.publishimage.attach.data.model.UploadedImageWrapper
import java.io.IOException
import java.net.HttpURLConnection.*

class UploadImageWrapperCall(
    private val delegate: Call<UploadedImageWrapper>
) : Call<Result<UploadedImageWrapper>> {

    override fun clone(): Call<Result<UploadedImageWrapper>> = UploadImageWrapperCall(delegate.clone())

    override fun execute(): Response<Result<UploadedImageWrapper>> {
        TODO("Not supported")
    }

    override fun enqueue(callback: Callback<Result<UploadedImageWrapper>>) {
        delegate.enqueue(ResponseConverter(this, callback))
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    private class ResponseConverter(
        private val resultCall: UploadImageWrapperCall,
        private val callback: Callback<Result<UploadedImageWrapper>>
    ) : Callback<UploadedImageWrapper> {

        override fun onResponse(
            call: Call<UploadedImageWrapper>,
            response: Response<UploadedImageWrapper>
        ) {
            val body = response.body()
            // When request had completed successfully response is always non-null
            body?.let {
                body.server?.let {
                    handleSuccessfulResponse(callback, body)
                } ?: handleUnsuccessfulResponse(callback, response.code(), body)
            }
        }

        override fun onFailure(call: Call<UploadedImageWrapper>, t: Throwable) {
            handleOnFailure(callback, t)
        }

        private fun handleSuccessfulResponse(
            callback: Callback<Result<UploadedImageWrapper>>,
            wrapper: UploadedImageWrapper
        ) {
            callback.onResponse(
                resultCall,
                Response.success(Result.Success(wrapper))
            )
        }

        private fun handleUnsuccessfulResponse(
            callback: Callback<Result<UploadedImageWrapper>>,
            responseCode: Int,
            wrapper: UploadedImageWrapper
        ) {
            val error = HttpErrorMapper.mapError(responseCode, wrapper.errorResponse)
            callback.onResponse(resultCall, Response.success(error))
        }

        private fun handleOnFailure(callback: Callback<Result<UploadedImageWrapper>>, e: Throwable) {
            val error = when (e) {
                is IOException -> Result.Error.NetworkError(e)
                else -> Result.Error.UnknownError(e)
            }//.let { error -> UploadedImageWrapper.EMPTY.also { it.domainError = error } }
            callback.onResponse(resultCall, Response.success(error))
        }
    }

    private companion object {
        private val successfulResponseCodesRange = HTTP_OK until HTTP_MULT_CHOICE
        private val clientErrorCodesRange = HTTP_BAD_REQUEST until HTTP_INTERNAL_ERROR
        private val serverErrorCodesRange = HTTP_INTERNAL_ERROR..526
    }
}