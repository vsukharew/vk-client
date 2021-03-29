package vsukharew.vkclient.common.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import vsukharew.vkclient.R

abstract class BaseFragment<V : ViewBinding>(@LayoutRes private val layoutResId: Int) : Fragment() {

    protected abstract val binding: ViewBinding

    protected val navController by lazy {
        (requireActivity().supportFragmentManager
                .findFragmentById(R.id.fragment_container_view) as NavHostFragment).navController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutResId, container, false)
}