package vsukharew.vkclient.common.network.response

import com.google.gson.annotations.SerializedName
import java.net.HttpURLConnection

/**
 * This wrapper is needed to combine the data and error because the server always sends
 * [HttpURLConnection.HTTP_OK] but different JSONs
 */
data class ResponseWrapper<T>(
    @SerializedName("response") val response: T?,
    @SerializedName("error") val errorResponse: ErrorResponse?
)

data class ErrorResponse(
    @SerializedName("error_code") val errorCode: Int,
    @SerializedName("error_msg") val errorMsg: String,
    @SerializedName("request_params") val requestParams: List<RequestParam>,
) {
    data class RequestParam(
        @SerializedName("key") val key: String,
        @SerializedName("value") val value: String,
    )
}
