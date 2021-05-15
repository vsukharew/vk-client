package vsukharew.vkclient.common.domain.interactor

import vsukharew.vkclient.auth.data.AuthRepo
import vsukharew.vkclient.auth.data.AuthStorage

class SessionInteractorImpl(
    private val authRepo: AuthRepo
) : SessionInteractor {

    override suspend fun clearSessionData() {
        authRepo.deleteToken()
    }
}