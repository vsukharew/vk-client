package vsukharew.vkclient.publishimage.caption.data.model

import com.google.gson.annotations.SerializedName

data class PublishedPostResponse(
    @SerializedName("post_id") val postId: Int
)