package vsukharew.vkclient.common.extension

import androidx.fragment.app.Fragment
import org.koin.core.Koin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import vsukharew.vkclient.common.di.DIScopes
import vsukharew.vkclient.common.di.LifecycleScopeManager
import vsukharew.vkclient.common.di.ScopeCreator
import java.util.*

/**
 * Links [parentScopes] to the current screen scope
 * [parentScopes] contain data to define when they must be closed
 * Scope managing is carried out by [LifecycleScopeManager] that observes screen lifecycle
 */
fun Lazy<Scope>.linkParentScopes(
    fragment: Fragment,
    parentScopes: List<ScopeCreator.ScopeData>
): Lazy<Scope> {
    if (parentScopes.isEmpty()) return this
    return apply {
        fragment.lifecycle.addObserver(
            LifecycleScopeManager(
                fragment,
                this,
                parentScopes)
        )
    }
}
