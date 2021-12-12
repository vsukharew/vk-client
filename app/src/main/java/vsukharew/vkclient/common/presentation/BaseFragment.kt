package vsukharew.vkclient.common.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.scope.Scope
import vsukharew.vkclient.R
import vsukharew.vkclient.common.di.ScopeCreator
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.livedata.SingleLiveEvent

abstract class BaseFragment<V : ViewBinding>(
    @LayoutRes private val layoutResId: Int
) : Fragment(), AndroidScopeComponent {

    protected abstract val scopeCreator: ScopeCreator
    protected abstract val binding: ViewBinding
    protected abstract val viewModel: BaseViewModel
    override val scope: Scope by lazy { scopeCreator.getScope(this, getKoin()).value }

    private val errorHandler: ErrorHandler by inject()

    open val navController by lazy {
        (requireActivity().supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment).navController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutResId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.errorLiveData.observe(viewLifecycleOwner, ::observeError)
    }

    override fun onStop() {
        super.onStop()
        if (requireActivity().isFinishing) {
            errorHandler.cancelCoroutineScope()
        }
    }

    protected fun <T> handleError(error: Either.Right<T>) {
        errorHandler.handleError(this, error)
    }

    private fun <T> observeError(event: SingleLiveEvent<Either.Right<T>>) {
        event.getContentIfNotHandled()?.let(::handleError)
    }
}