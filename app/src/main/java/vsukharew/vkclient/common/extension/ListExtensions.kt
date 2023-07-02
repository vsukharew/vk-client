package vsukharew.vkclient.common.extension

/**
 * Replaces item found by [predicate] with item passed in place of [with]
 */
inline fun <T> List<T>.replace(predicate: (T) -> Boolean, with: T): List<T> {
    val source = this
    return buildList {
        source.forEach {
            if (predicate.invoke(it)) {
                add(with)
            } else {
                add(it)
            }
        }
    }
}