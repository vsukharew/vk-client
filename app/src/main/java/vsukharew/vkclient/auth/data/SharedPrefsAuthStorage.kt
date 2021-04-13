package vsukharew.vkclient.auth.data

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.integration.android.AndroidKeystoreKmsClient
import vsukharew.vkclient.auth.domain.model.Token
import vsukharew.vkclient.auth.domain.model.Token.Companion.ACCESS_TOKEN_KEY
import vsukharew.vkclient.auth.domain.model.Token.Companion.EXPIRES_IN_KEY
import java.nio.charset.Charset

class SharedPrefsAuthStorage(context: Context) : AuthStorage {
    private val prefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
    private val aead = AndroidKeystoreKmsClient.getOrGenerateNewAeadKey(KEY_URI)
    private val associatedData: ByteArray = byteArrayOf()

    override suspend fun getToken(): Token? {
        val accessToken = prefs.getString(ACCESS_TOKEN_KEY, null)
            ?.let { Base64.decode(it, Base64.DEFAULT) }
            ?.let { aead.decrypt(it, associatedData) }
            ?.let { String(it, Charset.defaultCharset()) }
        val expiredIn = prefs.getLong(EXPIRES_IN_KEY, Token.INVALID_EXPIRES_IN)
        return accessToken?.let { Token(it, expiredIn) }
    }

    override suspend fun putToken(token: Token) {
        val encryptedAccessToken = aead.encrypt(token.accessToken.toByteArray(), associatedData)
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

    private companion object {
        private const val AUTH_PREFS = "auth_prefs"
        private const val KEY_URI = "android-keystore://auth_storage_key"
    }
}