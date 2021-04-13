package vsukharew.vkclient.auth.data

import vsukharew.vkclient.auth.domain.model.Token

interface AuthStorage {
    suspend fun getToken(): Token?
    suspend fun putToken(token: Token)
    suspend fun deleteToken()
}