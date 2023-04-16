package vsukharew.vkclient.account.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import vsukharew.vkclient.account.data.model.ProfileInfoResponse
import vsukharew.vkclient.account.data.model.ScreenNameResponse
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.network.ServerUrls.Account.GET_PROFILE
import vsukharew.vkclient.common.network.ServerUrls.Account.RESOLVE_SCREEN_NAME
import vsukharew.vkclient.common.network.response.ResponseWrapper

interface AccountApi {

    @GET(GET_PROFILE)
    suspend fun getProfileInfo(): Either<AppError, ResponseWrapper<ProfileInfoResponse>>

    @GET(RESOLVE_SCREEN_NAME)
    suspend fun resolveScreenName(
        @Query("screen_name") name: String
    ): Either<AppError, ResponseWrapper<ScreenNameResponse>>
}