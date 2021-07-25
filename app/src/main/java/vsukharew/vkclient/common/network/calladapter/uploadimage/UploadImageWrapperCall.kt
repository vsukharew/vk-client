package vsukharew.vkclient.common.network.calladapter.uploadimage

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.network.calladapter.utils.HttpErrorMapper
import vsukharew.vkclient.publishimage.attach.data.model.UploadedImageWrapper
import java.io.IOException
import java.net.HttpURLConnection.*

class UploadImageWrapperCall(
    private val delegate: Call<UploadedImageWrapper>
) : Call<UploadedImageWrapper> {

    override fun clone(): Call<UploadedImageWrapper> = UploadImageWrapperCall(delegate.clone())

    override fun execute(): Response<UploadedImageWrapper> {
        TODO("Not supported")
    }

    override fun enqueue(callback: Callback<UploadedImageWrapper>) {
        delegate.enqueue(ResponseConverter(this, callback))
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    private class ResponseConverter(
        private val resultCall: UploadImageWrapperCall,
        private val callback: Callback<UploadedImageWrapper>
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
            callback: Callback<UploadedImageWrapper>,
            wrapper: UploadedImageWrapper
        ) {
            callback.onResponse(
                resultCall,
                Response.success(wrapper)
            )
        }

        private fun handleUnsuccessfulResponse(
            callback: Callback<UploadedImageWrapper>,
            responseCode: Int,
            wrapper: UploadedImageWrapper
        ) {
            wrapper.apply {
                domainError = HttpErrorMapper.mapError(responseCode, wrapper.errorResponse)
                this.responseCode = responseCode
            }
            callback.onResponse(resultCall, Response.success(wrapper))
        }

        private fun handleOnFailure(callback: Callback<UploadedImageWrapper>, e: Throwable) {
            val wrapper = when (e) {
                is IOException -> Either.Error.NetworkError(e)
                else -> Either.Error.UnknownError(e)
            }.let { error -> UploadedImageWrapper.EMPTY.also { it.domainError = error } }
            callback.onResponse(resultCall, Response.success(wrapper))
        }
    }

    private companion object {
        private val successfulResponseCodesRange = HTTP_OK until HTTP_MULT_CHOICE
        private val clientErrorCodesRange = HTTP_BAD_REQUEST until HTTP_INTERNAL_ERROR
        private val serverErrorCodesRange = HTTP_INTERNAL_ERROR..526
    }
}