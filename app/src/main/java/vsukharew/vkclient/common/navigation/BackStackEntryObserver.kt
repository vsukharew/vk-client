package vsukharew.vkclient.common.navigation

import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController

/**
 * Observer for fragments that are added in back stack
 */
class BackStackEntryObserver(
    private val onResumeBlock: () -> Unit = {}
) : LifecycleEventObserver {
    fun addObserver(navController: NavController, @IdRes id: Int) {
        navController.getBackStackEntry(id)
            .lifecycle
            .addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> onResumeBlock.invoke()
            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> {

            }
        }
    }
}