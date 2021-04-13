package vsukharew.vkclient.common.domain.interactor

import vsukharew.vkclient.auth.data.AuthStorage

class SessionInteractorImpl(
    private val authStorage: AuthStorage
) : SessionInteractor {

    override suspend fun clearSessionData() {
        authStorage.deleteToken()
    }
}