package vsukharew.vkclient.common.delegation

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * wrapper above [FragmentViewBindingHolder]
 */
fun <T: ViewBinding> fragmentViewBinding(
    viewBinder: (View) -> T
): FragmentViewBindingHolder<T> = FragmentViewBindingHolder(viewBinder)

/**
 * wrapper above [ActivityViewBindingProperty]
 */
fun <T: ViewBinding> activityViewBinding(
    bindingInitializer: (LayoutInflater) -> T
): ActivityViewBindingProperty<T> = ActivityViewBindingProperty(bindingInitializer)

/**
 * The class that holds a reference to [ViewBinding] and syncs it with the [Lifecycle]
 */
abstract class ViewBindingProperty<T: ViewBinding> : LifecycleObserver {
    protected var binding: T? = null
    private var lifecycle: Lifecycle? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyView() {
        lifecycle?.removeObserver(this)
        lifecycle = null
        binding = null
    }

    protected fun registerObserver(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle.also { it.addObserver(this) }
    }
}

/**
 * The delegation property that initializes a [ViewBinding] for an activity layout
 */
class ActivityViewBindingProperty<T: ViewBinding>(
    private val bindingInitializer: (LayoutInflater) -> T
) : ViewBindingProperty<T>(), ReadOnlyProperty<AppCompatActivity, T> {

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        return binding ?: run {
            registerObserver(thisRef.lifecycle)
            bindingInitializer.invoke(thisRef.layoutInflater)
        }
    }
}

/**
 * The delegation property that initializes a [ViewBinding] for an fragment layout
 */
class FragmentViewBindingHolder<T : ViewBinding>(
    private val viewBinder: (View) -> T
) : ViewBindingProperty<T>(), ReadOnlyProperty<Fragment, T> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return binding ?: run {
            registerObserver(thisRef.lifecycle)
            viewBinder.invoke(thisRef.requireView())
        }
    }
}