package vsukharew.vkclient.common.viewbinding

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * The class that holds a reference to [ViewBinding] and synces it with the [Lifecycle]
 */
open class ViewBindingHolder<T: ViewBinding>(
    private val bindingInitializer: (LayoutInflater) -> T
) : LifecycleObserver {
    protected var binding: T? = null
    protected var lifecycle: Lifecycle? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyView() {
        lifecycle?.removeObserver(this)
        lifecycle = null
        binding = null
    }

    protected fun registerObserver(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle.also { it.addObserver(this) }
    }

    protected fun invokeBindingInitializer(layoutInflater: LayoutInflater) : T {
        return bindingInitializer.invoke(layoutInflater).also { binding = it }
    }
}

/**
 * The delegation property that initializes a [ViewBinding] for an activity layout
 */
class ActivityViewBindingProperty<T: ViewBinding>(
    bindingInitializer: (LayoutInflater) -> T
) : ViewBindingHolder<T>(bindingInitializer), ReadOnlyProperty<AppCompatActivity, T> {

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        return binding ?: run {
            registerObserver(thisRef.lifecycle)
            invokeBindingInitializer(thisRef.layoutInflater)
        }
    }
}

/**
 * The class that creates a [ViewBinding] for a [Fragment]
 */
class FragmentViewBindingHolder<T : ViewBinding>(
    bindingInitializer: (LayoutInflater) -> T
) : ViewBindingHolder<T>(bindingInitializer) {

    fun getViewBinding(fragment: Fragment): T {
        return binding ?: run {
            registerObserver(fragment.lifecycle)
            invokeBindingInitializer(fragment.layoutInflater)
        }
    }
}
