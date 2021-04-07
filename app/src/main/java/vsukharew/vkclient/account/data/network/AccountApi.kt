package vsukharew.vkclient.account.data.network

import retrofit2.http.GET
import vsukharew.vkclient.account.data.model.ProfileInfoResponse
import vsukharew.vkclient.common.network.response.ResponseWrapper
import vsukharew.vkclient.common.domain.model.Result
import vsukharew.vkclient.common.network.ServerUrls.Account.GET_PROFILE

interface AccountApi {

    @GET(GET_PROFILE)
    suspend fun getProfileInfo(): Result<ResponseWrapper<ProfileInfoResponse>>
}