package vsukharew.vkclient.splash.presentation

import android.os.Bundle
import android.view.View
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentSplashBinding
import vsukharew.vkclient.splash.di.SplashScopeCreator

class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {
    override val viewModel: SplashViewModel by viewModel()
    override val scopeCreator: ScopeCreator = SplashScopeCreator
    override val binding by fragmentViewBinding(FragmentSplashBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.openNextScreen()
    }
}