package vsukharew.vkclient.common.di

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import org.koin.core.scope.Scope

/**
 * Class for linking and closing parent scopes in relation to the current screen scope
 * according the current android lifecycle event
 */
class LifecycleScopeManager(
    private val fragment: Fragment,
    private val currentScreenScope: Lazy<Scope>,
    private val parentScopes: List<ScopeCreator.ScopeData>
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun addOnBackPressedListener() {
        with(fragment.requireActivity().onBackPressedDispatcher) {
            addCallback(fragment, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    isEnabled = false
                    closeScopesInCaseOfBackNavigation(fragment, parentScopes)
                    /**
                     * Stop observing lifecycle so that [closeScopes] isn't fired and
                     * scopes aren't closed twice
                     */
                    removeObserver()
                }
            })
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun linkScopes() {
        parentScopes.map { it.scope }
            .toTypedArray()
            .let { currentScreenScope.value.linkTo(*it) }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun closeScopes() {
        with(parentScopes) {
            // close all scopes if activity destroys completely
            fragment.requireActivity().apply {
                when {
                    // Activity is destroyed completely -> all scopes have to get closed
                    isFinishing -> forEach { it.scope.close() }

                    // Activity is not destroyed ->
                    // only current fragment is destroyed ->
                    // destroyed due to the navigation forward -> close appropriate scopes
                    lifecycle.currentState != Lifecycle.State.DESTROYED -> {
                        forEach {
                            if (it.shouldCloseOnForwardNavigation) {
                                it.scope.close()
                            }
                        }
                    }

                    // Keep all scopes when activity is destroyed but will be recreated
                    // due to the configuration changes
                    else -> {

                    }
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun removeObserver() {
        fragment.lifecycle.removeObserver(this)
    }

    private fun closeScopesInCaseOfBackNavigation(
        fragment: Fragment,
        parentScopes: List<ScopeCreator.ScopeData>
    ) {
        with(fragment) {
            parentScopes.forEach {
                if (it.shouldCloseOnBackNavigation) {
                    it.scope.close()
                }
            }
            requireActivity().onBackPressed()
        }
    }
}