package vsukharew.vkclient.common.di

import androidx.fragment.app.Fragment
import org.koin.core.Koin
import org.koin.core.scope.Scope
import vsukharew.vkclient.common.extension.fragmentRetainedScope
import vsukharew.vkclient.common.extension.getOrCreateParentScope
import vsukharew.vkclient.common.extension.linkParentScopes

/**
 * Class that creates current screen scope that is linked to parent ones
 */
abstract class ScopeCreator(
    private val fragment: Fragment,
    private val koin: Koin
) {
    /**
     * Parent scopes data is provided by each screen
     */
    abstract val parentScopes: List<ScopeData>

    fun getScope(): Lazy<Scope> = with(koin) {
        parentScopes.onEach {
            it.scope = getOrCreateParentScope(it.scopeName)
        }.let {
            fragment.fragmentRetainedScope()
                .linkParentScopes(fragment, it)
        }
    }

    data class ScopeData(
        val scopeName: DIScopes,
        val shouldCloseOnBackNavigation: Boolean,
        val shouldCloseOnForwardNavigation: Boolean
    ) {
        /**
         * Scope that will be created by given name
         */
        lateinit var scope: Scope
    }
}
