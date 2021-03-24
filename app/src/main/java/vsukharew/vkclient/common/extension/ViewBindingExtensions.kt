package vsukharew.vkclient.common.extension

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import vsukharew.vkclient.common.viewbinding.ActivityViewBindingProperty
import vsukharew.vkclient.common.viewbinding.FragmentViewBindingHolder

/**
 * wrapper above [FragmentViewBindingHolder]
 */
fun <T: ViewBinding> Fragment.fragmentViewBinding(
    bindingInitializer: (LayoutInflater) -> T
): T = FragmentViewBindingHolder(bindingInitializer).getViewBinding(this)

/**
 * wrapper above [ActivityViewBindingProperty]
 */
fun <T: ViewBinding> activityViewBinding(
    bindingInitializer: (LayoutInflater) -> T
): ActivityViewBindingProperty<T> = ActivityViewBindingProperty(bindingInitializer)