package vsukharew.vkclient.common.domain.model

import vsukharew.vkclient.common.network.response.ErrorResponse
import java.io.IOException

/**
 * The states that response received from the server can be in
 */
sealed class Either<out T> {

    data class Success<T>(val data: T) : Either<T>()

    sealed class Error : Either<Nothing>() {

        sealed class HttpError : Error() {
            sealed class ClientError : HttpError() {
                object UnauthorizedError : ClientError()
                data class OtherClientError(val httpCode: Int) : ClientError()
            }

            data class ServerError(val httpCode: Int, val errorBody: ErrorResponse?) : HttpError()
            data class OtherHttpError(val httpCode: Int) : HttpError()
        }

        sealed class DomainError : Error() {
            data class LocationNotReceivedError(val e: Throwable) : DomainError()
            object FileTooLargeError : DomainError()
            object ImageResolutionTooLargeError : DomainError()
            object NoPhotosToPostError : DomainError()
        }

        data class NetworkError(val e: IOException) : Error()
        data class UnknownError(val e: Throwable) : Error()
    }
}