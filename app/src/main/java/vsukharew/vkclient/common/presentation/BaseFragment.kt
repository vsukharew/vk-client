package vsukharew.vkclient.common.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import vsukharew.vkclient.R
import vsukharew.vkclient.common.di.SURVIVE_CONFIG_CHANGES_SCOPE

abstract class BaseFragment<V : ViewBinding>(@LayoutRes private val layoutResId: Int) : Fragment() {

    protected abstract val binding: ViewBinding
    protected val scope: Scope = getKoin().getOrCreateScope(
        this::class.java.name,
        named(SURVIVE_CONFIG_CHANGES_SCOPE)
    )

    val navController by lazy {
        (requireActivity().supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment).navController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutResId, container, false)

    override fun onDestroy() {
        super.onDestroy()
        activity?.apply {
            if (isFinishing && !isChangingConfigurations) {
                scope.close()
            }
        } ?: scope.close()
    }
}