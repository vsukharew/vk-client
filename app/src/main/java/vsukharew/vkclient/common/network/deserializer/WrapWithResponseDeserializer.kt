package vsukharew.vkclient.common.network.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import vsukharew.vkclient.common.network.response.ErrorResponse
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.publishimage.attach.data.model.UploadImageResponse
import vsukharew.vkclient.common.network.calladapter.responsewrapper.ResultResponseWrapperAdapter
import java.lang.reflect.Type

/**
 * Wraps response json with [ResponseWrapper]
 * so that it is converted correctly with [ResultResponseWrapperAdapter]
 */
class WrapWithResponseDeserializer : JsonDeserializer<ResponseWrapper<UploadImageResponse>> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ResponseWrapper<UploadImageResponse> {
        return if (json?.asJsonObject?.has("error") == true) {
            with(json.asJsonObject) {
                val errorResponse = context.deserialize<ErrorResponse>(this, typeOfT)
                ResponseWrapper(null, errorResponse)
            }
        } else {
            val rootObject = JsonObject()
            rootObject.add("response", json)
            val response = context.deserialize<UploadImageResponse>(rootObject, typeOfT)
            ResponseWrapper(response, null)
        }
    }
}