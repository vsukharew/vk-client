package vsukharew.vkclient.common.navigation

import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController

/**
 * Observer for fragments that are added in back stack
 */
object BackStackEntryObserver {
    fun addObserver(navController: NavController, @IdRes id: Int, onResumeBlock: () -> Unit = {}) {
        navController.getBackStackEntry(id)
            .lifecycle
            .addObserver(
                LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> onResumeBlock.invoke()
                        else -> {

                        }
                    }
                }
            )
    }
}