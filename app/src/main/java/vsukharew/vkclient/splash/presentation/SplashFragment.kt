package vsukharew.vkclient.splash.presentation

import android.os.Bundle
import android.view.View
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.SURVIVE_CONFIG_CHANGES_SCOPE
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentSplashBinding

class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {
    private lateinit var scope: Scope
    private val viewModel: SplashViewModel by viewModel()
    private val coordinator by lazy {
        getKoin().getOrCreateScope(this::class.java.name, named(SURVIVE_CONFIG_CHANGES_SCOPE))
            .also { scope = it }
            .get<SplashCoordinator>()
            .also { it.navController = navController }
    }

    override val binding by fragmentViewBinding(FragmentSplashBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun observeData() {
        viewModel.isAuthorized.observe(viewLifecycleOwner, coordinator::openNextScreen)
    }

    override fun onDestroy() {
        super.onDestroy()
        coordinator.apply {
            navController = null
        }
        if (!requireActivity().isChangingConfigurations) {
            scope.close()
        }
    }
}