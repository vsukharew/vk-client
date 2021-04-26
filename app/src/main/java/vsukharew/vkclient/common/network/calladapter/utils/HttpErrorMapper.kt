package vsukharew.vkclient.common.network.calladapter.utils

import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.network.response.ErrorResponse
import vsukharew.vkclient.common.network.response.ServerErrorCodes
import java.net.HttpURLConnection

/**
 * Maps server responses with error to domain errors
 */
object HttpErrorMapper {

    private val successfulResponseCodesRange =
        HttpURLConnection.HTTP_OK until HttpURLConnection.HTTP_MULT_CHOICE
    private val clientErrorCodesRange =
        HttpURLConnection.HTTP_BAD_REQUEST until HttpURLConnection.HTTP_INTERNAL_ERROR
    private val serverErrorCodesRange = HttpURLConnection.HTTP_INTERNAL_ERROR..526

    fun mapError(responseCode: Int, errorBody: ErrorResponse?): Result.Error {
        return when (errorBody?.errorCode) {
            ServerErrorCodes.AUTHORIZATION_FAILED -> Result.Error.HttpError.ClientError.UnauthorizedError
            else -> {
                if (responseCode in successfulResponseCodesRange) {
                    Result.Error.HttpError.ServerError(
                        HttpURLConnection.HTTP_INTERNAL_ERROR,
                        errorBody
                    )
                } else {
                    when (responseCode) {
                        in clientErrorCodesRange -> Result.Error.HttpError.ClientError.OtherClientError(
                            responseCode
                        )
                        in serverErrorCodesRange -> Result.Error.HttpError.ServerError(
                            responseCode,
                            errorBody
                        )
                        else -> Result.Error.HttpError.OtherHttpError(responseCode)
                    }
                }
            }
        }
    }
}