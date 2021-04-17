package vsukharew.vkclient.common.presentation

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding

abstract class BaseFlowFragment<V : ViewBinding>(
    @LayoutRes private val layoutResId: Int
) : BaseFragment<V>(layoutResId) {

    @get:IdRes
    protected abstract val fragmentContainerViewId: Int

    val flowNavController: NavController by lazy {
        (childFragmentManager.findFragmentById(fragmentContainerViewId)
            as NavHostFragment).navController
    }
}