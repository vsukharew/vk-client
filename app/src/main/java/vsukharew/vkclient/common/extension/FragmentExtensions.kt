package vsukharew.vkclient.common.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.koin.androidx.scope.ScopeHandlerViewModel
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.scope.createScope

fun Fragment.toast(@StringRes text: Int) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
fun Fragment.toast(text: String) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()

/**
 * Creates the scope that survives configuration changes
 * The same principle as in [activityRetainedScope]
 */
fun Fragment.fragmentRetainedScope() = lazy {
    val scopeViewModel = viewModels<ScopeHandlerViewModel>().value
    if (scopeViewModel.scope == null) {
        scopeViewModel.scope = createScope()
    }
    scopeViewModel.scope!!
}