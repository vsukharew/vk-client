package vsukharew.vkclient.common.extension

import vsukharew.vkclient.common.domain.model.Either

fun <L, R, V> Either<L, R>.map(
    dataMapper: ((L) -> V),
): Either<V, R> {
    return when (this) {
        is Either.Left -> Either.Left(dataMapper.invoke(data))
        is Either.Right -> this
    }
}

suspend fun <L, R, V> Either<L, R>.switchMap(
    dataMapper: suspend ((L) -> Either<V, R>),
): Either<V, R> {
    return when (this) {
        is Either.Left -> dataMapper.invoke(data)
        is Either.Right -> this
    }
}

fun <L, R> Either<L, R>.ifSuccess(block: (L) -> Unit): Either<L, R> {
    return when (this) {
        is Either.Left -> this.also { block.invoke(data) }
        else -> this
    }
}