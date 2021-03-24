package vsukharew.vkclient.auth.presentation

import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import vsukharew.vkclient.common.extension.fragmentViewBinding
import vsukharew.vkclient.common.presentation.BaseFragment
import vsukharew.vkclient.databinding.FragmentAuthBinding

class AuthFragment : BaseFragment() {

    private lateinit var viewModel: AuthViewModel

    override fun getViewBinding(container: ViewGroup?): ViewBinding = fragmentViewBinding {
        FragmentAuthBinding.inflate(layoutInflater, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance() = AuthFragment()
    }
}