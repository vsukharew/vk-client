package vsukharew.vkclient.auth.domain.interactor

import vsukharew.vkclient.auth.data.AuthRepo
import vsukharew.vkclient.auth.domain.model.Token


class AuthInteractorImpl(private val repo: AuthRepo) : AuthInteractor {
    override suspend fun saveToken(token: Token) {
        repo.putToken(token)
    }
}