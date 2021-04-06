package vsukharew.vkclient.account.data

import vsukharew.vkclient.account.data.network.AccountApi
import vsukharew.vkclient.account.domain.model.ProfileInfo

class AccountRepository(private val accountApi: AccountApi) : AccountRepo {

    override suspend fun getProfileInfo(): ProfileInfo {
        return accountApi.getProfileInfo().let { ProfileInfo(it.response.firstName, it.response.lastName) }
    }
}