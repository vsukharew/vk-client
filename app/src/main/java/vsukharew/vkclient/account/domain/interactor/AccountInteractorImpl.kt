package vsukharew.vkclient.account.domain.interactor

import vsukharew.vkclient.account.data.AccountRepo
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.account.domain.model.ScreenName
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.extension.map

class AccountInteractorImpl(
    private val accountRepo: AccountRepo
) : AccountInteractor {

    override suspend fun getProfileInfo(): Either<AppError, ProfileInfo> {
        return accountRepo.getProfileInfo()
    }

    override suspend fun doesShortNameExist(name: String): Either<AppError, Boolean> {
        // todo check profile info and return ScreenNameAvailability
        return accountRepo.resolveScreenName(name).map { it is ScreenName.ResolvedScreenName  }
    }
}