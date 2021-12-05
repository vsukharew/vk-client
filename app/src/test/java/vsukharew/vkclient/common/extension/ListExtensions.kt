package vsukharew.vkclient.common.extension

inline fun <T, reified R : T> List<T>.findAndCast(): R? = find { it is R } as R?