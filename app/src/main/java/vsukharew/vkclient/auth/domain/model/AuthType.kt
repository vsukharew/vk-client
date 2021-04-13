package vsukharew.vkclient.auth.domain.model

/**
 * By what means user takes authorization
 */
enum class AuthType {
    BROWSER,
    APP,
    UNKNOWN;

    companion object {
        fun getByName(name: String) = values().find { it.name == name } ?: UNKNOWN
    }
}