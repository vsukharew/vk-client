package vsukharew.vkclient.account.domain.interactor

import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.domain.model.Result

interface AccountInteractor {
    suspend fun getProfileInfo(): Result<ProfileInfo>
    suspend fun doesShortNameExist(name: String): Result<Boolean>
}