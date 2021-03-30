package vsukharew.vkclient.auth.data

import vsukharew.vkclient.auth.domain.model.Token

class AuthRepository(private val authStorage: AuthStorage) : AuthRepo {

    override suspend fun getToken(): Token {
        return authStorage.getToken()
    }

    override suspend fun putToken(token: Token) {
        authStorage.putToken(token)
    }
}