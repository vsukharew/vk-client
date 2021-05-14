package vsukharew.vkclient.auth.data.storage

import android.content.Context
import android.security.KeyPairGeneratorSpec
import vsukharew.vkclient.auth.data.AuthStorage
import vsukharew.vkclient.auth.domain.model.AuthType
import vsukharew.vkclient.auth.domain.model.Token
import vsukharew.vkclient.common.utils.AppDirectories
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.Key
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

/**
 * [AuthStorage] that uses RSA keys stored in Android Keystore, encrypts [Token] and saves it
 * into file
 */
class FileAuthStorage(private val context: Context) : AuthStorage {
    private val keyStore = KeyStore.getInstance(KEY_STORE_TYPE)
    private val authDirectoryPath = "${context.cacheDir}/${AppDirectories.AUTH}"
    private val tokenFilePath = "${context.cacheDir}/${AppDirectories.AUTH}/$TOKEN_FILE_NAME"
    private val privateKeyEntry: KeyStore.PrivateKeyEntry
        get() = keyStore.getEntry(KEY_URI, null) as KeyStore.PrivateKeyEntry

    init {
        initKeystore()
    }

    override suspend fun getToken(): Token? {
        return decryptToken()
    }

    override suspend fun putToken(token: Token) {
        encryptToken(token)
    }

    override suspend fun deleteToken() {
        keyStore.deleteEntry(KEY_URI)
        deleteTokenFile()
    }

    override suspend fun getAuthType(): AuthType {
        TODO("Not yet implemented")
    }

    override suspend fun putAuthType(authType: AuthType) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAuthType() {
        TODO("Not yet implemented")
    }

    override suspend fun clearAll() {
        TODO("Not yet implemented")
    }

    @Suppress("DEPRECATION")
    private fun initKeystore() {
        keyStore.load(null)
        // Create new key if needed
        if (!keyStore.containsAlias(KEY_URI)) {
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 25)
            val spec = KeyPairGeneratorSpec.Builder(context)
                .setAlias(KEY_URI)
                .setSubject(X500Principal(X500_CERTIFICATE_NAME))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()
            val generator = KeyPairGenerator.getInstance(KEY_PAIR_ALGORITHM, KEY_STORE_TYPE)
            generator.initialize(spec)
            generator.generateKeyPair()
        }
    }

    private fun encryptToken(token: Token) {
        val publicKey: RSAPublicKey = privateKeyEntry.certificate.publicKey as RSAPublicKey
        val fileOutputStream = FileOutputStream(createTokenFileIfNotExists())
        val cipher = initCipher(Cipher.ENCRYPT_MODE, publicKey)
        val cipherOutputStream = CipherOutputStream(fileOutputStream, cipher)
        cipherOutputStream.use { it.write(token.accessToken.toByteArray(charset("UTF-8"))) }
    }

    private fun decryptToken(): Token? {
        val privateKey: RSAPrivateKey = privateKeyEntry.privateKey as RSAPrivateKey
        val fileInputStream = FileInputStream(createTokenFileIfNotExists())
        val cipher = initCipher(Cipher.DECRYPT_MODE, privateKey)
        val cipherInputStream = CipherInputStream(fileInputStream, cipher)
        val accessToken = cipherInputStream.use { it.readBytes() }
            .let {
                if (it.isEmpty()) {
                    null
                } else {
                    String(it, Charset.defaultCharset())
                }
            }
        return accessToken?.let { Token(it, 0) }
    }

    private fun initCipher(opMode: Int, key: Key): Cipher {
        return Cipher.getInstance(TRANSFORMATION, PROVIDER)
            .apply { init(opMode, key) }
    }

    private fun createTokenFileIfNotExists(): File {
        val authDirectory = File(authDirectoryPath)
        if (!authDirectory.exists()) {
            authDirectory.mkdir()
        }
        return File(tokenFilePath).apply {
            if (!exists()) {
                createNewFile()
            }
        }
    }

    private fun deleteTokenFile() {
        File(tokenFilePath).delete()
    }

    private companion object {
        private const val KEY_URI = "android-keystore://auth_storage_key"
        private const val X500_CERTIFICATE_NAME = "CN = Sukharev"
        private const val KEY_PAIR_ALGORITHM = "RSA"
        private const val KEY_STORE_TYPE = "AndroidKeyStore"
        private const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private const val PROVIDER = "AndroidOpenSSL"
        private const val TOKEN_FILE_NAME = "token"
    }
}