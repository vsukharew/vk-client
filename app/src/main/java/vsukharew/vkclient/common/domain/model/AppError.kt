package vsukharew.vkclient.common.domain.model

import java.io.IOException

sealed class AppError {

    sealed class RemoteError : AppError() {
        object Unauthorized : RemoteError()
        object UnknownError : RemoteError()
        data class TooMuchRequestsPerSecond(val errorBody: ErrorBody) : RemoteError()
        data class ServerError(val errorBody: ErrorBody) : RemoteError()
        data class ErrorBody(
            val errorCode: Int,
            val errorMsg: String,
            val requestParams: List<RequestParam>,
        ) {
            data class RequestParam(
                val key: String,
                val value: String,
            )
        }
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