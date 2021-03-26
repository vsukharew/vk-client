package vsukharew.vkclient.auth.presentation

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import vsukharew.vkclient.R
import vsukharew.vkclient.common.delegation.fragmentViewBinding
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentAuthBinding

class AuthFragment : BaseFragment<FragmentAuthBinding>(R.layout.fragment_auth) {

    private lateinit var viewModel: AuthViewModel

    override val binding by fragmentViewBinding(FragmentAuthBinding::bind)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance() = AuthFragment()
    }
}