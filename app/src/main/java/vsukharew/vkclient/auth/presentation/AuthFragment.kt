package vsukharew.vkclient.auth.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import vsukharew.vkclient.R
import vsukharew.vkclient.auth.data.model.AuthParams
import vsukharew.vkclient.auth.di.AuthScopeCreator
import vsukharew.vkclient.auth.domain.model.AuthType
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.extension.toast
import vsukharew.vkclient.common.navigation.DeepLinkEndPoint
import vsukharew.vkclient.common.network.ServerUrls
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentAuthBinding

class AuthFragment : BaseFragment<FragmentAuthBinding>(R.layout.fragment_auth) {
    private lateinit var vkActivityLauncher: ActivityResultLauncher<Intent>
    override val viewModel: AuthViewModel by viewModel()
    override val scopeCreator: ScopeCreator = AuthScopeCreator
    override val binding by fragmentViewBinding(FragmentAuthBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vkActivityLauncher = registerForActivityResult(
            StartActivityForResult(),
            ::handleVkActivityResult
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        observeData()
    }

    fun handleBrowserRedirect(intent: Intent) {
        intent.dataString
            ?.takeIf { it.startsWith(ServerUrls.Auth.REDIRECT_URL) }
            ?.split((Regex("[#&]"))) // break url into the host and query parts
            ?.drop(1) // get rid of the host part
            ?.map { it.split("=") /* break each query parameter into the key and value*/ }
            ?.associate { it[0] to it[1] }
            ?.let { viewModel.onLoginSuccess(it, AuthType.BROWSER) }
            ?.also { intent.data = null }
    }

    private fun setListeners() {
        binding.authLoginBtn.setOnClickListener { viewModel.onLoginClick() }
    }

    private fun observeData() {
        viewModel.apply {
            openExternalAuthScreenEvent.observe(viewLifecycleOwner, { event ->
                event.getContentIfNotHandled()?.let(::openVkActivity)
            })
        }
    }

    private fun openVkActivity(authParams: AuthParams) {
        vkActivityLauncher.launch(
            Intent(VK_APP_AUTH_ACTION, null).apply {
                setPackage(VK_APP_PACKAGE_NAME)
                putExtras(
                    Bundle().apply {
                        putInt(VK_EXTRA_CLIENT_ID, authParams.clientId)
                        putBoolean(VK_EXTRA_REVOKE, authParams.revoke)
                        putString(VK_EXTRA_SCOPE, authParams.scopes)
                        putString(VK_EXTRA_REDIRECT_URL, authParams.redirectUrl)
                    }
                )
            }
        )
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
                            viewModel.onLoginSuccess(response, AuthType.APP)
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
        private const val VK_APP_PACKAGE_NAME = "com.vkontakte.android"
        private const val VK_APP_AUTH_ACTION = "com.vkontakte.android.action.SDK_AUTH"
        private const val VK_EXTRA_CLIENT_ID = "client_id"
        private const val VK_EXTRA_SCOPE = "scope"
        private const val VK_EXTRA_REVOKE = "revoke"
        private const val VK_EXTRA_REDIRECT_URL = "redirect_url"
    }
}