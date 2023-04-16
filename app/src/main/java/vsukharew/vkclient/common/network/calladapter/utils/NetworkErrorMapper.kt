package vsukharew.vkclient.common.network.calladapter.utils

import vsukharew.vkclient.common.domain.model.AppError.RemoteError
import vsukharew.vkclient.common.domain.model.AppError.RemoteError.*
import vsukharew.vkclient.common.network.response.ErrorResponse

/**
 * Maps server responses with error to domain errors
 */
private const val UNKNOWN_ERROR = 1
private const val UNAUTHORIZED = 5
private const val TOO_MUCH_REQUESTS_PER_SECOND = 6
private const val INTERNAL_SERVER_ERROR = 10

private val responseErrorBodyToDomain: (response: ErrorResponse) -> ErrorBody =
    { response ->
        response.run {
            ErrorBody(
                errorCode,
                errorMsg,
                requestParams.map { ErrorBody.RequestParam(it.key, it.value) })
        }
    }

val responseErrorToDomainError: (response: ErrorResponse) -> RemoteError =
    {
        when (it.errorCode) {
            UNKNOWN_ERROR -> UnknownError
            UNAUTHORIZED -> Unauthorized
            TOO_MUCH_REQUESTS_PER_SECOND -> TooMuchRequestsPerSecond(responseErrorBodyToDomain(it))
            INTERNAL_SERVER_ERROR -> ServerError(responseErrorBodyToDomain(it))
            else -> UnknownError
        }
    }
