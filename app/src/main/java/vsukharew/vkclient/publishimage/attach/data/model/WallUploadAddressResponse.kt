package vsukharew.vkclient.publishimage.attach.data.model

import com.google.gson.annotations.SerializedName
import vsukharew.vkclient.common.network.response.DEFAULT_STRING

data class WallUploadAddressResponse(@SerializedName("upload_url") val uploadUrl: String = DEFAULT_STRING)