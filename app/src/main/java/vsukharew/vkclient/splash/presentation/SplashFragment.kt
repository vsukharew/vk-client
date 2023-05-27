package vsukharew.vkclient.splash.presentation

import android.os.Bundle
import android.view.View
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentSplashBinding
import vsukharew.vkclient.splash.di.SplashScopeCreator

class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {
    private val coordinator: SplashCoordinator by inject()

    override val viewModel: SplashViewModel by viewModel()
    override val scopeCreator: ScopeCreator = SplashScopeCreator
    override val binding by fragmentViewBinding(FragmentSplashBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coordinator.navController = navController
        observeData()
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.isAuthorized
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest(coordinator::openNextScreen)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coordinator.navController = null
    }
}