package vsukharew.vkclient.publishimage.attach.data.network

import retrofit2.http.POST
import retrofit2.http.Query
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.network.ServerUrls.Wall.POST_TO_USER_WALL
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.publishimage.caption.data.model.PublishedPostResponse

interface WallApi {
    @POST(POST_TO_USER_WALL)
    suspend fun postToWall(
        @Query("message") message: String,
        @Query("attachments") attachments: String,
        @Query("lat") latitude: Double? = null,
        @Query("long") longitude: Double? = null
    ): Result<ResponseWrapper<PublishedPostResponse>>
}