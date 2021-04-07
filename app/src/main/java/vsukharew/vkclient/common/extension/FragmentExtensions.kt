package vsukharew.vkclient.common.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.toast(@StringRes text: Int) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
fun Fragment.toast(text: String) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()