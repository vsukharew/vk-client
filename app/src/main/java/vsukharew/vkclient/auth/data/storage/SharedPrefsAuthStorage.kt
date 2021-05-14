package vsukharew.vkclient.auth.data.storage

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.integration.android.AndroidKeystoreKmsClient
import vsukharew.vkclient.auth.data.AuthStorage
import vsukharew.vkclient.auth.domain.model.AuthType
import vsukharew.vkclient.auth.domain.model.Token
import vsukharew.vkclient.auth.domain.model.Token.Companion.ACCESS_TOKEN_KEY
import vsukharew.vkclient.auth.domain.model.Token.Companion.EXPIRES_IN_KEY
import java.nio.charset.Charset

class SharedPrefsAuthStorage(private val context: Context) : AuthStorage {
    private val prefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
    private var aead: Aead? = null
    private val associatedData: ByteArray = byteArrayOf()

    override suspend fun getToken(): Token? {
        generateAeadKey()
        val accessToken = prefs.getString(ACCESS_TOKEN_KEY, null)
            ?.let { Base64.decode(it, Base64.DEFAULT) }
            ?.let { aead?.decrypt(it, associatedData) }
            ?.let { String(it, Charset.defaultCharset()) }
        val expiredIn = prefs.getLong(EXPIRES_IN_KEY, Token.INVALID_EXPIRES_IN)
        return accessToken?.let { Token(it, expiredIn) }
    }

    override suspend fun putToken(token: Token) {
        generateAeadKey()
        val encryptedAccessToken = aead?.encrypt(token.accessToken.toByteArray(), associatedData)
            .let { Base64.encodeToString(it, Base64.DEFAULT) }
        prefs.edit()
            .putString(ACCESS_TOKEN_KEY, encryptedAccessToken)
            .putLong(EXPIRES_IN_KEY, token.expiresIn)
            .apply()
    }

    override suspend fun deleteToken() {
        prefs.edit()
            .remove(ACCESS_TOKEN_KEY)
            .apply()
    }

    override suspend fun getAuthType(): AuthType {
        return prefs.getString(KEY_AUTH_TYPE, AuthType.UNKNOWN.name)?.let {
            AuthType.getByName(it)
        } ?: AuthType.UNKNOWN
    }

    override suspend fun putAuthType(authType: AuthType) {
        prefs.edit()
            .putString(KEY_AUTH_TYPE, authType.name)
            .apply()
    }

    override suspend fun deleteAuthType() {
        prefs.edit()
            .remove(KEY_AUTH_TYPE)
            .apply()
    }


    override suspend fun clearAll() {
        deleteToken()
        deleteAuthType()
    }

    private fun generateAeadKey() {
        aead ?: AndroidKeystoreKmsClient.getOrGenerateNewAeadKey(KEY_URI).also { aead = it }
    }

    private companion object {
        private const val AUTH_PREFS = "auth_prefs"
        private const val KEY_URI = "android-keystore://auth_storage_key"
        private const val KEY_AUTH_TYPE = "auth_type"
    }
}