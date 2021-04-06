package vsukharew.vkclient.common.network

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("response") val response: T
)
