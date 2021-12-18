package vsukharew.vkclient.common.domain.model

import vsukharew.vkclient.common.network.response.ErrorResponse
import java.io.IOException

sealed class AppError {

    sealed class HttpError : AppError() {
        sealed class ClientError : HttpError() {
            object UnauthorizedError : ClientError()
            data class OtherClientError(val httpCode: Int) : ClientError()
        }

        data class ServerError(val httpCode: Int, val errorBody: ErrorResponse?) : HttpError()
        data class OtherHttpError(val httpCode: Int) : HttpError()
    }

    sealed class DomainError : AppError() {
        data class LocationNotReceivedError(val e: Throwable) : DomainError()
        object FileTooLargeError : DomainError()
        object ImageResolutionTooLargeError : DomainError()
        object NoPhotosToPostError : DomainError()
    }

    data class NetworkError(val e: IOException) : AppError()
    data class UnknownError(val e: Throwable) : AppError()
}