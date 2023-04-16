package vsukharew.vkclient.account.data

import vsukharew.vkclient.account.data.model.ScreenNameResponse.ResolvedScreenNameResponse
import vsukharew.vkclient.account.data.network.AccountApi
import vsukharew.vkclient.account.domain.model.ProfileInfo
import vsukharew.vkclient.account.domain.model.ScreenName
import vsukharew.vkclient.account.domain.model.ScreenName.ResolvedScreenName
import vsukharew.vkclient.account.domain.model.ScreenName.UnresolvedScreenName
import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.extension.*

class AccountRepository(private val accountApi: AccountApi) : AccountRepo {

    override suspend fun getProfileInfo(): Either<AppError, ProfileInfo> {
        return accountApi.getProfileInfo()
            .map { wrapper ->
                wrapper.response?.run {
                    ProfileInfo(
                        firstName,
                        lastName,
                        screen_name,
                    )
                } ?: ProfileInfo.EMPTY
            }
    }

    override suspend fun resolveScreenName(name: String): Either<AppError, ScreenName> {
        return accountApi.resolveScreenName(name)
            .map { wrapper ->
                wrapper.response?.let {
                    when (it) {
                        is ResolvedScreenNameResponse -> ResolvedScreenName(
                            it.objectId,
                            it.type
                        )
                        else -> UnresolvedScreenName
                    }
                } ?: ResolvedScreenName(-1, String.EMPTY)
            }
    }
}