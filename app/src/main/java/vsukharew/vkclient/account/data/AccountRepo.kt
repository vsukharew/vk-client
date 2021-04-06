package vsukharew.vkclient.account.data

import vsukharew.vkclient.account.domain.model.ProfileInfo

interface AccountRepo {
    suspend fun getProfileInfo(): ProfileInfo
}