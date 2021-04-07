package vsukharew.vkclient.account.data

import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.domain.model.Result

interface AccountRepo {
    suspend fun getProfileInfo(): Result<ProfileInfo>
}