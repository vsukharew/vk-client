package vsukharew.vkclient.common.domain.model

import vsukharew.vkclient.common.network.response.ErrorResponse
import java.io.IOException
import java.net.HttpURLConnection

/**
 * The states that response received from the server can be in
 */
sealed class Result<out T> {

    data class Success<T>(val data: T) : Result<T>()

    sealed class Error : Result<Nothing>() {

        sealed class HttpError(open val httpCode: Int) : Error() {
            sealed class ClientError(httpCode: Int) : HttpError(httpCode) {
                object UnauthorizedError : ClientError(HttpURLConnection.HTTP_UNAUTHORIZED)
                data class OtherClientError(override val httpCode: Int) : ClientError(httpCode)
            }

            class ServerError(httpCode: Int, val errorBody: ErrorResponse?) : HttpError(httpCode)
            class OtherHttpError(httpCode: Int) : HttpError(httpCode)
        }

        data class NetworkError(val e: IOException) : Error()
        data class UnknownError(val e: Throwable) : Error()
    }
}