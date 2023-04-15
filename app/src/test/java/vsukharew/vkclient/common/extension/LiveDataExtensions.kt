package vsukharew.vkclient.common.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeForTesting(
    observer: Observer<T>,
    block: () -> Unit
) {
    try {
        observeForever(observer)
        block()
    } finally {
        removeObserver(observer)
    }
}