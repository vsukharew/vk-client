package vsukharew.vkclient.common.domain.model

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 * If it's used as server response, [Left] represents a successful response and [Right] an error
 */
sealed class Either<out L, out R> {
    data class Left<T>(val data: T) : Either<T, Nothing>()
    data class Right<T>(val data: T) : Either<Nothing, T>()
}