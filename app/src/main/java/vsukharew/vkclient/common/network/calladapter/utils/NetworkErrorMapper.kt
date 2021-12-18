package vsukharew.vkclient.common.network.calladapter.utils

import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.AppError.HttpError
import vsukharew.vkclient.common.network.response.ErrorResponse
import vsukharew.vkclient.common.network.response.ServerErrorCodes
import java.net.HttpURLConnection

/**
 * Maps server responses with error to domain errors
 */
object NetworkErrorMapper {

    private val successfulResponseCodesRange =
        HttpURLConnection.HTTP_OK until HttpURLConnection.HTTP_MULT_CHOICE
    private val clientErrorCodesRange =
        HttpURLConnection.HTTP_BAD_REQUEST until HttpURLConnection.HTTP_INTERNAL_ERROR
    private val serverErrorCodesRange = HttpURLConnection.HTTP_INTERNAL_ERROR..526

    fun mapError(responseCode: Int, errorBody: ErrorResponse?): HttpError {
        return when (errorBody?.errorCode) {
            ServerErrorCodes.AUTHORIZATION_FAILED -> HttpError.ClientError.UnauthorizedError
            else -> {
                if (responseCode in successfulResponseCodesRange) {
                    HttpError.ServerError(
                        HttpURLConnection.HTTP_INTERNAL_ERROR,
                        errorBody
                    )
                } else {
                    when (responseCode) {
                        in clientErrorCodesRange -> HttpError.ClientError.OtherClientError(
                            responseCode
                        )
                        in serverErrorCodesRange -> HttpError.ServerError(
                            responseCode,
                            errorBody
                        )
                        else -> HttpError.OtherHttpError(responseCode)
                    }
                }
            }
        }
    }
}