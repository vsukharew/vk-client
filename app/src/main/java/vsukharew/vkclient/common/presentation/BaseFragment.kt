package vsukharew.vkclient.common.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.scope.Scope
import vsukharew.vkclient.R
import vsukharew.vkclient.common.di.ScopeCreator

abstract class BaseFragment<V : ViewBinding>(
    @LayoutRes private val layoutResId: Int
) : Fragment(), AndroidScopeComponent {

    protected abstract val scopeCreator: ScopeCreator
    protected abstract val binding: ViewBinding
    override val scope: Scope by lazy { scopeCreator.getScope().value }

    val navController by lazy {
        (requireActivity().supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment).navController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutResId, container, false)
}