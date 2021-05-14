package vsukharew.vkclient.auth.data

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.*
import vsukharew.vkclient.auth.domain.model.AuthType
import vsukharew.vkclient.auth.domain.model.Token
import vsukharew.vkclient.auth.data.storage.FileAuthStorage

class AuthRepository(
    private val sharedPrefsStorage: AuthStorage,
    fileStorage: AuthStorage
) : AuthRepo {

    /**
     * Android Keystore that stores data is unavailable before Android M
     * so before Android M [FileAuthStorage] is used as own implementation of secure data storage
     */
    private val storage = if (SDK_INT < M) {
        fileStorage
    } else {
        sharedPrefsStorage
    }

    override suspend fun getToken(): Token? {
        return storage.getToken()
    }

    override suspend fun putToken(token: Token) {
        storage.putToken(token)
    }

    override suspend fun deleteToken() {
        storage.deleteToken()
    }

    override suspend fun getAuthType(): AuthType {
        return sharedPrefsStorage.getAuthType()
    }

    override suspend fun putAuthType(authType: AuthType) {
        sharedPrefsStorage.putAuthType(authType)
    }
}