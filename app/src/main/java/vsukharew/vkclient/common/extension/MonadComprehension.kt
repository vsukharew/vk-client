package vsukharew.vkclient.common.extension

import vsukharew.vkclient.common.domain.model.AppError
import vsukharew.vkclient.common.domain.model.Either
import vsukharew.vkclient.common.domain.model.Left
import vsukharew.vkclient.common.domain.model.Right

//region Either
class EitherScope<L> {
    lateinit var error: Left<L>

    fun <R> Either<L, R>.bind(): R {
        return when (this) {
            is Right -> data
            is Left -> {
                error = this
                bindLeft()
            }
        }
    }

    fun Left<L>.bindLeft(): Nothing {
        error = this
        throw EitherBindingException()
    }

    operator fun <R> Either<L, R>.not() = bind()
}

inline fun <L, R> sideEffect(block: EitherScope<L>.() -> R): Either<L, R> {
    val binding = EitherScope<L>()
    return try {
        with(binding) { Right(block()) }
    } catch (e: EitherBindingException) {
        binding.error
    }
}

inline fun <R> EitherScope<AppError>.safeNonNull(block: NullableScope.() -> R): R {
    val binding = NullableScope()
    return try {
        with(binding) { block() }
    } catch (e: NullableBindingException) {
        error = Left(AppError.UnknownError(e))
        throw EitherBindingException()
    }
}

class EitherBindingException : Exception()
//endregion

//region nullable
class NullableScope {
    fun <T> T?.bind(): T = this ?: throw NullableBindingException()

    operator fun <T> T?.not() = bind()
}

inline fun <T> nullable(block: NullableScope.() -> T): T? {
    val binding = NullableScope()
    return try {
        with(binding) { block() }
    } catch (e: NullableBindingException) {
        null
    }
}

class NullableBindingException : Exception()
//endregion