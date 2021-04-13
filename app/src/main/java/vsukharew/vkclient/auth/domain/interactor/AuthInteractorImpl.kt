package vsukharew.vkclient.auth.domain.interactor

import vsukharew.vkclient.auth.data.AuthRepo
import vsukharew.vkclient.auth.domain.model.AuthType
import vsukharew.vkclient.auth.domain.model.Token

class AuthInteractorImpl(private val repo: AuthRepo) : AuthInteractor {

    override suspend fun isAuthorized(): Boolean {
        return repo.getToken() != null
    }

    override suspend fun saveToken(token: Token) {
        repo.putToken(token)
    }

    override suspend fun getAuthType(): AuthType {
        return repo.getAuthType()
    }

    override suspend fun saveAuthType(authType: AuthType) {
        repo.putAuthType(authType)
    }
}