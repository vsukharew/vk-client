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
        return sideEffect {
            val wrapper = accountApi.getProfileInfo().bind()
            safeNonNull {
                val profileResponse = wrapper.response.bind()
                profileResponse.run {
                    ProfileInfo(
                        firstName,
                        lastName,
                        screen_name,
                    )
                }
            }
        }
    }

    override suspend fun resolveScreenName(name: String): Either<AppError, ScreenName> {
        return sideEffect {
            val wrapper = accountApi.resolveScreenName(name).bind()
            safeNonNull {
                val response = wrapper.response.bind()
                response.run {
                    when (this) {
                        is ResolvedScreenNameResponse -> ResolvedScreenName
                        else -> UnresolvedScreenName
                    }
                }
            }
        }
    }
}