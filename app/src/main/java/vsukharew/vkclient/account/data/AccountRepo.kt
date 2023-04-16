package vsukharew.vkclient.account.data

import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.account.domain.model.ScreenName
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either

interface AccountRepo {
    suspend fun getProfileInfo(): Either<AppError, ProfileInfo>
    suspend fun resolveScreenName(name: String): Either<AppError, ScreenName>
}