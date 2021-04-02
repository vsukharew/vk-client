package vsukharew.vkclient.auth.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import vsukharew.vkclient.R
import vsukharew.vkclient.auth.di.AUTH_SCREEN_SCOPE
import vsukharew.vkclient.auth.navigation.AuthCoordinator
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.extension.toast
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentAuthBinding

class AuthFragment : BaseFragment<FragmentAuthBinding>(R.layout.fragment_auth),
    ChromeTabsResponseListener {
    private lateinit var scope: Scope
    private lateinit var vkActivityLauncher: ActivityResultLauncher<Intent>
    private val viewModel: AuthViewModel by sharedViewModel()
    private val coordinator: AuthCoordinator by lazy {
        getKoin().getOrCreateScope(AUTH_SCREEN_SCOPE, named(AUTH_SCREEN_SCOPE))
            .also { scope = it }
            .get()
    }

    override val binding by fragmentViewBinding(FragmentAuthBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vkActivityLauncher = registerForActivityResult(
            StartActivityForResult(),
            ::handleVkActivityResult
        )
        coordinator.vkActivityLauncher = vkActivityLauncher
        coordinator.navController = navController
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        observeData()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.close()
        coordinator.apply {
            vkActivityLauncher = null
            navController = null
        }
    }

    override fun onResponse(intent: Intent) {
        intent.dataString
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
                } }
            )
        }
    }

    private fun handleVkActivityResult(result: ActivityResult) {
        when {
            result.data?.extras != null -> {
                val response = fetchAuthResponse(result.data!!.extras!!)
                if (response.isNotEmpty() && response[VK_AUTH_ERROR] == null) {
                    viewModel.onLoginSuccess(response)
                } else {
                    toast(R.string.auth_failed_text)
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