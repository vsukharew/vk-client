package vsukharew.vkclient.common.extension

import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.scope.ScopeHandlerViewModel
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.scope.createScope

fun Fragment.toast(@StringRes text: Int) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
fun Fragment.toast(text: String) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
fun Fragment.snackBar(text: String) {
    val view = activity?.findViewById<View>(android.R.id.content)
    view?.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
}
fun Fragment.snackBar(@StringRes text: Int) {
    val view = activity?.findViewById<View>(android.R.id.content)
    view?.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
}
fun Fragment.snackBarLong(
    @StringRes text: Int,
    @StringRes actionText: Int? = null,
    action: () -> Unit = {}
) {
    view?.let { view ->
        Snackbar.make(view, text, 5000)
            .apply {
                actionText?.let { setAction(it) { action.invoke() } }
            }
            .show()
    }
}
fun Fragment.snackBarIndefinite(
    @StringRes text: Int,
    @StringRes actionText: Int? = null,
    action: () -> Unit = {}
) {
    view?.let { view ->
        Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
            .apply {
                actionText?.let { setAction(it) { action.invoke() } }
            }
            .show()
    }
}

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