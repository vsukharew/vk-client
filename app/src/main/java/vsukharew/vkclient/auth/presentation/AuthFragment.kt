package vsukharew.vkclient.auth.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.auth.navigation.AuthCoordinator
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.extension.toast
import vsukharew.vkclient.common.network.ServerUrls
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentAuthBinding

class AuthFragment : BaseFragment<FragmentAuthBinding>(R.layout.fragment_auth) {
    private lateinit var vkActivityLauncher: ActivityResultLauncher<Intent>
    private val viewModel: AuthViewModel by sharedViewModel()
    private val coordinator: AuthCoordinator by lazy {
        scope.get<AuthCoordinator>().also { it.navController = navController }
    }

    override val binding by fragmentViewBinding(FragmentAuthBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vkActivityLauncher = registerForActivityResult(
            StartActivityForResult(),
            ::handleVkActivityResult
        )
        coordinator.vkActivityLauncher = vkActivityLauncher
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

    private companion object {
        const val VK_AUTH_ERROR = "error"
    }
}