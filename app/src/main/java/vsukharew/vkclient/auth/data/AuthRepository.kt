package vsukharew.vkclient.auth.data

import vsukharew.vkclient.auth.domain.model.AuthType
import vsukharew.vkclient.auth.domain.model.Token

class AuthRepository(private val authStorage: AuthStorage) : AuthRepo {

    override suspend fun getToken(): Token? {
        return authStorage.getToken()
    }

    override suspend fun putToken(token: Token) {
        authStorage.putToken(token)
    }

    override suspend fun getAuthType(): AuthType {
        return authStorage.getAuthType()
    }

    override suspend fun putAuthType(authType: AuthType) {
        authStorage.putAuthType(authType)
    }
}