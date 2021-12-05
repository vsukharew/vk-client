package vsukharew.vkclient.features

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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