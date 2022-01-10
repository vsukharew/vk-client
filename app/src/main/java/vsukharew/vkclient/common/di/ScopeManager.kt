package vsukharew.vkclient.common.di

import org.koin.core.Koin
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

class ScopeManager(private val koin: Koin) {
    private val scopes = mutableMapOf<ScopeID, Scope>()

    private fun getOrCreateScope(scopeId: ScopeID, scopeQualifier: Qualifier): Scope {
        return scopes[scopeId] ?: koin.getOrCreateScope(scopeId, scopeQualifier)
            .also { scopes[scopeId] = it }
    }

    private fun closeScope(scopeId: ScopeID) {
        scopes[scopeId]?.close()
    }

    fun createAuthDataScope(): Scope =
        getOrCreateScope(DIScopes.AUTH_DATA.toString(), named(DIScopes.AUTH_DATA))

    fun createAccountScope(): Scope =
        getOrCreateScope(DIScopes.ACCOUNT.toString(), named(DIScopes.ACCOUNT))

    fun createPublishingPostScope(): Scope =
        getOrCreateScope(
            DIScopes.PUBLISHING_POST_DATA.toString(),
            named(DIScopes.PUBLISHING_POST_DATA)
        )
}