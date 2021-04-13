package vsukharew.vkclient.auth.domain.interactor

import vsukharew.vkclient.auth.domain.model.AuthType
import vsukharew.vkclient.auth.domain.model.Token

interface AuthInteractor {
    suspend fun isAuthorized(): Boolean
    suspend fun saveToken(token: Token)
    suspend fun getAuthType(): AuthType
    suspend fun saveAuthType(authType: AuthType)
}