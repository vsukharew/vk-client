package vsukharew.vkclient.auth.data.model

import vsukharew.vkclient.auth.domain.model.Scope
import vsukharew.vkclient.common.extension.COMMA
import vsukharew.vkclient.common.extension.randomAlphanumericString
import vsukharew.vkclient.common.network.ServerUrls.Auth.BASE_URL
import vsukharew.vkclient.common.network.ServerUrls.Auth.REDIRECT_URL
import kotlin.random.Random

data class AuthParams(
    val baseUrl: String = BASE_URL,
    val clientId: Int = CLIENT_ID,
    val redirectUrl: String = REDIRECT_URL,
    val display: String = DISPLAY,
    val responseType: String = RESPONSE_TYPE,
    val revoke: Boolean = true,
    val scopes: String = Scope.namesLowerCase().joinToString(separator = String.COMMA),
    val state: String = String.randomAlphanumericString(Random(System.currentTimeMillis()).nextInt(11)),
) {
    val completeUrl: String
    get() {
        val scopes = Scope.namesLowerCase().joinToString()
        return "$baseUrl?client_id=$clientId&redirect_uri=$redirectUrl&display=$display&scope=$scopes&response_type=$responseType&state=$state&v=5.90"
    }

    companion object {
        const val CLIENT_ID = 7798005
        const val DISPLAY = "page"
        const val RESPONSE_TYPE = "token"
    }
}
