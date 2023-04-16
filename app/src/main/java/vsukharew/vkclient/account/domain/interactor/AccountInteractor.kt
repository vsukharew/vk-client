package vsukharew.vkclient.account.domain.interactor

import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either

interface AccountInteractor {
    suspend fun getProfileInfo(): Either<AppError, ProfileInfo>
    suspend fun doesShortNameExist(name: String): Either<AppError, Boolean>
}