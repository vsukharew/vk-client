package vsukharew.vkclient.account.data.network

import retrofit2.http.GET
import vsukharew.vkclient.account.data.model.ProfileInfoResponse
import vsukharew.vkclient.common.network.Response
import vsukharew.vkclient.common.network.ServerUrls.Account.GET_PROFILE

interface AccountApi {

    @GET(GET_PROFILE)
    suspend fun getProfileInfo(): Response<ProfileInfoResponse>
}