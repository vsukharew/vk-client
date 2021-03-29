package vsukharew.vkclient.auth.data.model

import vsukharew.vkclient.auth.domain.model.Scope
import vsukharew.vkclient.common.extension.COMMA
import vsukharew.vkclient.common.extension.EMPTY
import vsukharew.vkclient.common.extension.randomAlphanumericString

data class AuthParams(
    val baseUrl: String = "https://oauth.vk.com/authorize",
    val clientId: Int = 7798005,
    val redirectUrl: String = "https://oauth.vk.com/blank.html",
    val display: String = "page",
    val responseType: String = "token",
    val revoke: Boolean = true,
    val scopes: String = Scope.namesLowerCase().joinToString(separator = String.COMMA),
    val state: String = String.randomAlphanumericString(10),
) {
    val completeUrl: String
    get() {
        val scopes = Scope.namesLowerCase().joinToString(separator = String.EMPTY) { "&scope=$it" }
        return "$baseUrl?client_id=$clientId&redirect_uri=$redirectUrl&display=$display$scopes&response_type=$responseType&state=$state&v=5.90"
    }
}
