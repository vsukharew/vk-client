package vsukharew.vkclient.publishimage.caption.data.model

import com.google.gson.annotations.SerializedName
import vsukharew.vkclient.common.network.response.DEFAULT_INT

data class PublishedPostResponse(
    @SerializedName("post_id") val postId: Int = DEFAULT_INT
)