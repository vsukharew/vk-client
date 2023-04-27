package vsukharew.vkclient.common.network.response

import com.google.gson.annotations.SerializedName
import vsukharew.vkclient.common.domain.model.AppError
import java.net.HttpURLConnection

const val DEFAULT_MESSAGE = ""
const val DEFAULT_CODE = -1
const val DEFAULT_KEY = ""
const val DEFAULT_VALUE = ""
val DEFAULT_PARAMS = emptyList<ErrorResponse.RequestParam>()

/**
 * This wrapper needs to combine the data and error because the server always sends
 * [HttpURLConnection.HTTP_OK] but different JSONs
 */
data class ResponseWrapper<T>(
    @SerializedName("response") val response: T?,
    @SerializedName("error") val errorResponse: ErrorResponse? = ErrorResponse.DEFAULT
)

data class ErrorResponse(
    @SerializedName("error_code") val errorCode: Int = DEFAULT_CODE,
    @SerializedName("error_msg") val errorMsg: String = DEFAULT_MESSAGE,
    @SerializedName("request_params") val requestParams: List<RequestParam> = DEFAULT_PARAMS,
) {
    data class RequestParam(
        @SerializedName("key") val key: String = DEFAULT_KEY,
        @SerializedName("value") val value: String = DEFAULT_VALUE,
    )

    companion object {
        val DEFAULT = ErrorResponse(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_PARAMS)
    }
}
