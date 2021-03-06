package vsukharew.vkclient.common.presentation

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import vsukharew.vkclient.R

abstract class BaseFlowFragment<V : ViewBinding>(
    @LayoutRes private val layoutResId: Int
) : BaseFragment<V>(layoutResId) {

    @get:IdRes
    protected abstract val fragmentContainerViewId: Int

    val flowNavController: NavController by lazy {
        (childFragmentManager.findFragmentById(fragmentContainerViewId)
            as NavHostFragment).navController
    }

    protected fun navigateIfDestinationIsNotCreated(@IdRes destination: Int) {
        with(flowNavController) {
            if (currentDestination == null || currentDestination!!.id == graph.startDestination) {
                navigate(R.id.attachImageFragment)
            }
        }
    }
}