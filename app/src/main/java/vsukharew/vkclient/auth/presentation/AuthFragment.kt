package vsukharew.vkclient.auth.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.Koin
import vsukharew.vkclient.R
import vsukharew.vkclient.auth.navigation.AuthCoordinator
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.di.DIScopes
import vsukharew.vkclient.common.extension.toast
import vsukharew.vkclient.common.network.ServerUrls
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentAuthBinding

class AuthFragment : BaseFragment<FragmentAuthBinding>(R.layout.fragment_auth) {
    private lateinit var vkActivityLauncher: ActivityResultLauncher<Intent>
    private val viewModel: AuthViewModel by viewModel()
    private val coordinator: AuthCoordinator by inject()

    override val scopeCreator: ScopeCreator = AuthScopeCreator(this, getKoin())
    override val binding by fragmentViewBinding(FragmentAuthBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vkActivityLauncher = registerForActivityResult(
            StartActivityForResult(),
            ::handleVkActivityResult
        )
        coordinator.let {
            it.navController = navController
            it.vkActivityLauncher = vkActivityLauncher
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        observeData()
        activity?.intent?.let(::handleBrowserRedirect)
    }

    override fun onDestroy() {
        super.onDestroy()
        coordinator.apply {
            vkActivityLauncher = null
            navController = null
        }
    }

    private fun handleBrowserRedirect(intent: Intent) {
        intent.dataString
            ?.takeIf { it.startsWith(ServerUrls.Auth.REDIRECT_URL) }
            ?.split((Regex("[#&]"))) // break url into the host and query parts
            ?.drop(1) // get rid of the host part
            ?.map { it.split("=") /* break each query parameter into the key and value*/ }
            ?.associate { it[0] to it[1] }
            ?.let(viewModel::onLoginSuccess)
            ?.also { intent.data = null }
    }

    private fun setListeners() {
        binding.authLoginBtn.setOnClickListener { viewModel.onLoginClick() }
    }

    private fun observeData() {
        viewModel.apply {
            openBrowserForAuthEvent.observe(viewLifecycleOwner, { event ->
                event.getContentIfNotHandled()?.let {
                    coordinator.onLoginClick(requireContext(), it)
                }
            })
            openFunctionScreenEvent.observe(viewLifecycleOwner, { event ->
                event?.getContentIfNotHandled()?.let {
                    coordinator.openFeaturesScreen()
                }
            }
            )
        }
    }

    private fun handleVkActivityResult(result: ActivityResult) {
        when {
            result.data?.extras != null -> {
                val response = fetchAuthResponse(result.data!!.extras!!)
                when {
                    response.isNotEmpty() -> {
                        val error = response[VK_AUTH_ERROR]
                        if (error != null) {
                            toast(error)
                        } else {
                            viewModel.onLoginSuccess(response)
                        }
                    }
                    else -> {
                        toast(R.string.auth_failed_text)
                    }
                }
            }
            result.resultCode != Activity.RESULT_CANCELED -> {
                toast(R.string.auth_failed_text)
            }
        }
    }

    private fun fetchAuthResponse(bundle: Bundle): Map<String, String> {
        val authResponse = mutableMapOf<String, String>()
        for (key in bundle.keySet()) {
            authResponse[key] = bundle.get(key).toString()
        }
        return authResponse
    }

    private class AuthScopeCreator(
        fragment: Fragment,
        koin: Koin
    ) : ScopeCreator(fragment, koin) {
        override val parentScopes: List<ScopeData> = listOf(
            ScopeData(
                DIScopes.AUTH_DATA,
                shouldCloseOnBackNavigation = true,
                shouldCloseOnForwardNavigation = true
            )
        )
    }

    private companion object {
        const val VK_AUTH_ERROR = "error"
    }
}