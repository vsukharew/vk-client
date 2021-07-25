package vsukharew.vkclient.common.extension

import vsukharew.vkclient.common.domain.model.Either

fun <T, R> Either<T>.map(
    dataMapper: ((T) -> R),
): Either<R> {
    return when (this) {
        is Either.Success -> Either.Success(dataMapper.invoke(data))
        is Either.Error -> this
    }
}

suspend fun <T, R> Either<T>.switchMap(
    dataMapper: suspend ((T) -> Either<R>),
): Either<R> {
    return when (this) {
        is Either.Success -> dataMapper.invoke(data)
        is Either.Error -> this
    }
}

fun <T> Either<T>.ifSuccess(block: (T) -> Unit): Either<T> {
    return when (this) {
        is Either.Success -> this.also { block.invoke(data) }
        else -> this
    }
}