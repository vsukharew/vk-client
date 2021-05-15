package vsukharew.vkclient.account.data

import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.account.domain.model.ScreenName
import vsukharew.vkclient.common.domain.model.Result

interface AccountRepo {
    suspend fun getProfileInfo(): Result<ProfileInfo>
    suspend fun resolveScreenName(name: String): Result<ScreenName>
}