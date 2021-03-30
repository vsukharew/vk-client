package vsukharew.vkclient.auth.data

import vsukharew.vkclient.auth.domain.model.Token

interface AuthRepo {
    suspend fun getToken(): Token?
    suspend fun putToken(token: Token)
}