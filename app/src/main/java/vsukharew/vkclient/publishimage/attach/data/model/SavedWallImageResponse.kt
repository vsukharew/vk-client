package vsukharew.vkclient.publishimage.attach.data.model

import com.google.gson.annotations.SerializedName

data class SavedWallImageResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("album_id") val albumId: Int,
    @SerializedName("owner_id") val ownerId: Int,
)