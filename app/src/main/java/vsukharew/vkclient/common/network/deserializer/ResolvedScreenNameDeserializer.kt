package vsukharew.vkclient.common.network.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import vsukharew.vkclient.account.data.model.ScreenNameResponse
import vsukharew.vkclient.account.data.model.ScreenNameResponse.ResolvedScreenNameResponse
import vsukharew.vkclient.account.data.model.ScreenNameResponse.UnresolvedScreenNameResponse
import java.lang.reflect.Type

class ResolvedScreenNameDeserializer : JsonDeserializer<ScreenNameResponse> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ScreenNameResponse {
        return when (json?.isJsonObject) {
            false -> UnresolvedScreenNameResponse
            else -> {
                val jsonObject = json?.asJsonObject
                if (jsonObject?.has("object_id") == true && jsonObject.has("type")) {
                    ResolvedScreenNameResponse(
                        jsonObject.get("object_id").asInt,
                        jsonObject.get("type").asString
                    )
                } else {
                    UnresolvedScreenNameResponse
                }
            }
        }
    }
}