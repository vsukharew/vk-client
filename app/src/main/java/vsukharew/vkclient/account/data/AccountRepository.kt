package vsukharew.vkclient.account.data

import vsukharew.vkclient.account.data.network.AccountApi
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.extension.EMPTY
import vsukharew.vkclient.common.extension.map
import vsukharew.vkclient.common.domain.model.Result

class AccountRepository(private val accountApi: AccountApi) : AccountRepo {

    override suspend fun getProfileInfo(): Result<ProfileInfo> {
        return accountApi.getProfileInfo()
            .map {
                ProfileInfo(
                    it.response?.firstName ?: String.EMPTY,
                    it.response?.lastName ?: String.EMPTY
                )
            }
    }
}