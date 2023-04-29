package vsukharew.vkclient.common.network.response

import com.google.gson.annotations.SerializedName
import java.net.HttpURLConnection

const val DEFAULT_STRING = ""
const val DEFAULT_INT = -1
val DEFAULT_PARAMS = emptyList<ErrorResponse.RequestParam>()

/**
 * This wrapper needs to combine the data and error because the server always sends
 * [HttpURLConnection.HTTP_OK] but different JSONs
 */
data class ResponseWrapper<T>(
    @SerializedName("response") val response: T?,
    @SerializedName("error") val errorResponse: ErrorResponse?
)

data class ErrorResponse(
    @SerializedName("error_code") val errorCode: Int = DEFAULT_INT,
    @SerializedName("error_msg") val errorMsg: String = DEFAULT_STRING,
    @SerializedName("request_params") val requestParams: List<RequestParam> = DEFAULT_PARAMS,
) {
    data class RequestParam(
        @SerializedName("key") val key: String = DEFAULT_STRING,
        @SerializedName("value") val value: String = DEFAULT_STRING,
    )
}
