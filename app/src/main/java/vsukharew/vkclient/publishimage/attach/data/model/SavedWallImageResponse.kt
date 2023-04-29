package vsukharew.vkclient.publishimage.attach.data.model

import com.google.gson.annotations.SerializedName
import vsukharew.vkclient.common.network.response.DEFAULT_INT

data class SavedWallImageResponse(
    @SerializedName("id") val id: Int = DEFAULT_INT,
    @SerializedName("album_id") val albumId: Int = DEFAULT_INT,
    @SerializedName("owner_id") val ownerId: Int = DEFAULT_INT,
)