package vsukharew.vkclient.splash.presentation

import android.os.Bundle
import android.view.View
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeManager
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentSplashBinding

class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {
    private val coordinator: SplashCoordinator by inject { parametersOf(navController) }

    override val viewModel: SplashViewModel by viewModel()
    override val parentScopes: ScopeManager.() -> Array<Scope> = { arrayOf(createAuthDataScope()) }
    override val binding by fragmentViewBinding(FragmentSplashBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun observeData() {
        viewModel.isAuthorized.observe(viewLifecycleOwner, coordinator::openNextScreen)
    }
}