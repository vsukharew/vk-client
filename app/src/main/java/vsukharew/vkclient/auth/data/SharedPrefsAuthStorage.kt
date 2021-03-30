package vsukharew.vkclient.auth.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AesGcmKeyManager
import vsukharew.vkclient.auth.domain.model.Token
import vsukharew.vkclient.auth.domain.model.Token.Companion.ACCESS_TOKEN_KEY
import vsukharew.vkclient.auth.domain.model.Token.Companion.EXPIRES_IN_KEY
import vsukharew.vkclient.auth.domain.model.Token.Companion.INVALID_TOKEN

class SharedPrefsAuthStorage(context: Context) : AuthStorage {
    private val prefs: SharedPreferences = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
    private val aead = KeysetHandle.generateNew(AesGcmKeyManager.aes128GcmTemplate())
        .getPrimitive(Aead::class.java)

    override suspend fun getToken(): Token {
        val accessToken = prefs.getString(ACCESS_TOKEN_KEY, null)
            ?.let { Base64.decode(it, Base64.DEFAULT) }
            ?.let { aead.decrypt(it, null) }
            ?.let { String(it, Charsets.UTF_8) } ?: INVALID_TOKEN
        val expiredIn = prefs.getLong(EXPIRES_IN_KEY, Token.INVALID_EXPIRES_IN)
        return Token(accessToken, expiredIn)
    }

    override suspend fun putToken(token: Token) {
        val encryptedAccessToken = aead.encrypt(
            token.accessToken.toByteArray(),
            null
        ).let { Base64.encodeToString(it, Base64.DEFAULT) }
        prefs.edit()
            .putString(ACCESS_TOKEN_KEY, encryptedAccessToken)
            .putLong(EXPIRES_IN_KEY, token.expiresIn)
            .apply()
    }

    private companion object {
        private const val AUTH_PREFS = "auth_prefs"
    }
}