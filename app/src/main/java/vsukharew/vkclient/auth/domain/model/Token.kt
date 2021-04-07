package vsukharew.vkclient.auth.domain.model

data class Token(
    val accessToken: String,
    val expiresIn: Long
) {
    constructor(params: Map<String, String>) : this (
            params[ACCESS_TOKEN_KEY] ?: INVALID_TOKEN,
            params[EXPIRES_IN_KEY]?.toLong() ?: INVALID_EXPIRES_IN
    )

    companion object {
        const val EXPIRES_IN_KEY = "expires_in"
        const val ACCESS_TOKEN_KEY = "access_token"

        const val INVALID_TOKEN = ""
        const val INVALID_EXPIRES_IN = Long.MIN_VALUE
    }
}